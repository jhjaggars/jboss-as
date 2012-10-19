package org.jboss.as.jdr.commands;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.jdr.util.JdrZipFile;

public class JdrEnvironment {
    private String jbossHome;
    private String username;
    private String password;
    private String host;
    private String port;
    private String outputDirectory;
    private String hostControllerName;
    private String serverName;
    private ModelControllerClient client;
    private JdrZipFile zip;

    public JdrEnvironment() {}

    public JdrEnvironment(JdrEnvironment copy) {
        this.setJbossHome(copy.getJbossHome());
        this.setUsername(copy.getUsername());
        this.setPassword(copy.getPassword());
        this.setHost(copy.getHost());
        this.setPort(copy.getPort());
        this.setOutputDirectory(copy.getOutputDirectory());
        this.setHostControllerName(copy.getHostControllerName());
        this.setServerName(copy.getServerName());
        this.setClient(copy.getClient());
        this.setZip(copy.getZip());
    }

    public String getJbossHome() {
        return jbossHome;
    }

    public void setJbossHome(String jbossHome) {
        this.jbossHome = jbossHome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getHostControllerName() {
        return hostControllerName;
    }

    public void setHostControllerName(String hostControllerName) {
        this.hostControllerName = hostControllerName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public ModelControllerClient getClient() {
        return client;
    }

    public void setClient(ModelControllerClient client) {
        this.client = client;
    }

    public JdrZipFile getZip() {
        return zip;
    }

    public void setZip(JdrZipFile zip) {
        this.zip = zip;
    }
}
