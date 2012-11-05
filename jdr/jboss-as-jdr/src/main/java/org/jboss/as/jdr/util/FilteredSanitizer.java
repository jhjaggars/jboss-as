package org.jboss.as.jdr.util;

import org.jboss.as.jdr.resource.Resource;
import org.jboss.as.jdr.resource.filter.ResourceFilter;

import java.io.InputStream;

/**
 * User: csams@redhat.com
 * Date: 11/4/12
 * Time: 11:57 PM
 */
public class FilteredSanitizer implements Sanitizer {

    private final Sanitizer delegate;
    private final ResourceFilter filter;

    public FilteredSanitizer(Sanitizer delegate, ResourceFilter filter){
        this.delegate = delegate;
        this.filter = filter;
    }

    public boolean accepts(Resource resource){
        return filter.accepts(resource);
    }

    @Override
    public InputStream sanitize(InputStream in) throws Exception {
        return delegate.sanitize(in);
    }
}
