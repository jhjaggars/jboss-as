package org.jboss.as.jdr.resource.factory;

import org.jboss.as.jdr.resource.Resource;
import org.jboss.as.jdr.resource.VFSResource;
import org.jboss.vfs.VFS;

/**
= * User: csams
 * Date: 11/4/12
 * Time: 5:00 PM
 */
public class VFSResourceLoader implements ResourceLoader {
    @Override
    public Resource getResource(String path) {
        return new VFSResource(VFS.getChild(path));
    }
}
