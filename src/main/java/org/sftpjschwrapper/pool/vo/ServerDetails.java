package org.sftpjschwrapper.pool.vo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ServerDetails {
    private String remoteHost;
    private Integer port;
    private String knownHosts;
    private Integer timeout;
    private String username;
    private String password;
    private Map<String, String> config;

    public ServerDetails(String remoteHost, String username, String password) {
        this.remoteHost = remoteHost;
        this.username = username;
        this.password = password;
        port = 22;
        timeout = 10000;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getKnownHosts() {
        return knownHosts;
    }

    public void setKnownHosts(String knownHosts) {
        this.knownHosts = knownHosts;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
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

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n")
                .append("[Remote host] ").append(remoteHost).append("\n")
                .append("[       Port] ").append(port).append("\n")
                .append("[Known hosts] ").append(knownHosts == null ? "" : knownHosts).append("\n")
                .append("[    Timeout] ").append(timeout).append("\n")
                .append("[   Username] ").append(username).append("\n")
                .append("[   Password] ").append(password).append("\n");
        if (config == null) {
            config = new HashMap<String, String>();
        }
        builder.append("[     Config] ").append(Arrays.toString(config.entrySet().toArray()));
        return builder.toString();
    }

}
