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
 * Created with IntelliJ IDEA.
 * User: csams
 * Date: 11/4/12
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
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

    @Override
    public String getManifest() throws IOException {
        JarFile jf = null;
        InputStream is = null;
        try {
            jf = new JarFile(file);
            ZipEntry manifest = jf.getEntry(Utils.MANIFEST_NAME);
            is = jf.getInputStream(manifest);
            return extractManfiest(is);
        } finally {
            Utils.safelyClose(is);
            Utils.safeClose(jf);
        }
    }

    @Override
    public List<Resource> getChildren() {
        return toResourceList(file.listFiles());
    }

    @Override
    public List<Resource> getChildren(ResourceFilter filter) {
        return Utils.applyFilter(toResourceList(file.listFiles()), filter);
    }

    @Override
    public long getSize() {
        return file.length();
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

}
