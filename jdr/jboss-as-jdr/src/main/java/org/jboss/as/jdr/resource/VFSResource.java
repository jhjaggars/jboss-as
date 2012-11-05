package org.jboss.as.jdr.resource;

import org.jboss.as.jdr.resource.filter.ResourceFilter;
import org.jboss.vfs.VirtualFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: csams
 * Date: 11/4/12
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class VFSResource extends AbstractResource implements Resource {

    final private VirtualFile virtualFile;

    public VFSResource(VirtualFile virtualFile){
        this.virtualFile = virtualFile;
    }

    @Override
    public String getPath() {
        return virtualFile.getPathName();
    }

    @Override
    public String getName() {
        return virtualFile.getName();
    }

    @Override
    public String getManifest() throws IOException{
        VirtualFile manifestFile = virtualFile.getChild(Utils.MANIFEST_NAME);
        InputStream is = null;
        try {
            is = manifestFile.openStream();
            return extractManfiest(is);
        } finally {
            Utils.safelyClose(is);
        }
    }

    @Override
    public List<Resource> getChildren() {
        return toResourceList(virtualFile.getChildren());
    }

    @Override
    public List<Resource> getChildren(ResourceFilter filter) {
        return Utils.applyFilter(toResourceList(virtualFile.getChildren()), filter);
    }

    @Override
    public long getSize() {
        return virtualFile.getSize();
    }

    @Override
    public boolean isDirectory() {
        return virtualFile.isDirectory();
    }

    @Override
    public boolean isFile() {
        return virtualFile.isFile();
    }

    @Override
    public boolean isSymlink() throws IOException {
        return super.isSymlink(virtualFile.getPhysicalFile());
    }

    @Override
    public InputStream openStream() throws IOException {
        return virtualFile.openStream();
    }

    public static List<Resource> toResourceList(List<VirtualFile> files){
        List<Resource> children = new ArrayList<Resource>(files.size());

        for(VirtualFile child: files) {
            children.add(new VFSResource(child));
        }

        return children;
    }
}
