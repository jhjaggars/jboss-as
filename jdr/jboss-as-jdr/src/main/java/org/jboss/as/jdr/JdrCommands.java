package org.jboss.as.jdr;

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
import java.util.List;
import java.util.LinkedList;

import org.jboss.dmr.ModelNode;

class CopyDir extends JdrCommand {

    FileFilter filter;

    public CopyDir(FileFilter filter) {
        this.filter = filter;
    }

    public CopyDir(String pattern) {
        this.filter = new WildcardPathFilter(pattern);
    }

    public void execute() throws Exception {
        Collection<File> matches = Find.walk(new java.io.File(this.env.jbossHome), this.filter);
        for( File f : matches ) {
            System.out.println( f.getPath() );
        }
    }
}

class JarCheck extends JdrCommand {

    public void execute() throws Exception {
            walk(new java.io.File(this.env.jbossHome));
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
            System.out.println(
                    f.getCanonicalPath().replace(this.env.jbossHome, "JBOSSHOME") + "\n"
                    + sum + "\n"
                    + getManifestString(jf) + "===");
        }
        catch( java.util.zip.ZipException ze ) {
            // skip
        }
        catch( java.io.FileNotFoundException fnfe ) {
            System.err.println(fnfe.toString());
        }
        catch( java.io.IOException ioe ) {
            System.err.println(ioe.toString());
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
            // No MANIFEST
            return "";
        }
    }
}

class TreeCommand extends JdrCommand {

    public void execute() throws Exception {
        FSTree tree = new FSTree(this.env.jbossHome);
        System.out.println(tree.toString());
    }
}

class CallAS7 extends JdrCommand {

    private String operation = "read-resource";
    private LinkedList<String> resource = new LinkedList<String>();
    private Map<String, String> parameters = new HashMap<String, String>();

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
        while(this.resource.size() > 0) {
            request.get("address").add(this.resource.removeFirst(),
                    this.resource.removeFirst());
        }

        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            request.get(entry.getKey()).set(entry.getValue());
        }

        System.out.println(this.env.client.execute(request).toJSONString(true));
    }
}
