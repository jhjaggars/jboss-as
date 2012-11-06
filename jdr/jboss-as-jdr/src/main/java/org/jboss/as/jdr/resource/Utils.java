package org.jboss.as.jdr.resource;

import org.jboss.as.jdr.resource.filter.ResourceFilter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

/**
 * User: csams
 * Date: 11/4/12
 * Time: 3:40 PM
 */
public final class Utils {

    public static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";

    public static List<Resource> applyFilter(List<Resource> resources, ResourceFilter filter) {
        List<Resource> filtered = new ArrayList<Resource>();
        for(Resource resource: resources){
            if(filter.accepts(resource)){
                filtered.add(resource);
            }
        }
        return filtered;
    }

    public static void safeClose(JarFile jf){
        try{
            if(jf != null) {
                jf.close();
            }
        }catch(Exception e){

        }
    }

    public static void safelyClose(InputStream is){
        try{
            if(is != null) {
                is.close();
            }
        }catch(Exception e){

        }
    }

    public static List<String> readLines(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        List<String> result = new ArrayList<String>();
        String line = reader.readLine();

        while(line != null){
            result.add(line);
            line = reader.readLine();
        }
        return result;
    }

    public static String resourceToString(Resource r) throws IOException {
        byte [] buffer = new byte[1024];
        InputStream is = r.openStream();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int bytesRead = is.read(buffer);
        while( bytesRead > -1 ) {
            os.write(buffer, 0, bytesRead);
            bytesRead = is.read(buffer);
        }

        Utils.safelyClose(is);

        return new String(os.toByteArray());
    }

    public static char WIN_SEP = '\\';
    public static char SYS_SEP = File.separatorChar;

    public static boolean isWindows() {
        return SYS_SEP == WIN_SEP;
    }

    public static final long ONE_KB = 1024;

    public static final long ONE_MB = ONE_KB * ONE_KB;

    public static final long ONE_GB = ONE_KB * ONE_MB;

    public static final long ONE_TB = ONE_KB * ONE_GB;

}
