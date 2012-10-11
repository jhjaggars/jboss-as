package org.jboss.as.jdr;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.Date;
import java.text.SimpleDateFormat;

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

    public void add(File file) throws Exception {
        String name = file.getPath().substring(this.jbossHome.length());
        byte [] buffer = new byte[1024];

        try {
            ZipEntry ze = new ZipEntry("JBOSS_HOME" + name);
            ze.setSize(file.length());
            zos.putNextEntry(ze);

            FileInputStream fis = new FileInputStream(file);
            int bytesRead = fis.read(buffer);
            while( bytesRead > -1 ) {
                zos.write(buffer);
                bytesRead = fis.read(buffer);
            }
        }
        catch (ZipException ze) {
            // if this is a dupe we don't care...
        }
        finally {
            zos.closeEntry();
        }
    }

    public void add(String content, String path) throws Exception {
        ZipEntry ze = new ZipEntry("sos_strings/as7/" + path);
        ze.setSize(content.length());
        zos.putNextEntry(ze);
        zos.write(content.getBytes());
        zos.closeEntry();
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

    public boolean accept(File f) {
        for (String p : Arrays.asList(
                "mgmt-users.properties",
                "application-users.properties")) {
            if (f.getPath().endsWith(p) ) {
                return false;
            }
        }
        return true;
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
