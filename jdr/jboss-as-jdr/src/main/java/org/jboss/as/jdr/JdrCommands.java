package org.jboss.as.jdr;

import static org.jboss.as.jdr.JdrLogger.ROOT_LOGGER;

import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.jar.JarFile;
import java.util.zip.ZipException;
import java.util.zip.ZipEntry;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

import org.jboss.dmr.ModelNode;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DelegateFileFilter;

class CopyDir extends JdrCommand {

    FileFilter filter;
    FileFilter blacklistFilter = new BlackListFilter();
    LinkedList<Sanitizer> sanitizers = new LinkedList<Sanitizer>();

    public CopyDir(FileFilter filter) {
        this.filter = filter;
    }

    public CopyDir(String pattern) {
        this.filter = new WildcardPathFilter(pattern);
    }

    public CopyDir blacklist(FileFilter blacklistFilter) {
        this.blacklistFilter = blacklistFilter;
        return this;
    }

    public CopyDir sanitizer(Sanitizer sanitizer) {
        this.sanitizers.add(sanitizer);
        return this;
    }

    public void execute() throws Exception {
        Collection<File> matches = Find.walk(
            new File(this.env.jbossHome),
            new AndFileFilter(
                new DelegateFileFilter(this.filter),
                new DelegateFileFilter(this.blacklistFilter)
            )
        );
        for( File f : matches ) {
            InputStream stream = new FileInputStream(f);
            for (Sanitizer s : this.sanitizers) {
                stream = s.sanitize(stream);
            }
            this.env.zip.add(f, stream);
        }
    }
}

class JarCheck extends JdrCommand {

    StringBuilder buffer;

    public void execute() throws Exception {
        this.buffer = new StringBuilder();
        walk(new java.io.File(this.env.jbossHome));
        this.env.zip.add(this.buffer.toString(), "jarcheck.txt");
    }

    private void walk(File root) throws NoSuchAlgorithmException {
        for(File f : root.listFiles()) {
            if(f.isDirectory()) {
                walk(f);
            }
            else {
                check(f);
            }
        }
    }

    private void check(File f) throws NoSuchAlgorithmException {
        try {
            MessageDigest alg = MessageDigest.getInstance("md5");
            JarFile jf = new JarFile(f);
            FileInputStream fis = new FileInputStream(f);
            byte [] buffer = new byte[(int) f.length()];
            fis.read(buffer);
            alg.update(buffer);
            String sum = new BigInteger(1, alg.digest()).toString(16);
            this.buffer.append(
                    f.getCanonicalPath().replace(this.env.jbossHome, "JBOSSHOME") + "\n"
                    + sum + "\n"
                    + getManifestString(jf) + "===");
        }
        catch( java.util.zip.ZipException ze ) {
            // skip
        }
        catch( java.io.FileNotFoundException fnfe ) {
            ROOT_LOGGER.debug(fnfe);
        }
        catch( java.io.IOException ioe ) {
            ROOT_LOGGER.debug(ioe);
        }
    }

    private String getManifestString(JarFile jf) throws java.io.IOException {
        StringBuilder buffer = new StringBuilder();
        try {
            ZipEntry manifest = jf.getEntry("META-INF/MANIFEST.MF");
            InputStream manifest_is = jf.getInputStream(manifest);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(manifest_is));
            String line = reader.readLine();
            while( line.length() != 0 ) {
                buffer.append(line + "\n");
                line = reader.readLine();
            }
            return buffer.toString();
        } catch (NullPointerException npe) {
            ROOT_LOGGER.tracef("no MANIFEST present");
            return "";
        }
    }
}

class TreeCommand extends JdrCommand {

    public void execute() throws Exception {
        FSTree tree = new FSTree(this.env.jbossHome);
        this.env.zip.add(tree.toString(), "tree.txt");
    }
}

class CallAS7 extends JdrCommand {

    private String operation = "read-resource";
    private LinkedList<String> resource = new LinkedList<String>();
    private Map<String, String> parameters = new HashMap<String, String>();
    private String name;

    public CallAS7(String name) {
        this.name = name + ".json";
    }

    public CallAS7 operation(String operation) {
        this.operation = operation;
        return this;
    }

    public CallAS7 param(String key, String val) {
        this.parameters.put(key, val);
        return this;
    }

    public CallAS7 resource(String... parts) {
        for(String part : parts ) {
            this.resource.add(part);
        }
        return this;
    }

    public void execute() throws Exception {
        ModelNode request = new ModelNode();
        request.get("operation").set(this.operation);

        assert this.resource.size() % 2 == 0;
        while (this.resource.size() > 0) {
            request.get("address").add(this.resource.removeFirst(),
                    this.resource.removeFirst());
        }

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            request.get(entry.getKey()).set(entry.getValue());
        }

        if (this.env.hostControllerName != null) {
            request.get("host").set(this.env.hostControllerName);
        }

        if (this.env.serverName != null) {
            request.get("server").set(this.env.serverName);
        }

        this.env.zip.add(this.env.client.execute(request).toJSONString(true), this.name);
    }
}
