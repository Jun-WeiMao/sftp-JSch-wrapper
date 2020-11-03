package org.sftpjschwrapper.pool;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.sftpjschwrapper.pool.vo.ServerDetails;

import java.util.Map;

/**
 * Session factory extends {@link BaseKeyedPoolableObjectFactory}, implement make and destroy method for session control.
 */
public class SessionFactory extends BaseKeyedPoolableObjectFactory<ServerDetails, Session> {

    /**
     * @param key a ServerDetails object contains host details as a key for factory
     * @return connected session if details are correct and without other issue
     */
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

    /**
     * @param key     a ServerDetails object contains host details as a key for factory
     * @param session session to disconnect
     */
    @Override
    public void destroyObject(ServerDetails key, Session session) {
        session.disconnect();
    }

}
