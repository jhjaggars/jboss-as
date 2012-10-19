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
package org.jboss.as.jdr;

import org.apache.commons.io.FileUtils;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.jdr.commands.JdrCommand;
import org.jboss.as.jdr.commands.JdrEnvironment;
import org.jboss.as.jdr.plugins.JdrPlugin;
import org.jboss.as.jdr.util.JdrZipFile;
import org.jboss.as.cli.impl.CommandContextImpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.jboss.as.jdr.JdrLogger.ROOT_LOGGER;
import static org.jboss.as.jdr.JdrMessages.MESSAGES;

public class JdrRunner implements JdrReportCollector {

    JdrEnvironment env = new JdrEnvironment();
    CommandContextImpl ctx;

    public JdrRunner() {
    }

    public JdrRunner(String user, String pass, String host, String port) {
        this.env.setUsername(user);
        this.env.setPassword(pass);
        this.env.setHost(host);
        this.env.setPort(port);
        try {
            ctx = new CommandContextImpl(host, Integer.valueOf(port), null, null, false);
            ctx.connectController();
            this.env.setClient(ctx.getModelControllerClient());
        }
        catch (Exception e) {
            e.printStackTrace();
            // the server isn't available, carry on
        }
    }

    public JdrReport collect() throws OperationFailedException {

        try {
            this.env.setZip(new JdrZipFile(new JdrEnvironment(this.env)));
        }
        catch (Exception e) {
            ROOT_LOGGER.couldNotCreateZipfile(e);
            throw MESSAGES.couldNotCreateZipfile();
        }

        List<JdrCommand> commands = new ArrayList<JdrCommand>();


        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("plugins.properties");
            Properties plugins = new Properties();
            plugins.load(is);
            for (String pluginName : plugins.stringPropertyNames()) {
                Class pluginClass = Class.forName(pluginName);
                JdrPlugin plugin = (JdrPlugin) pluginClass.newInstance();
                commands.addAll(plugin.getCommands());
            }
        } catch (Exception e) {
            ROOT_LOGGER.couldNotConfigureJDR(e);
            throw MESSAGES.couldNotConfigureJDR();
        }

        if (commands.size() < 1) {
            ROOT_LOGGER.noCommandsToRun();
            throw MESSAGES.noCommandsToRun();
        }

        JdrReport report = new JdrReport();
        report.setStartTime();

        for( JdrCommand command : commands ) {
            command.setEnvironment(new JdrEnvironment(this.env));
            try {
                command.execute();
            } catch (Exception e) {
                ROOT_LOGGER.debugf("Skipping command %s", command.toString());
            }
        }

        try {
            this.env.getZip().close();
        } catch (Exception e) {
            ROOT_LOGGER.debugf(e, "Could not close zipfile");
        }

        report.setEndTime();
        report.setLocation(this.env.getZip().name());

        try {
            ctx.terminateSession();
        } catch (Exception e) {
            // idk
        }

        return report;
    }

    public void setJbossHomeDir(String dir) {
        this.env.setJbossHome(dir);
    }

    public void setReportLocationDir(String dir) {
        this.env.setOutputDirectory(dir);
    }

    public void setControllerClient(ModelControllerClient client) {
        this.env.setClient(client);
    }

    public void setHostControllerName(String name) {
        this.env.setHostControllerName(name);
    }

    public void setServerName(String name) {
        this.env.setServerName(name);
    }
}
