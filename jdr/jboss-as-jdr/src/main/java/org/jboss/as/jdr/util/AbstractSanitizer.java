package org.jboss.as.jdr.util;

import org.jboss.as.jdr.resource.Resource;
import org.jboss.as.jdr.resource.filter.ResourceFilter;

import java.io.InputStream;

abstract class AbstractSanitizer implements Sanitizer {

    protected ResourceFilter filter = ResourceFilter.TRUE;

    @Override
    public abstract InputStream sanitize(InputStream in) throws Exception;


    @Override
    public boolean accepts(Resource resource) {
        return filter.accepts(resource);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
