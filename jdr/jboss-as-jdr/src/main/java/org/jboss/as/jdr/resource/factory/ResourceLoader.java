package org.jboss.as.jdr.resource.factory;

import org.jboss.as.jdr.resource.Resource;

/**
 * User: csams
 * Date: 11/4/12
 * Time: 4:59 PM
 */
public interface ResourceLoader {

    Resource getResource(String path);

}
