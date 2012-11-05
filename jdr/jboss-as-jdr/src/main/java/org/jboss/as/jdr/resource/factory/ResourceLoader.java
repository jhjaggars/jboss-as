package org.jboss.as.jdr.resource.factory;

import org.jboss.as.jdr.resource.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: csams
 * Date: 11/4/12
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ResourceLoader {

    public Resource getResource(String path);

}
