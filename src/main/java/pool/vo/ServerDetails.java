package pool.vo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServerDetails {
    String remoteHost;
    String port;
    String knownHosts;
    String timeout;
    String username;
    String password;
    Map<String, String> config;

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getKnownHosts() {
        return knownHosts;
    }

    public void setKnownHosts(String knownHosts) {
        this.knownHosts = knownHosts;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
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
        builder.append("ServerDetails{" + "remoteHost='").append(remoteHost).append('\'')
                .append(", port=").append(port).append('\'')
                .append(", knownHosts='").append(knownHosts).append('\'')
                .append(", timeout=").append(timeout).append('\'')
                .append(", username='").append(username).append('\'')
                .append(", password='").append(password).append('\'');
        if (config == null) {
            config = new HashMap<String, String>();
        }
        builder.append(", config=").append(Arrays.toString(config.entrySet().toArray()));
        builder.append('}');
        return builder.toString();
    }

}
