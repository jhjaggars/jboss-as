package org.jboss.as.jdr;

import org.jboss.as.controller.client.ModelControllerClient;

import java.util.Arrays;
import java.util.List;

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
        List<JdrCommand> commands = Arrays.asList(
            new TreeCommand(),
            new CallAS7().param("recursive", "true")
        );

        JdrReport report = new JdrReport();
        report.setStartTime();

        for( JdrCommand command : commands ) {
            command.setEnvironment(this.env);
            try {
                command.execute();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        report.setEndTime();
        report.setLocation("none");
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
