package org.jboss.as.jdr.resource.filter;

import org.jboss.as.jdr.resource.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: csams
 * Date: 11/4/12
 * Time: 2:30 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ResourceFilter {

    public static ResourceFilter TRUE = new ResourceFilter() {
        @Override
        public boolean accepts(Resource resource) {
            return true;
        }
    };

    public static ResourceFilter FALSE = new NotFilter(TRUE);

    public boolean accepts(Resource resource);
}
