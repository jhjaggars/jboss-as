package org.jboss.as.jdr.util;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: jhjaggars
 * Date: 10/18/12
 * Time: 12:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class FSTree {
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
        File path = new File(dir).getCanonicalFile();

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