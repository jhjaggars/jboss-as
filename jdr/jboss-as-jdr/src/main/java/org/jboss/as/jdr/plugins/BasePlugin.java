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

package org.jboss.as.jdr.plugins;

import org.jboss.as.jdr.commands.CallAS7;
import org.jboss.as.jdr.commands.CopyDir;
import org.jboss.as.jdr.commands.JarCheck;
import org.jboss.as.jdr.commands.JdrCommand;
import org.jboss.as.jdr.commands.TreeCommand;
import org.jboss.as.jdr.util.PatternSanitizer;
import org.jboss.as.jdr.util.Sanitizer;
import org.jboss.as.jdr.util.XMLSanitizer;

import java.util.Arrays;
import java.util.List;

public class BasePlugin implements JdrPlugin {

    @Override
    public List<JdrCommand> getCommands() throws Exception {
        Sanitizer xmlSanitizer = new XMLSanitizer("//password");
        Sanitizer passwordSanitizer = new PatternSanitizer("password=*");

        return Arrays.asList(
            new TreeCommand(),
            new JarCheck(),
            new CallAS7("configuration").param("recursive", "true"),
            new CallAS7("dump-services").resource("core-service", "service-container"),
            new CallAS7("cluster-proxies-configuration").resource("subsystem", "modcluster"),
            new CopyDir("*/standalone/configuration/*").sanitizer(xmlSanitizer).sanitizer(passwordSanitizer),
            new CopyDir("*/domain/configuration/*").sanitizer(xmlSanitizer).sanitizer(passwordSanitizer),
            new CopyDir("*.log"),
            new CopyDir("*.properties").sanitizer(passwordSanitizer),
            new CopyDir("*.xml").sanitizer(xmlSanitizer)
        );
    }
}
