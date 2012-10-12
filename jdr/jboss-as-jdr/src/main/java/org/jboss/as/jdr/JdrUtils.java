package org.jboss.as.jdr;

import static org.jboss.as.jdr.JdrLogger.ROOT_LOGGER;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

class JdrZipFile {

    ZipOutputStream zos;
    String jbossHome;
    JdrEnvironment env;
    String name;

    public JdrZipFile(JdrEnvironment env) throws Exception {
        this.env = env;
        this.jbossHome = this.env.jbossHome;
        SimpleDateFormat fmt = new SimpleDateFormat("yy-MM-dd_hh-mm-ss");
        this.name = this.env.outputDirectory +
                    java.io.File.separator +
                    "jdr_" + fmt.format(new Date());

        if (this.env.hostControllerName != null) {
            this.name += "." + this.env.hostControllerName;
        }

        if (this.env.serverName != null) {
            this.name += "_" + this.env.serverName;
        }

        this.name += ".zip";

        zos = new ZipOutputStream(new FileOutputStream(this.name));
    }

    public String name() {
        return this.name;
    }

    public void add(InputStream is, String path) {
        byte [] buffer = new byte[1024];

        try {
            ZipEntry ze = new ZipEntry(path);
            zos.putNextEntry(ze);
            int bytesRead = is.read(buffer);
            while( bytesRead > -1 ) {
                zos.write(buffer);
                bytesRead = is.read(buffer);
            }
        }
        catch (ZipException ze) {
            ROOT_LOGGER.tracef(ze, "%s is already in the zip", path);
        }
        catch (Exception e) {
            ROOT_LOGGER.debugf(e, "Error when adding %s", path);
        }
        finally {
            try {
                zos.closeEntry();
            }
            catch (Exception e) {
                ROOT_LOGGER.debugf(e, "Error when closing entry for %s", path);
            }
        }
    }

    public void add(File file) throws Exception {
        String name = "JBOSS_HOME" + file.getPath().substring(this.jbossHome.length());
        this.add(new FileInputStream(file), name);
    }

    public void add(String content, String path) throws Exception {
        String name = "sos_strings/as7/" + path;
        this.add(new ByteArrayInputStream(content.getBytes()), name);
    }

    public void close() throws Exception {
        this.zos.close();
    }
}

class WildcardPathFilter implements FileFilter {

    String pattern;

    public WildcardPathFilter(String pattern) {
        this.pattern = pattern;
    }

    public boolean accept(File f) {
        return FilenameUtils.wildcardMatch(f.getPath(), this.pattern);
    }

    public String toString() {
        return "<WildcardPathFilter: [" + pattern + "]>";
    }
}

class BlackListFilter implements FileFilter {

    private List<String> patterns = Arrays.asList("*-users.properties");

    public BlackListFilter() {
    }

    public BlackListFilter(List<String> patterns) {
        this.patterns = patterns;
    }

    public boolean accept(File f) {
        for(String pattern : this.patterns) {
            if (FilenameUtils.wildcardMatch(f.getPath(), pattern)) {
                return false;
            }
        }
        return true;
    }
}

class XMLSanitizer {

    XPathExpression expression;

    public XMLSanitizer (String pattern) throws Exception {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        expression = xpath.compile(pattern);
    }

    public InputStream sanitize(InputStream in) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(in);
        Object result = expression.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); i++) {
            System.err.println(nodes.item(i).getNodeValue());
            nodes.item(i).setNodeValue("");
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        StreamResult outStream = new StreamResult(output);
        transformer.transform(source, outStream);
        return new ByteArrayInputStream(output.toByteArray());
    }
}

class Find {

    public static Collection walk(File root) throws Exception {
        return walk(root, TrueFileFilter.TRUE);
    }

    public static Collection walk(File root, String pattern) throws Exception {
        return walk(root, new WildcardPathFilter(pattern));
    }

    public static Collection walk(File root, FileFilter filter) throws Exception {
        ArrayList<File> results = new ArrayList<File>();
        for( File f : root.listFiles() ) {
            if(f.isDirectory()) {
                results.addAll(walk(f, filter));
            }
            else {
                if( filter.accept(f) ) {
                    results.add(f);
                }
            }
        }
        return results;
    }
}

class FSTree {
    int directoryCount = 0;
    int fileCount = 0;
    StringBuilder buf = new StringBuilder();
    String topDirectory = null;
    String fmt = "%s%s%s %s";

    public FSTree(String root) throws Exception {
        this.traverse(root, "", true);
    }

    private static double div(long left, long right) {
        return (double)left / (double)right;
    }

    private static String formatBytes(long size) {

        if (size > FileUtils.ONE_TB) {
            return String.format("%.1fT", div(size, FileUtils.ONE_TB));
        } else if (size > FileUtils.ONE_GB) {
            return String.format("%.1fG", div(size, FileUtils.ONE_GB));
        } else if (size > FileUtils.ONE_MB) {
            return String.format("%.1fM", div(size, FileUtils.ONE_MB));
        } else if (size > FileUtils.ONE_KB) {
            return String.format("%.1fK", div(size, FileUtils.ONE_KB));
        } else {
            return String.format("%d", size);
        }
    }

    public void traverse(String dir, String padding) throws java.io.IOException {
        traverse(dir, padding, false);
    }

    private void append(File f, String padding) {
        String basename = f.getName();
        String size = formatBytes(f.length());
        buf.append(String.format(fmt, padding, "+-- ", size, basename));
        buf.append("\n");
    }

    public void traverse(String dir, String padding, boolean first)
        throws java.io.IOException {
        File path = new java.io.File(dir).getCanonicalFile();

        if (!first) {
            String _p = padding.substring(0, padding.length() -1);
            append(path, _p);
            padding += "   ";
        }
        else {
            buf.append(path.getName());
            buf.append("\n");
        }

        int count = 0;
        File [] files = path.listFiles();
        for (File f : files ) {
            count += 1;

            if (f.getPath().startsWith(".")) {
                continue;
            }
            else if (f.isFile()) {
                append(f, padding);
            }
            else if (FileUtils.isSymlink(f)) {
                buf.append(padding);
                buf.append("+-- ");
                buf.append(f.getName());
                buf.append(" -> ");
                buf.append(f.getCanonicalFile().getPath());
                buf.append("\n");
            }
            else if (f.isDirectory()) {
                if (count == files.length) {
                    traverse(f.getPath(), padding + " ");
                }
                else {
                    traverse(f.getPath(), padding + "|");
                }
            }
        }
    }

    public String toString() {
        return buf.toString();
    }
}
