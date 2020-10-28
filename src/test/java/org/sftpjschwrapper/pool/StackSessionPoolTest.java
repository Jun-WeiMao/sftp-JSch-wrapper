package org.sftpjschwrapper.pool;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import org.apache.commons.pool.KeyedObjectPool;
import org.junit.Assert;
import org.junit.Test;
import org.sftpjschwrapper.pool.vo.ServerDetails;

import java.io.InputStream;
import java.util.*;

public class StackSessionPoolTest {

    @Test
    public void getPool() throws Exception {
        Session session = null;
        ChannelSftp channelSftp = null;
        KeyedObjectPool<ServerDetails, Session> sessionPool = null;
        Properties prop = new Properties();
        InputStream is;
        try {
            // load properties from resource(test)
            is = this.getClass().getClassLoader().getResourceAsStream("test.properties");
            prop.load(is);

            Map<String, String> configMap = new HashMap<String, String>();
            String configArr = prop.getProperty("config");
            if (configArr != null && !configArr.isEmpty()) {
                String[] configSplit = configArr.split(",");
                for (String kvArr : configSplit) {
                    String[] keyAndValues = kvArr.split("=");
                    configMap.put(keyAndValues[0], keyAndValues[1]);
                }
                configMap.put("StrictHostKeyChecking", "No");
            }
            ServerDetails details = new ServerDetails(prop.getProperty("remoteHost"), Integer.parseInt(prop.getProperty("port")), Integer.parseInt(prop.getProperty("timeout")), prop.getProperty("username"), prop.getProperty("password"), configMap);

            // init session org.sftpjschwrapper.pool
            StackSessionPool.getInstance().setMax(5);
            sessionPool = StackSessionPool.getInstance().getPool();

            // request session from org.sftpjschwrapper.pool
            session = sessionPool.borrowObject(details);
            Assert.assertTrue(session.isConnected());

            // use session to open sftp channel
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            Assert.assertTrue(channelSftp.isConnected());

            // list a directory
            String path = "*.bin";
            Vector<?> list = channelSftp.ls(path);
            System.out.println(String.format("\ncommand:[ls %s]", path));
            if (!list.isEmpty()) {
                for (Object name : list) {
                    System.out.println(name);
                }
            }

            // return session to org.sftpjschwrapper.pool
            sessionPool.returnObject(details, session);

            // sleep a while and check if session alive
            /*System.out.println();
            for (int i = 1; i <= 910; i++) {
                System.out.println(String.format("Sleep...[%s]", i));
                Thread.sleep(1000);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (channelSftp != null) {
                // disconnect channel
                channelSftp.disconnect();
                Assert.assertFalse(channelSftp.isConnected());
            }
            if (session != null) {
                // session is connected before return org.sftpjschwrapper.pool
                Assert.assertTrue(session.isConnected());

                // close org.sftpjschwrapper.pool
                sessionPool.close();
                Assert.assertFalse(session.isConnected());
            }
        }
    }
}
