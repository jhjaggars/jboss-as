package org.jboss.as.jdr.util;

import org.jboss.vfs.VirtualFile;
import org.jboss.vfs.VirtualFileFilter;

import java.io.InputStream;

/**
 * User: csams@redhat.com
 * Date: 11/4/12
 * Time: 11:57 PM
 */
public class FilteredSanitizer implements Sanitizer {

    private final Sanitizer delegate;
    private final VirtualFileFilter filter;

    public FilteredSanitizer(Sanitizer delegate, VirtualFileFilter filter){
        this.delegate = delegate;
        this.filter = filter;
    }

    public boolean accepts(VirtualFile file){
        return filter.accepts(file);
    }

    @Override
    public InputStream sanitize(InputStream in) throws Exception {
        return delegate.sanitize(in);
    }
}
