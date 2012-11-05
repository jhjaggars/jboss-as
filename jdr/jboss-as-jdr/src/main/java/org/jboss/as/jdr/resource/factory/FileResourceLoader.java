package org.jboss.as.jdr.resource.factory;

import org.jboss.as.jdr.resource.FileResource;
import org.jboss.as.jdr.resource.Resource;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: csams
 * Date: 11/4/12
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileResourceLoader implements ResourceLoader {
    @Override
    public Resource getResource(String path) {
        return new FileResource(new File(path));
    }
}
