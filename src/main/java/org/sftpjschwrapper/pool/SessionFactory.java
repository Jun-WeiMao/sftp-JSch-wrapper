package org.sftpjschwrapper.pool;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.sftpjschwrapper.pool.vo.ServerDetails;

import java.util.Map;

public class SessionFactory extends BaseKeyedPoolableObjectFactory<ServerDetails, Session> {

    @Override
    public Session makeObject(ServerDetails key) {
        Session session;
        try {
            JSch jsch = new JSch();
            String knowHost = key.getKnownHosts();
            if (knowHost != null && !knowHost.isEmpty()) {
                jsch.setKnownHosts(knowHost);
            }
            session = jsch.getSession(key.getUsername(), key.getRemoteHost(), key.getPort());
            Map<String, String> config = key.getConfig();
            if (config != null && !config.isEmpty()) {
                for (Map.Entry<String, String> entry : config.entrySet()) {
                    session.setConfig(entry.getKey(), entry.getValue());
                }
            }
            session.setTimeout(key.getTimeout());
            session.setPassword(key.getPassword());
            session.connect();
        } catch (Exception e) {
            throw new RuntimeException("Unrecoverable error when trying to connect to serverDetails: " + key, e);
        }
        return session;
    }

    @Override
    public void destroyObject(ServerDetails key, Session obj) {
        obj.disconnect();
    }

}
