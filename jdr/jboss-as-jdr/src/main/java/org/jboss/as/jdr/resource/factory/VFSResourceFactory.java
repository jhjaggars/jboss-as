package org.jboss.as.jdr.resource.factory;

import org.jboss.as.jdr.resource.Resource;
import org.jboss.as.jdr.resource.VFSResource;
import org.jboss.vfs.VFS;

/**
 * Created with IntelliJ IDEA.
 * User: csams
 * Date: 11/4/12
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class VFSResourceFactory implements ResourceFactory {
    @Override
    public Resource getResource(String path) {
        return new VFSResource(VFS.getChild(path));
    }
}
