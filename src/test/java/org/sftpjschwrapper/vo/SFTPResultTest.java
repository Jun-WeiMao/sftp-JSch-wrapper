package org.sftpjschwrapper.vo;

import org.junit.Assert;
import org.junit.Test;
import org.sftpjschwrapper.vo.enums.ActionType;

public class SFTPResultTest {

    @Test
    public void testToString() {
        SFTPResult r = new SFTPResult(ActionType.downloadFile, "/etc/test.txt", "/etc/opt/test.txt");
        r.setSuccess(true);
        System.out.println(r.toString());
        Assert.assertTrue(r.isSuccess());

        SFTPResult r2 = new SFTPResult(ActionType.uploadFile, "/etc/test.txt", "/etc/opt/test.txt");
        r2.setErrMsg(new NullPointerException("ChannelSftp is required, fail to put file.").getMessage());
        r2.setSuccess(false);
        System.out.println(r2.toString());
        Assert.assertFalse(r2.isSuccess());

        SFTPResult r3 = new SFTPResult(ActionType.isConnected, null, null);
        r3.setSuccess(true);
        System.out.println(r3.toString());

        SFTPResult r4 = new SFTPResult(ActionType.changeMode, null, "/etc/opt/test.txt");
        r4.setCommand("chmod 777");
        r4.setSuccess(true);
        System.out.println(r4.toString());
    }
}
