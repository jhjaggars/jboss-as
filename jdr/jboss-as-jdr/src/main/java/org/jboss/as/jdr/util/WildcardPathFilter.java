package org.jboss.as.jdr.util;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileFilter;

/**
 * Created with IntelliJ IDEA.
 * User: jhjaggars
 * Date: 10/18/12
 * Time: 12:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class WildcardPathFilter implements FileFilter {

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
