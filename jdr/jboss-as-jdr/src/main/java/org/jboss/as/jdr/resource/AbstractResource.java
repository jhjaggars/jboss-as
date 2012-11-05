package org.jboss.as.jdr.resource;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import static org.jboss.as.jdr.JdrLogger.ROOT_LOGGER;

/**
 * User: csams
 * Date: 11/4/12
 * Time: 4:21 PM
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
