package org.jboss.as.jdr.resource.filter;

import org.jboss.as.jdr.resource.Resource;

/**
 * User: csams
 * Date: 11/4/12
 * Time: 2:32 PM
 */
public class AndFilter implements ResourceFilter {

    private final ResourceFilter[] filters;

    public AndFilter(ResourceFilter... filters){
        this.filters = filters;
    }

    @Override
    public boolean accepts(Resource resource) {
        for(ResourceFilter filter: this.filters){
            if(!filter.accepts(resource)){
                return false;
            }
        }
        return true;
    }
}
