package org.jboss.as.jdr.resource.filter;

import org.jboss.as.jdr.resource.Resource;

/**
 * User: csams@redhat.com
 * Date: 11/4/12
 * Time: 3:13 PM
 */
public class OrFilter implements ResourceFilter {

    private final ResourceFilter[] filters;

    public OrFilter(ResourceFilter... filters){
        this.filters = filters;
    }

    @Override
    public boolean accepts(Resource resource) {
        for(ResourceFilter filter: this.filters){
            if(filter.accepts(resource)){
                return true;
            }
        }
        return false;
    }
}
