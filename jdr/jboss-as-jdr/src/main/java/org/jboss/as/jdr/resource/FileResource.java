package org.jboss.as.jdr.resource;

import org.jboss.as.jdr.resource.filter.ResourceFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * User: csams
 * Date: 11/4/12
 * Time: 2:26 PM
 */
public class FileResource extends AbstractResource {

    private final File file;

    public FileResource(File file){
        this.file = file;
    }

    @Override
    public String getPath() {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String getName() {
        return file.getName();
    }

    //todo csams change this so it doesn't test for .jar
    @Override
    public String getManifest() throws IOException {

        if(!file.getName().endsWith(".jar")){
            return null;
        }

        JarFile jf = null;
        InputStream is = null;
        try {
            jf = new JarFile(file);
            ZipEntry manifest = jf.getEntry(Utils.MANIFEST_NAME);

            if(manifest != null){
                is = jf.getInputStream(manifest);
                return extractManfiest(is);
            }
            else {
                return null;
            }
        } finally {
            Utils.safelyClose(is);
            Utils.safeClose(jf);
        }
    }

    @Override
    public Resource getChild(String path) {
        return new FileResource(new File(file, path));
    }

    @Override
    public List<Resource> getChildren() {
        return toResourceList(file.listFiles());
    }

    @Override
    public List<Resource> getChildren(ResourceFilter filter) {
        return Utils.applyFilter(getChildren(), filter);
    }

    @Override
    public List<Resource> getChildrenRecursively() throws IOException {
        return toResourceList(getLeaves(this.file).toArray(new File[0]));
    }

    @Override
    public List<Resource> getChildrenRecursively(ResourceFilter filter) throws IOException {
        return Utils.applyFilter(getChildrenRecursively(), filter);
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public long lastModified() {
        return file.lastModified();
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public boolean isFile() {
        return file.isFile();
    }

    @Override
    public boolean isSymlink() throws IOException {
        return super.isSymlink(file);
    }

    @Override
    public InputStream openStream() throws IOException {
        return new FileInputStream(file);
    }

    public static List<Resource> toResourceList(File[] files){
        List<Resource> resources = new ArrayList<Resource>(files.length);
        for(File f: files){
            resources.add(new FileResource(f));
        }
        return resources;
    }

    private List<File> getLeaves(File root) throws IOException {
        ArrayList<File> results = new ArrayList<File>();
        for(File f : root.listFiles() ) {
            if(f.isDirectory()) {
                results.addAll(getLeaves(f));
            }
            else {
                results.add(f);
            }
        }
        return results;
    }

}
