package org.jboss.as.jdr.resource;

import java.io.*;

import static org.jboss.as.jdr.JdrLogger.ROOT_LOGGER;

/**
 * Created with IntelliJ IDEA.
 * User: csams
 * Date: 11/4/12
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractResource implements  Resource{

    protected String extractManfiest(InputStream is) throws IOException {

        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = reader.readLine();

        while( line.length() != 0 ) {
            buffer.append(line + "\n");
            line = reader.readLine();
        }

        return buffer.toString();
    }

    protected boolean isSymlink(File file) throws IOException {

        if(Utils.isWindows()){
            return false;
        }

        File fileInCanonicalDir = null;
        if (file.getParent() == null) {
            fileInCanonicalDir = file;
        } else {
            File canonicalDir = file.getParentFile().getCanonicalFile();
            fileInCanonicalDir = new File(canonicalDir, file.getName());
        }

        if (fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile())) {
            return false;
        } else {
            return true;
        }

    }

}
