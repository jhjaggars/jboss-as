package org.jboss.as.jdr.resource;

import org.jboss.as.jdr.resource.filter.ResourceFilter;
import org.jboss.vfs.VirtualFile;
import org.jboss.vfs.VirtualFileFilter;
import org.jboss.vfs.VisitorAttributes;
import org.jboss.vfs.util.FilterVirtualFileVisitor;
import org.jboss.vfs.util.MatchAllVirtualFileFilter;
import org.jboss.vfs.util.automount.Automounter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * User: csams
 * Date: 11/4/12
 * Time: 2:21 PM
 */
public class VFSResource extends AbstractResource implements Resource {

    private final VirtualFile virtualFile;

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
    public String getManifest() throws IOException {
        Automounter.mount(virtualFile);

        InputStream is = null;
        try {
            VirtualFile manifestFile = virtualFile.getChild(Utils.MANIFEST_NAME);
            if(!manifestFile.exists()){
                return null;
            }
            is = manifestFile.openStream();
            return extractManfiest(is);
        } finally {
            Utils.safelyClose(is);
            Automounter.cleanup(virtualFile);
        }
    }

    @Override
    public Resource getChild(String path) {
        return new VFSResource(virtualFile.getChild(path));
    }

    @Override
    public List<Resource> getChildren() {
        return toResourceList(virtualFile.getChildren());
    }

    @Override
    public List<Resource> getChildren(ResourceFilter filter) {
        return Utils.applyFilter(getChildren(), filter);
    }

    @Override
    public List<Resource> getChildrenRecursively() throws IOException {
        return toResourceList(getLeavesRecursively());
    }

    @Override
    public List<Resource> getChildrenRecursively(ResourceFilter filter) throws IOException {
        return Utils.applyFilter(getChildrenRecursively(), filter);
    }

    @Override
    public long getSize() {
        return virtualFile.getSize();
    }

    @Override
    public long lastModified() {
        return virtualFile.getLastModified();
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

    /*
    The VirtualFile#getChildrenRecursively() uses VisistorAttributes.RECURSE and so gets directories as well as files
     */
    private List<VirtualFile> getLeavesRecursively() throws IOException {
        VirtualFileFilter filter = MatchAllVirtualFileFilter.INSTANCE;
        FilterVirtualFileVisitor visitor = new FilterVirtualFileVisitor(filter, VisitorAttributes.RECURSE_LEAVES_ONLY);
        virtualFile.visit(visitor);
        return visitor.getMatched();
    }

}
