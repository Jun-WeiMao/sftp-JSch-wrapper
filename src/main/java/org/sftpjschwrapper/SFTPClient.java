package org.sftpjschwrapper;

import com.jcraft.jsch.*;
import org.sftpjschwrapper.utils.CommonUtils;
import org.sftpjschwrapper.vo.SFTPResult;
import org.sftpjschwrapper.vo.enums.ActionType;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * sftp client
 *
 * @author John.Mao
 * @version 1.0
 * @link https://epaul.github.io/jsch-documentation/javadoc/com/jcraft/jsch/ChannelSftp.html
 */
@SuppressWarnings("unused")
public class SFTPClient {
    private static final Logger log = getLogger(SFTPClient.class);
    private static final CommonUtils utils = CommonUtils.getInstance();

    ChannelSftp channelSftp;

    public SFTPClient(Session session) {
        try {
            if (session == null) {
                throw new NullPointerException("Session is null, fail to connect.");
            } else {
                if (!session.isConnected()) {
                    session.connect();
                }
                this.channelSftp = (ChannelSftp) session.openChannel("sftp");
                channelSftp.connect();
            }
        } catch (JSchException e) {
            log.error(e.getMessage());
        }
    }

    public SFTPResult isConnected() {
        SFTPResult result = new SFTPResult(ActionType.isConnected, null, null);
        if (channelSftp == null) {
            result.setErrMsg(new NullPointerException("ChannelSftp is null, fail to connection testing.").getMessage());
            result.setSuccess(false);
        } else {
            result.setSuccess(channelSftp.isConnected());
        }
        log.info(result.toString());
        return result;
    }

    public void disconnect() {
        if (channelSftp != null) {
            if (channelSftp.isConnected())
                channelSftp.disconnect();
        }
    }

    public String getHomePath(String userName) {
        SFTPResult result = new SFTPResult(ActionType.getHomePath, null, null);
        String home = null;
        if (channelSftp != null) {
            try {
                home = channelSftp.getHome();
                result.setOutput(home);
                result.setCommand("echo ~" + userName);
                result.setSuccess(true);
            } catch (SftpException e) {
                result.setErrMsg(e.getMessage());
                result.setSuccess(false);
            }
        } else {
            result.setErrMsg(new NullPointerException("ChannelSftp is null, fail to get home path.").getMessage());
            result.setSuccess(false);
        }
        log.info(result.toString());
        return home;
    }

    public SFTPResult isRemoteFileExists(File remoteFile) {
        SFTPResult result = new SFTPResult(ActionType.isRemoteFileExists, null, remoteFile.getAbsolutePath());
        if (channelSftp == null) {
            result.setErrMsg(new NullPointerException("ChannelSftp is null, fail to check file existence.").getMessage());
            result.setSuccess(false);
        } else {
            try {
                SftpATTRS sftpATTRS = channelSftp.lstat(remoteFile.getAbsolutePath());
                if (sftpATTRS != null && sftpATTRS.isDir()) {
                    throw new Exception("It's not file.");
                }
                result.setSuccess(true);
            } catch (Exception e) {
                result.setErrMsg(e.getMessage());
                result.setSuccess(false);
            }
        }
        log.info(result.toString());
        return result;
    }

    public SFTPResult isRemoteDirExists(File remoteDir, boolean doCreate) {
        SFTPResult result = new SFTPResult(ActionType.isRemoteDirExists, null, remoteDir.getAbsolutePath());
        if (channelSftp == null) {
            result.setErrMsg(new NullPointerException("ChannelSftp is null, fail to check dir existence.").getMessage());
            result.setSuccess(false);
        } else {
            try {
                SftpATTRS sftpATTRS = channelSftp.lstat(remoteDir.getAbsolutePath());
                if (sftpATTRS != null && !sftpATTRS.isDir()) {
                    throw new Exception("It's not dir.");
                }
                result.setSuccess(true);
            } catch (Exception e) {
                result.setErrMsg(e.getMessage());
                result.setSuccess(false);
                if (doCreate) {
                    mkdirWithParent(remoteDir.getAbsoluteFile());
                    result.setSuccess(true);
                    result.setErrMsg(null);
                }
            }
        }
        log.info(result.toString());
        return result;
    }

    public SFTPResult mkdirWithParent(File targetDir) {
        SFTPResult result = new SFTPResult(ActionType.makeDirectories, null, targetDir.getAbsolutePath());
        String[] dirs = utils.startWithSlash(targetDir.getAbsolutePath()).split("/");
        StringBuilder fullPath = new StringBuilder(dirs[0].isEmpty() ? "/" : dirs[0]);
        int lastIdx = dirs.length - 1;
        try {
            for (int i = 1; i < dirs.length; i++) {
                fullPath.append(dirs[i]).append("/");
                if (!dirs[i].isEmpty() && !isRemoteDirExists(new File(fullPath.toString()), false).isSuccess()) {
                    channelSftp.mkdir(fullPath.toString());
                }
                if (i == lastIdx) {
                    result.setCommand("mkdir -p");
                    result.setDestPath(fullPath.toString());
                    result.setSuccess(true);
                }
            }
        } catch (SftpException e) {
            result.setErrMsg(e.getMessage());
            result.setSuccess(false);
        }
        log.info(result.toString());
        return result;
    }

    //TODO ls

    public SFTPResult getFile(File remoteFile, File localFile) {
        SFTPResult result = new SFTPResult(ActionType.downloadFile, remoteFile.getAbsolutePath(), localFile.getAbsolutePath());
        if (channelSftp == null) {
            result.setErrMsg(new NullPointerException("ChannelSftp is null, fail to get file.").getMessage());
            result.setSuccess(false);
        } else {
            try {
                // check remote file exist
                if (!isRemoteFileExists(remoteFile).isSuccess()) {
                    throw new FileNotFoundException("Remote file is not exists.");
                }
                // check local dir exist
                File localFileParentDir = new File(localFile.getParent());
                if (!localFileParentDir.exists()) {
                    localFileParentDir.mkdirs();
                }
                // get file
                channelSftp.get(remoteFile.getAbsolutePath(), localFile.getAbsolutePath());
                result.setSuccess(true);
            } catch (Exception e) {
                result.setErrMsg(e.getMessage());
                result.setSuccess(false);
            }
        }
        log.info(result.toString());
        return result;
    }

    //TODO get dir

    public SFTPResult putFile(File localFile, File remoteFile) {
        SFTPResult result = new SFTPResult(ActionType.uploadFile, localFile.getAbsolutePath(), remoteFile.getAbsolutePath());
        if (channelSftp == null) {
            result.setErrMsg(new NullPointerException("ChannelSftp is null, fail to put file.").getMessage());
            result.setSuccess(false);
        } else {
            try {
                // check local file exist
                if (!localFile.exists()) {
                    throw new FileNotFoundException("Local file is not exists.");
                }
                // check remoteFile dir exist
                isRemoteDirExists(new File(remoteFile.getParent()), true);
                // put file
                channelSftp.put(localFile.getAbsolutePath(), remoteFile.getAbsolutePath());
                result.setSuccess(true);
            } catch (Exception e) {
                result.setErrMsg(e.getMessage());
                result.setSuccess(false);
            }
        }
        log.info(result.toString());
        return result;
    }

    //TODO put dir

    //TODO chown
    public SFTPResult chown(int userId, File target) {
        SFTPResult result = new SFTPResult(ActionType.changeOwner, null, target.getAbsolutePath());
        if (channelSftp == null) {
            result.setErrMsg(new NullPointerException("ChannelSftp is null, fail to chown.").getMessage());
            result.setSuccess(false);
        } else {
            try {
                // check target exists
                channelSftp.chown(userId, target.getAbsolutePath());
                result.setSuccess(true);
            } catch (SftpException e) {
                result.setErrMsg(e.getMessage());
                result.setSuccess(false);
            }
        }
        log.info(result.toString());
        return result;
    }

    //TODO chmod
    public SFTPResult chmod(int permission, File target) {
        SFTPResult result = new SFTPResult(ActionType.changeMode, null, target.getAbsolutePath());
        if (channelSftp == null) {
            result.setErrMsg(new NullPointerException("ChannelSftp is null, fail to chmod.").getMessage());
            result.setSuccess(false);
        } else {
            // check target exists
            try {
                channelSftp.chmod(permission, target.getAbsolutePath());
                result.setSuccess(true);
            } catch (SftpException e) {
                result.setErrMsg(e.getMessage());
                result.setSuccess(false);
            }
        }
        log.info(result.toString());
        return result;
    }

    //TODO rename
    //TODO rm file
    //TODO rm -r dir
    //TODO setMtime
    //TODO setStat
    //TODO symlink

}
