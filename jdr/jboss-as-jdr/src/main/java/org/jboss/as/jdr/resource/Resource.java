package org.jboss.as.jdr.resource;

import org.jboss.as.jdr.resource.factory.ResourceFactory;
import org.jboss.as.jdr.resource.factory.VFSResourceFactory;
import org.jboss.as.jdr.resource.filter.ResourceFilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: csams
 * Date: 11/4/12
 * Time: 2:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Resource {

    public String getPath();
    public String getName();
    public String getManifest() throws IOException;
    public List<Resource> getChildren() throws IOException;
    public List<Resource> getChildren(ResourceFilter filter) throws IOException;
    public long getSize();
    public boolean isDirectory();
    public boolean isFile();
    public boolean isSymlink() throws IOException;
    public InputStream openStream() throws IOException;

}
