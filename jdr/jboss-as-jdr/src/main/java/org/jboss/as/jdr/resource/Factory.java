package org.jboss.as.jdr.resource;

import org.jboss.as.jdr.resource.factory.FileResourceLoader;
import org.jboss.as.jdr.resource.factory.ResourceLoader;
import org.jboss.as.jdr.resource.factory.VFSResourceLoader;

/**
 * User: csams
 * Date: 11/4/12
 * Time: 6:06 PM
 */
public final class Factory {

    private static final ResourceLoader LOADER;

    static {
        LOADER = new VFSResourceLoader();
    }

    public static Resource getResource(String path) {
        return LOADER.getResource(path);
    }
}
