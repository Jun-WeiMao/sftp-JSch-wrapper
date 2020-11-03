package org.sftpjschwrapper.pool.vo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Required host, username, password for initialize
 */
@SuppressWarnings("unused")
public class ServerDetails {
    private String remoteHost;
    private Integer port;
    private String knownHosts;
    private Integer timeout;
    private String username;
    private String password;
    private Map<String, String> config;

    /**
     * Instantiates a new Server details.
     *
     * @param remoteHost ip or domain
     * @param username   the username of host
     * @param password   the password of username
     */
    public ServerDetails(String remoteHost, String username, String password) {
        this.remoteHost = remoteHost;
        this.username = username;
        this.password = password;
        port = 22;
        timeout = 10000;
    }

    /**
     * Gets remote host.
     *
     * @return a string representation of the host
     */
    public String getRemoteHost() {
        return remoteHost;
    }

    /**
     * Sets remote host.
     *
     * @param remoteHost ip or domain
     */
    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    /**
     * Gets port.
     *
     * @return a integer representation of the port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Sets port.
     *
     * @param port port open on the host for sftp connection
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Gets known hosts.
     *
     * @return a string representation of the known hosts
     */
    public String getKnownHosts() {
        return knownHosts;
    }

    /**
     * Sets known hosts.
     *
     * @param knownHosts the known hosts
     */
    public void setKnownHosts(String knownHosts) {
        this.knownHosts = knownHosts;
    }

    /**
     * Gets timeout.
     *
     * @return the timeout
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * Sets timeout.
     *
     * @param timeout a integer representation of the timeout
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets password.
     *
     * @return a string representation of the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets config.
     *
     * @return a map representation of the configurations
     */
    public Map<String, String> getConfig() {
        return config;
    }

    /**
     * Sets config.
     *
     * @param config a map contains configurations
     */
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
