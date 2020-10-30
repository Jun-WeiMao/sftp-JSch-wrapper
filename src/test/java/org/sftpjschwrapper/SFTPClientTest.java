package org.sftpjschwrapper;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import org.apache.commons.pool.KeyedObjectPool;
import org.junit.*;
import org.junit.rules.TestName;
import org.sftpjschwrapper.pool.StackSessionPool;
import org.sftpjschwrapper.pool.vo.ServerDetails;
import org.sftpjschwrapper.vo.SFTPResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@FixMethodOrder
public class SFTPClientTest {

    private static final Logger log = LoggerFactory.getLogger(SFTPClientTest.class);

    @Rule
    public TestName name = new TestName();

    static Session session;
    static KeyedObjectPool<ServerDetails, Session> sessionPool;
    static ServerDetails details;
    static SFTPClient sftpClient;
    static String remoteHome;
    static String localHome = System.getProperty("user.home");

    @BeforeClass
    public static void setUp() {
        Properties prop = new Properties();
        InputStream is;
        try {
            // load properties from resource(test)
            is = SFTPClientTest.class.getClassLoader().getResourceAsStream("test.properties");
            prop.load(is);

            Map<String, String> configMap = new HashMap<String, String>();
            String configArr = prop.getProperty("config");
            if (configArr != null && !configArr.isEmpty()) {
                String[] configSplit = configArr.split(",");
                for (String kvArr : configSplit) {
                    String[] keyAndValues = kvArr.split("=");
                    configMap.put(keyAndValues[0], keyAndValues[1]);
                }
            }
            details = new ServerDetails(prop.getProperty("remoteHost"), prop.getProperty("username"), prop.getProperty("password"));
            details.setConfig(configMap);
            log.info(details.toString());

            // init session pool
            StackSessionPool.getInstance().setMax(5);
            sessionPool = StackSessionPool.getInstance().getPool();

            // request session from pool
            session = sessionPool.borrowObject(details);
            Assert.assertTrue(session.isConnected());

            // use session to open sftp channel
            sftpClient = new SFTPClient(session);
            remoteHome = sftpClient.getHomePath(details.getUsername());
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
        Assert.assertEquals("/root", sftpClient.getHomePath(details.getUsername()));
    }

    @Test
    public void isRemoteFileExists() {
        SFTPResult result = sftpClient.isRemoteFileExists(new File(remoteHome + "/test.txt"));
        Assert.assertTrue(result.isSuccess());
        SFTPResult result2 = sftpClient.isRemoteFileExists(new File(remoteHome + "/tmp"));
        Assert.assertFalse(result2.isSuccess());
    }

    @Test
    public void isRemoteDirExists() {
        SFTPResult result = sftpClient.isRemoteDirExists(new File(remoteHome + "/test.txt"), false);
        Assert.assertFalse(result.isSuccess());
        SFTPResult result2 = sftpClient.isRemoteDirExists(new File(remoteHome + "/tmp"), false);
        Assert.assertTrue(result2.isSuccess());
    }

    @Test
    public void testMkdirWithParent() {
        File testDir = new File((remoteHome == null ? "/" : remoteHome) + "/test" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + "fakeDir");
        SFTPResult result = sftpClient.mkdirWithParent(testDir);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void getFile() {
        String fileName = "test.properties";
        File local = new File(localHome + "/Downloads/" + fileName);
        local.delete();
        Assert.assertFalse(local.exists());
        Assert.assertTrue(sftpClient.getFile(new File(remoteHome + "/" + fileName), local).isSuccess());
    }

    @Test
    public void putFile() {
        File localFile = new File(localHome + "/Downloads/test.properties");
        File remoteFile = new File(remoteHome + "/test.properties_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        Assert.assertFalse(sftpClient.isRemoteFileExists(remoteFile).isSuccess());
        Assert.assertTrue(sftpClient.putFile(localFile, remoteFile).isSuccess());
    }

    @Test
    public void chown() {
        File remoteFile = new File(remoteHome + "/test.properties");
        Assert.assertTrue(sftpClient.chown(503, 511, remoteFile, false).isSuccess());
        File fakeRemoteFile = new File(remoteHome + "/test.properties.fake");
        Assert.assertFalse(sftpClient.chown(503, 511, fakeRemoteFile, false).isSuccess());
    }

    @Test
    public void chmod() {
        File remoteFile = new File(remoteHome + "/test.properties");
        Assert.assertTrue(sftpClient.chmod("0777", remoteFile, false).isSuccess());
        File remoteDir = new File(remoteHome + "/test");
        Assert.assertTrue(sftpClient.chmod("0640", remoteDir, true).isSuccess());
    }


    @Before
    public void setUpEach() {
        System.out.println();
        System.out.println(" ### [START] Testing... " + name.getMethodName() + " ###");
    }

    @Test
    public void dirList() {
        File remoteDir = new File(remoteHome + "/Downloads");
        Assert.assertEquals(sftpClient.dirList(remoteDir, false).size(), 2);
        Assert.assertEquals(sftpClient.dirList(remoteDir, true).size(), 1);
    }

    @Test
    public void dirListAll() {
        File remoteDir = new File(remoteHome + "/Downloads");
        Map<String, List<ChannelSftp.LsEntry>> map = sftpClient.dirListAll(remoteDir);
        /*for (Map.Entry<String, List<ChannelSftp.LsEntry>> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ":" + Arrays.toString(entry.getValue().toArray()));
        }*/
        Assert.assertEquals(map.size(), 2);
    }

    @Test
    public void getDir() {
        File remoteDir = new File(remoteHome + "/Downloads");
        File localDest = new File(localHome + "/Downloads");
        sftpClient.getDir(remoteDir, localDest);
        Assert.assertTrue(new File(localDest.getAbsolutePath() + "/" + remoteDir.getAbsolutePath()).exists());

        remoteDir = new File("/test");
        sftpClient.getDir(remoteDir);
        Assert.assertTrue(new File(remoteDir.getAbsolutePath() + "/test1.txt").exists());
    }

    @Test
    public void putDir() {
        File localDir = new File(localHome + "/Downloads/test2");
        sftpClient.putDir(localDir, new File(remoteHome));
        Assert.assertTrue(sftpClient.isRemoteFileExists(new File(remoteHome + "/test2/ttt1.xml")).isSuccess());
        sftpClient.putDir(localDir);
        Assert.assertTrue(sftpClient.isRemoteFileExists(new File("/test2/ttt1.xml")).isSuccess());
    }

    @Test
    public void rename() {
        File oldPath = new File(remoteHome + "/index.html");
        File newPath = new File(remoteHome + "/index.html.bk");
        sftpClient.rename(oldPath, newPath, false);
        Assert.assertTrue(sftpClient.isRemoteFileExists(newPath).isSuccess());
        oldPath = new File(remoteHome + "/testMv");
        newPath = new File(remoteHome + "/testMv2/dir");
        sftpClient.rename(oldPath, newPath, true);
        Assert.assertTrue(sftpClient.isRemoteDirExists(newPath, false).isSuccess());
    }

    @Test
    public void remove() {
        File localDir = new File(localHome + "/Downloads/delDir");
        File remoteDest = new File(remoteHome);
        sftpClient.putDir(localDir, remoteDest);
        Assert.assertTrue(sftpClient.isRemoteDirExists(new File(remoteDest.getAbsolutePath() + "/" + localDir.getName()), false).isSuccess());
        File targetFile = new File(remoteHome + "/delDir");
        sftpClient.remove(targetFile, true);
        Assert.assertFalse(sftpClient.isRemoteDirExists(new File(remoteDest.getAbsolutePath() + "/" + localDir.getName()), false).isSuccess());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        sftpClient.disconnect();
        sessionPool.returnObject(details, session);
    }

}
