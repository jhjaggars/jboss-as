package org.jboss.as.jdr;

import org.jboss.as.controller.client.ModelControllerClient;

public abstract class JdrCommand {
    JdrEnvironment env;

    public void setEnvironment(JdrEnvironment env) {
        this.env = env;
    }

    public abstract void execute() throws Exception;
}

class JdrEnvironment {
    public String jbossHome;
    public String username;
    public String password;
    public String host;
    public String port;
    public String outputDirectory;
    public String hostControllerName;
    public String serverName;
    public ModelControllerClient client;
    public JdrZipFile zip;

    public JdrEnvironment() {}

    public JdrEnvironment(JdrEnvironment copy) {
        this.jbossHome = copy.jbossHome;
        this.username = copy.username;
        this.password = copy.password;
        this.host = copy.host;
        this.port = copy.port;
        this.outputDirectory = copy.outputDirectory;
        this.hostControllerName = copy.hostControllerName;
        this.serverName = copy.serverName;
        this.client = copy.client;
        this.zip = copy.zip;
    }
}
