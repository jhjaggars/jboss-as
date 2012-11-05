package org.jboss.as.jdr.resource;

import org.jboss.as.jdr.resource.filter.ResourceFilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * User: csams
 * Date: 11/4/12
 * Time: 2:15 PM
 */
public interface Resource {

    String getPath();
    String getName();
    String getManifest() throws IOException;
    Resource getChild(String path);
    List<Resource> getChildren();
    List<Resource> getChildren(ResourceFilter filter);
    List<Resource> getChildrenRecursively() throws IOException;
    List<Resource> getChildrenRecursively(ResourceFilter filter) throws IOException;
    long getSize();
    boolean isDirectory();
    boolean isFile();
    boolean isSymlink() throws IOException;
    InputStream openStream() throws IOException;

}
