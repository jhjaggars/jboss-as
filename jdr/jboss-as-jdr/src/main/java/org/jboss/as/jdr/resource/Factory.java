package org.jboss.as.jdr.resource;

import org.jboss.as.jdr.resource.factory.ResourceLoader;
import org.jboss.as.jdr.resource.factory.VFSResourceLoader;

/**
 * Created with IntelliJ IDEA.
 * User: csams
 * Date: 11/4/12
 * Time: 6:06 PM
 * To change this template use File | Settings | File Templates.
 */
public final class Factory {

    private final static ResourceLoader LOADER;

    static {
        LOADER = new VFSResourceLoader();
    }

    public static Resource getResource(String path) {
        return LOADER.getResource(path);
    }
}
