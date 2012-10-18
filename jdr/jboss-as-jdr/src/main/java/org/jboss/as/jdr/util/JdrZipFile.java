package org.jboss.as.jdr.util;

import org.jboss.as.jdr.commands.JdrEnvironment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import static org.jboss.as.jdr.JdrLogger.ROOT_LOGGER;

/**
 * Created with IntelliJ IDEA.
 * User: jhjaggars
 * Date: 10/18/12
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class JdrZipFile {

    ZipOutputStream zos;
    String jbossHome;
    JdrEnvironment env;
    String name;

    public JdrZipFile(JdrEnvironment env) throws Exception {
        this.env = env;
        this.jbossHome = this.env.getJbossHome();
        SimpleDateFormat fmt = new SimpleDateFormat("yy-MM-dd_hh-mm-ss");
        this.name = this.env.getOutputDirectory() +
                    java.io.File.separator +
                    "jdr_" + fmt.format(new Date());

        if (this.env.getHostControllerName() != null) {
            this.name += "." + this.env.getHostControllerName();
        }

        if (this.env.getServerName() != null) {
            this.name += "_" + this.env.getServerName();
        }

        this.name += ".zip";

        zos = new ZipOutputStream(new FileOutputStream(this.name));
    }

    public String name() {
        return this.name;
    }

    public void add(InputStream is, String path) {
        byte [] buffer = new byte[1024];

        try {
            ZipEntry ze = new ZipEntry(path);
            zos.putNextEntry(ze);
            int bytesRead = is.read(buffer);
            while( bytesRead > -1 ) {
                zos.write(buffer);
                bytesRead = is.read(buffer);
            }
        }
        catch (ZipException ze) {
            ROOT_LOGGER.tracef(ze, "%s is already in the zip", path);
        }
        catch (Exception e) {
            ROOT_LOGGER.debugf(e, "Error when adding %s", path);
        }
        finally {
            try {
                zos.closeEntry();
            }
            catch (Exception e) {
                ROOT_LOGGER.debugf(e, "Error when closing entry for %s", path);
            }
        }
    }

    public void add(File file, InputStream is) throws Exception {
        String name = "JBOSS_HOME" + file.getPath().substring(this.jbossHome.length());
        this.add(is, name);
    }

    public void add(String content, String path) throws Exception {
        String name = "sos_strings/as7/" + path;
        this.add(new ByteArrayInputStream(content.getBytes()), name);
    }

    public void close() throws Exception {
        this.zos.close();
    }
}
