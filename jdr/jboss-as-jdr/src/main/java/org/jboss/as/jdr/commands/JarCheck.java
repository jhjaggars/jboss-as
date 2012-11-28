/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.jdr.commands;


import org.jboss.as.jdr.resource.ResourceFactory;
import org.jboss.as.jdr.resource.Resource;
import org.jboss.as.jdr.resource.Utils;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.jboss.as.jdr.JdrLogger.ROOT_LOGGER;

public class JarCheck extends JdrCommand {

    StringBuilder buffer;

    @Override
    public void execute() throws Exception {
        this.buffer = new StringBuilder();
        walk(ResourceFactory.getResource(this.env.getJbossHome()));
        this.env.getZip().add(this.buffer.toString(), "jarcheck.txt");
    }

    private void walk(Resource root) throws NoSuchAlgorithmException {
        for(Resource f : root.getChildren()) {
            if(f.isDirectory()) {
                walk(f);
            }
            else {
                check(f);
            }
        }
    }

    private void check(Resource f) throws NoSuchAlgorithmException {
        InputStream is = null;
        try {
            MessageDigest alg = MessageDigest.getInstance("md5");
            is = f.openStream();
            byte [] buffer = new byte[(int) f.getSize()];
            is.read(buffer);
            alg.update(buffer);
            String sum = new BigInteger(1, alg.digest()).toString(16);
            this.buffer.append(
                    f.getPath().replace(this.env.getJbossHome(), "JBOSSHOME") + "\n"
                    + sum + "\n"
                    + getManifestString(f) + "===");
        }
        catch( java.util.zip.ZipException ze ) {
            // skip
        }
        catch( java.io.FileNotFoundException fnfe ) {
            ROOT_LOGGER.debug(fnfe);
        }
        catch( java.io.IOException ioe ) {
            ROOT_LOGGER.debug(ioe);
        }
        finally {
            Utils.safelyClose(is);
        }
    }

    private String getManifestString(Resource resource) throws java.io.IOException {
        try {
            String result = resource.getManifest();
            return result != null? result: "";
        } catch (Exception npe) {
            ROOT_LOGGER.tracef("no MANIFEST present");
            return "";
        }
    }
}
