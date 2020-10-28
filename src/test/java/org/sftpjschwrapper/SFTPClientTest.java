package org.sftpjschwrapper;

import com.jcraft.jsch.Session;
import org.apache.commons.pool.KeyedObjectPool;
import org.junit.*;
import org.junit.rules.TestName;
import org.sftpjschwrapper.pool.StackSessionPool;
import org.sftpjschwrapper.pool.vo.ServerDetails;
import org.sftpjschwrapper.vo.SFTPResult;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@FixMethodOrder
public class SFTPClientTest {

    @Rule
    public TestName name = new TestName();

    static Session session;
    static KeyedObjectPool<ServerDetails, Session> sessionPool;
    static ServerDetails details;
    static SFTPClient sftpClient;

    @BeforeClass
    public static void setUp() {
        Properties prop = new Properties();
        InputStream is;
        try {
            // load properties from resource(test)
            is = SFTPClientTest.class.getClassLoader().getResourceAsStream("test.properties");
            prop.load(is);

            details = new ServerDetails();
            Map<String, String> configMap = new HashMap<String, String>();
            details.setRemoteHost(prop.getProperty("remoteHost"));
            details.setPort(prop.getProperty("port"));
            details.setUsername(prop.getProperty("username"));
            details.setPassword(prop.getProperty("password"));
            details.setTimeout(prop.getProperty("timeout"));

            String configArr = prop.getProperty("config");
            if (configArr != null && !configArr.isEmpty()) {
                String[] configSplit = configArr.split(",");
                System.out.println("config:" + Arrays.toString(configSplit));
                for (String kvArr : configSplit) {
                    String[] keyAndValues = kvArr.split("=");
                    configMap.put(keyAndValues[0], keyAndValues[1]);
                }
                //configMap.put("StrictHostKeyChecking", "No");
                details.setConfig(configMap);
            }

            // init session pool
            StackSessionPool.getInstance().setMax(5);
            sessionPool = StackSessionPool.getInstance().getPool();

            // request session from pool
            session = sessionPool.borrowObject(details);
            Assert.assertTrue(session.isConnected());

            // use session to open sftp channel
            sftpClient = new SFTPClient(session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void isConnected() {
        Assert.assertTrue(sftpClient.isConnected().isSuccess());
    }

    @Test
    public void disconnect() {
        sftpClient.disconnect();
        Assert.assertFalse(sftpClient.isConnected().isSuccess());
        sftpClient = new SFTPClient(session);
    }

    @Test
    public void getHomePath() {
        Assert.assertEquals("/home/mmccadmin", sftpClient.getHomePath(details.getUsername()));
    }

    @Test
    public void isRemoteFileExists() {
        String home = sftpClient.getHomePath(details.getUsername());
        SFTPResult result = sftpClient.isRemoteFileExists(new File(home + "/test.txt"));
        Assert.assertTrue(result.isSuccess());
        SFTPResult result2 = sftpClient.isRemoteFileExists(new File(home + "/tmp"));
        Assert.assertFalse(result2.isSuccess());
    }

    @Test
    public void isRemoteDirExists() {
        String home = sftpClient.getHomePath(details.getUsername());
        SFTPResult result = sftpClient.isRemoteDirExists(new File(home + "/test.txt"), false);
        Assert.assertFalse(result.isSuccess());
        SFTPResult result2 = sftpClient.isRemoteDirExists(new File(home + "/tmp"), false);
        Assert.assertTrue(result2.isSuccess());
    }

    @Test
    public void testMkdirWithParent() {
        String home = sftpClient.getHomePath(details.getUsername());
        File testDir = new File((home == null ? "/" : home) + "/test" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + "fakeDir");
        SFTPResult result = sftpClient.mkdirWithParent(testDir);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void getFile() {
    }

    @Test
    public void putFile() {
    }

    @Test
    public void chown() {
    }

    @Test
    public void chmod() {
    }

    @AfterClass
    public static void tearDown() throws Exception {
        sftpClient.disconnect();
        sessionPool.returnObject(details, session);
    }

    @Before
    public void setUpEach() {
        System.out.println(" ### [START] Testing... " + name.getMethodName() + " ###");
    }

    @After
    public void tearDownEach() {
        System.out.println();
    }
}
