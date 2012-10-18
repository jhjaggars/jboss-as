package org.jboss.as.jdr.util;

import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: jhjaggars
 * Date: 10/18/12
 * Time: 12:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class Find {

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
