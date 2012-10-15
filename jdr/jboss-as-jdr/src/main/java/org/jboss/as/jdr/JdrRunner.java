package org.jboss.as.jdr;

import static org.jboss.as.jdr.JdrLogger.ROOT_LOGGER;
import static org.jboss.as.jdr.JdrMessages.MESSAGES;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.OperationFailedException;

import java.util.Arrays;
import java.util.List;
import java.io.File;

public class JdrRunner implements JdrReportCollector {

    JdrEnvironment env = new JdrEnvironment();

    public JdrRunner() {
    }

    public JdrRunner(String user, String pass, String host, String port) {
        this.env.username = user;
        this.env.password = pass;
        this.env.host = host;
        this.env.port = port;
    }

    public JdrReport collect() throws OperationFailedException {

        try {
            this.env.zip = new JdrZipFile(new JdrEnvironment(this.env));
        }
        catch (Exception e) {
            ROOT_LOGGER.couldNotCreateZipfile(e);
            throw MESSAGES.couldNotCreateZipfile();
        }

        List<JdrCommand> commands;

        try {
            Sanitizer xmlSanitizer = new XMLSanitizer("//password");
            Sanitizer passwordSanitizer = new PatternSanitizer("password=*");

            commands = Arrays.asList(
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
        } catch (Exception e) {
            ROOT_LOGGER.couldNotConfigureJDR(e);
            throw MESSAGES.couldNotConfigureJDR();
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
            this.env.zip.close();
        } catch (Exception e) {
            ROOT_LOGGER.debugf(e, "Could not close zipfile");
        }

        report.setEndTime();
        report.setLocation(this.env.zip.name());
        return report;
    }

    public void setJbossHomeDir(String dir) {
        this.env.jbossHome = dir;
    }

    public void setReportLocationDir(String dir) {
        this.env.outputDirectory = dir;
    }

    public void setControllerClient(ModelControllerClient client) {
        this.env.client = client;
    }

    public void setHostControllerName(String name) {
        this.env.hostControllerName = name;
    }

    public void setServerName(String name) {
        this.env.serverName = name;
    }
}
