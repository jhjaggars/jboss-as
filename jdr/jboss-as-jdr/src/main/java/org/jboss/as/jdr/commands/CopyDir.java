package org.jboss.as.jdr.commands;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DelegateFileFilter;
import org.jboss.as.jdr.util.BlackListFilter;
import org.jboss.as.jdr.util.Sanitizer;
import org.jboss.as.jdr.util.Find;
import org.jboss.as.jdr.util.WildcardPathFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: jhjaggars
 * Date: 10/18/12
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class CopyDir extends JdrCommand {

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

    @Override
    public void execute() throws Exception {
        Collection<File> matches = Find.walk(
            new File(this.env.getJbossHome()),
            new AndFileFilter(
                new DelegateFileFilter(this.filter),
                new DelegateFileFilter(this.blacklistFilter)
            )
        );
        for( File f : matches ) {
            System.out.println(f.getPath());
            InputStream stream = new FileInputStream(f);
            for (Sanitizer s : this.sanitizers) {
                stream = s.sanitize(stream);
            }
            this.env.getZip().add(f, stream);
        }
    }
}
