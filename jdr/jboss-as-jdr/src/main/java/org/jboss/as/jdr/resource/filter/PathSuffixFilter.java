package org.jboss.as.jdr.resource.filter;

import org.jboss.as.jdr.resource.Resource;

/**
 * User: csams
 * Date: 11/4/12
 * Time: 11:47 PM
 */
public class PathSuffixFilter implements ResourceFilter {

    private final String suffix;

    public PathSuffixFilter(String suffix){
        this.suffix = suffix;
    }
    @Override
    public boolean accepts(Resource resource) {
        return resource.getPath().endsWith(suffix);
    }
}
