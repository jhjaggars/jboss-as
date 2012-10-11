package org.jboss.as.jdr;

import org.jboss.as.controller.client.ModelControllerClient;

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

    public JdrReport collect() {

        try {
            this.env.zip = new JdrZipFile(new JdrEnvironment(this.env));
        }
        catch (Exception e) {
            System.err.println(e);
            // handle zip failure and bail
        }

        List<JdrCommand> commands = Arrays.asList(
            new TreeCommand(),
            new JarCheck(),
            new CallAS7("configuration").param("recursive", "true"),
            new CallAS7("dump-services").resource("core-service", "service-container"),
            new CallAS7("cluster-proxies-configuration").resource("subsystem", "modcluster"),
            new CopyDir("*/standalone/configuration/*"),
            new CopyDir("*/domain/configuration/*"),
            new CopyDir("*.log"),
            new CopyDir("*.properties"),
            new CopyDir("*.xml")
        );

        JdrReport report = new JdrReport();
        report.setStartTime();

        for( JdrCommand command : commands ) {
            command.setEnvironment(new JdrEnvironment(this.env));
            try {
                command.execute();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        try {
            this.env.zip.close();
        } catch (Exception e) {
            System.err.println(e);
            // couldn't close zip
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
