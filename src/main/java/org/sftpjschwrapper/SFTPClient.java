package org.sftpjschwrapper;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import org.sftpjschwrapper.utils.CommonUtils;
import org.sftpjschwrapper.vo.SFTPResult;
import org.sftpjschwrapper.vo.enums.ActionType;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Sftp client provide implementation of data manipulation via sftp protocol.
 */
@SuppressWarnings("unused")
public class SFTPClient {
    private static final Logger log = getLogger(SFTPClient.class);
    private static final CommonUtils utils = CommonUtils.getInstance();

    private ChannelSftp channelSftp;

    /**
     * Instantiates a new Sftp client.
     *
     * @param session the session
     */
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

    /**
     * Is connected.
     *
     * @return the sftp result
     */
    public SFTPResult isConnected() {
        SFTPResult result = new SFTPResult(ActionType.isConnected, null, null);
        if (channelSftp == null) {
            result.setErrMsg("ChannelSftp is null, fail to connection testing.");
        } else {
            result.setSuccess(channelSftp.isConnected());
        }
        log.info(result.toString());
        return result;
    }

    /**
     * Disconnect.
     */
    public void disconnect() {
        if (channelSftp != null) {
            if (channelSftp.isConnected())
                channelSftp.disconnect();
        }
    }

    /**
     * Gets home path.
     *
     * @param userName the user name on host
     * @return the home path
     */
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
            }
        } else {
            result.setErrMsg("ChannelSftp is null, fail to get home path.");
        }
        log.info(result.toString());
        return home;
    }

    /**
     * Is remote file exists.
     *
     * @param remoteFile the remote file
     * @return the sftp result
     */
    public SFTPResult isRemoteFileExists(File remoteFile) {
        SFTPResult result = new SFTPResult(ActionType.isRemoteFileExists, null, remoteFile.getAbsolutePath());
        if (channelSftp == null) {
            result.setErrMsg("ChannelSftp is null, fail to check file existence.");
        } else {
            try {
                SftpATTRS sftpATTRS = channelSftp.lstat(remoteFile.getAbsolutePath());
                if (sftpATTRS != null && sftpATTRS.isDir()) {
                    throw new Exception("It's not file.");
                }
                result.setSuccess(true);
            } catch (Exception e) {
                result.setErrMsg(e.getMessage());
            }
        }
        log.info(result.toString());
        return result;
    }

    /**
     * Is remote directory exists.
     *
     * @param remoteDir the remote directory
     * @param doCreate  true will create it when not exists
     * @return the sftp result
     */
    public SFTPResult isRemoteDirExists(File remoteDir, boolean doCreate) {
        SFTPResult result = new SFTPResult(ActionType.isRemoteDirExists, null, remoteDir.getAbsolutePath());
        if (channelSftp == null) {
            result.setErrMsg("ChannelSftp is null, fail to check dir existence.");
        } else {
            try {
                SftpATTRS sftpATTRS = channelSftp.lstat(remoteDir.getAbsolutePath());
                if (sftpATTRS != null && !sftpATTRS.isDir()) {
                    throw new Exception("It's not dir.");
                }
                result.setSuccess(true);
            } catch (Exception e) {
                result.setErrMsg(e.getMessage());
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

    /**
     * Make directory with parent.
     *
     * @param targetDir the target directory
     * @return the sftp result
     */
    public SFTPResult mkdirWithParent(File targetDir) {
        SFTPResult result = new SFTPResult(ActionType.makeDirectories, null, targetDir.getAbsolutePath());
        String[] dirs = utils.startWithSlash(targetDir.getAbsolutePath()).split("/");
        StringBuilder fullPath = new StringBuilder(dirs[0].isEmpty() ? "/" : dirs[0]);
        int lastIdx = dirs.length - 1;
        if (channelSftp == null) {
            result.setErrMsg("ChannelSftp is null, fail to make dir.");
        } else {
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
            }
            log.info(result.toString());
        }
        return result;
    }

    /**
     * List the directory exclude nested directory.
     *
     * @param targetDir the target directory
     * @param fileOnly  true will only show the type is file
     * @return the list
     */
    @SuppressWarnings("unchecked")
    public List<LsEntry> dirList(File targetDir, boolean fileOnly) {
        List<LsEntry> result = new ArrayList<LsEntry>();
        if (channelSftp == null) {
            log.error("ChannelSftp is null, fail to list dir.");
        } else {
            try {
                // check dir exists
                if (!isRemoteDirExists(targetDir, false).isSuccess()) {
                    return result;
                }
                Vector<LsEntry> files = channelSftp.ls(targetDir.getAbsolutePath());
                if (files != null && !files.isEmpty()) {
                    StringBuilder logBuilder = new StringBuilder();
                    for (LsEntry file : files) {
                        if (!file.getFilename().equals(".") && !file.getFilename().equals("..")) {
                            boolean isInclude = false;
                            if (fileOnly) {
                                if (!file.getAttrs().isDir()) {
                                    isInclude = true;
                                }
                            } else {
                                isInclude = true;
                            }
                            if (isInclude) {
                                result.add(file);
                                logBuilder.append("\n").append(file.getLongname());
                            }
                        }
                    }
                    log.info(logBuilder.toString());
                }
            } catch (SftpException e) {
                log.error(e.getMessage());
            }
        }
        return result;
    }

    /**
     * List the directory include nested directory.
     *
     * @param targetDir the target dir
     * @return the map<pathOfTheList, list>
     */
    public Map<String, List<LsEntry>> dirListAll(File targetDir) {
        Map<String, List<LsEntry>> result = new HashMap<String, List<LsEntry>>();
        List<LsEntry> files = dirList(targetDir, false);
        if (files != null && !files.isEmpty()) {
            result.put(targetDir.getAbsolutePath(), files);
            for (LsEntry file : files) {
                if (file.getAttrs().isDir()) {
                    result.putAll(dirListAll(new File(targetDir.getAbsolutePath() + "/" + file.getFilename())));
                }
            }
        }
        return result;
    }

    /**
     * Gets file.
     *
     * @param remoteFile the remote file
     * @param localFile  the local file
     * @return the sftp result
     */
    public SFTPResult getFile(File remoteFile, File localFile) {
        SFTPResult result = new SFTPResult(ActionType.downloadFile, remoteFile.getAbsolutePath(), localFile.getAbsolutePath());
        if (channelSftp == null) {
            result.setErrMsg("ChannelSftp is null, fail to get file.");
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
            }
        }
        log.info(result.toString());
        return result;
    }

    /**
     * Gets full directory with nested directory and files.
     *
     * @param remoteDir the remote directory
     * @return the sftp result
     */
    public SFTPResult getDir(File remoteDir) {
        return getDir(remoteDir, new File("/"));
    }

    /**
     * Gets directory.
     *
     * @param remoteDir the remote directory
     * @param localDest the local destination
     * @return the sftp result
     */
    public SFTPResult getDir(File remoteDir, File localDest) {
        SFTPResult result = new SFTPResult(ActionType.batchDownloadFiles, remoteDir.getAbsolutePath(), localDest.getAbsolutePath());
        // Create local dest dir if not exists
        if (!localDest.getAbsolutePath().isEmpty() && !localDest.exists()) {
            localDest.mkdirs();
        }
        Map<String, List<LsEntry>> dirMap = dirListAll(remoteDir);
        if (!dirMap.isEmpty()) {
            for (Map.Entry<String, List<LsEntry>> entry : dirMap.entrySet()) {
                File remoteDirPath = new File(entry.getKey());
                for (LsEntry fileEntry : entry.getValue()) {
                    File remoteTargetFullPath = new File(remoteDirPath.getAbsolutePath() + "/" + fileEntry.getFilename());
                    File localFullPath = new File(localDest.getAbsolutePath() + "/" + remoteTargetFullPath.getAbsolutePath());
                    if (!fileEntry.getAttrs().isDir()) {
                        getFile(remoteTargetFullPath, localFullPath);
                    } else {
                        localFullPath.mkdirs();
                    }
                }
            }
            result.setSuccess(true);
        } else {
            result.setErrMsg("The dir is not exists.");

        }
        log.info(result.toString());
        return result;
    }

    /**
     * Put file.
     *
     * @param localFile  the local file
     * @param remoteFile the remote file
     * @return the sftp result
     */
    public SFTPResult putFile(File localFile, File remoteFile) {
        SFTPResult result = new SFTPResult(ActionType.uploadFile, localFile.getAbsolutePath(), remoteFile.getAbsolutePath());
        if (channelSftp == null) {
            result.setErrMsg("ChannelSftp is null, fail to put file.");
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
            }
        }
        log.info(result.toString());
        return result;
    }

    /**
     * Put directory.
     *
     * @param localDir the local directory
     * @return the sftp result
     */
    public SFTPResult putDir(File localDir) {
        return putDir(localDir, new File("/"));
    }

    /**
     * Put directory.
     *
     * @param localDir   the local directory
     * @param remoteDest the remote destination
     * @return the sftp result
     */
    public SFTPResult putDir(File localDir, File remoteDest) {
        SFTPResult result = new SFTPResult(ActionType.batchUploadFiles, localDir.getAbsolutePath(), remoteDest.getAbsolutePath());
        if (!localDir.exists() && !localDir.isDirectory()) {
            result.setErrMsg(new Exception("Provided path is not dir or not exists.").getMessage());

        } else {
            isRemoteDirExists(remoteDest, true);
            File[] files = localDir.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    File remoteFileFullPath = new File(remoteDest.getAbsolutePath() + "/" + localDir.getName() + (file.isFile() ? "/" + file.getName() : ""));
                    if (file.isDirectory()) {
                        putDir(file, remoteFileFullPath);
                    } else if (file.isFile()) {
                        putFile(file, remoteFileFullPath);
                    }
                }
                result.setSuccess(true);
            }
        }
        log.info(result.toString());
        return result;
    }

    /**
     * Chown.
     *
     * @param userId  the user id
     * @param groupId the group id
     * @param target  the target
     * @param isDir   true if the target is directory
     * @return the sftp result
     */
    public SFTPResult chown(int userId, int groupId, File target, boolean isDir) {
        SFTPResult result = new SFTPResult(ActionType.changeOwner, null, target.getAbsolutePath());
        if (channelSftp == null) {
            result.setErrMsg("ChannelSftp is null, fail to chown.");
        } else {
            try {
                // check target exists
                SFTPResult checkResult;
                if (isDir) {
                    checkResult = isRemoteDirExists(target, false);
                } else {
                    checkResult = isRemoteFileExists(target);
                }
                if (!checkResult.isSuccess()) {
                    throw new FileNotFoundException("Remote file or dir is not exists.");
                }
                channelSftp.chown(userId, target.getAbsolutePath());
                channelSftp.chgrp(groupId, target.getAbsolutePath());
                result.setCommand("chown " + userId + ":" + groupId);
                result.setSuccess(true);
            } catch (Exception e) {
                result.setErrMsg(e.getMessage());
            }
        }
        log.info(result.toString());
        return result;
    }

    /**
     * Chmod.
     *
     * @param permissionOctal the permission string in octal format (ex:0777)
     * @param target          the target
     * @param isDir           true if the target is directory
     * @return the sftp result
     */
    public SFTPResult chmod(String permissionOctal, File target, boolean isDir) {
        SFTPResult result = new SFTPResult(ActionType.changeMode, null, target.getAbsolutePath());
        if (channelSftp == null) {
            result.setErrMsg("ChannelSftp is null, fail to chmod.");
        } else {
            try {
                // check target exists
                SFTPResult checkResult;
                if (isDir) {
                    checkResult = isRemoteDirExists(target, false);
                } else {
                    checkResult = isRemoteFileExists(target);
                }
                if (!checkResult.isSuccess()) {
                    throw new FileNotFoundException("Remote file or dir is not exists.");
                }
                channelSftp.chmod(Integer.parseInt(permissionOctal, 8), target.getAbsolutePath());
                result.setCommand("chmod " + permissionOctal);
                result.setSuccess(true);
            } catch (Exception e) {
                result.setErrMsg(e.getMessage());
            }
        }
        log.info(result.toString());
        return result;
    }

    /**
     * Rename.
     *
     * @param oldPath the old path
     * @param newPath the new path
     * @param isDir   true if the target is directory
     * @return the sftp result
     */
    public SFTPResult rename(File oldPath, File newPath, boolean isDir) {
        SFTPResult result = new SFTPResult(ActionType.rename, oldPath.getAbsolutePath(), newPath.getAbsolutePath());
        if (channelSftp == null) {
            result.setErrMsg("ChannelSftp is null, fail to rename.");
        } else {
            try {
                // check old file exists and new path parent exists
                SFTPResult checkResult;
                if (isDir) {
                    checkResult = isRemoteDirExists(oldPath, false);
                } else {
                    checkResult = isRemoteFileExists(oldPath);
                }
                if (!checkResult.isSuccess()) {
                    throw new FileNotFoundException("Remote file or dir is not exists.");
                }
                isRemoteDirExists(new File(newPath.getParent()), true);
                channelSftp.rename(oldPath.getAbsolutePath(), newPath.getAbsolutePath());
                result.setSuccess(true);
            } catch (Exception e) {
                result.setErrMsg(e.getMessage());
            }
        }
        log.info(result.toString());
        return result;
    }

    /**
     * Remove.
     *
     * @param target the target
     * @param isDir  true if the target is directory
     * @return the sftp result
     */
    public SFTPResult remove(File target, boolean isDir) {
        SFTPResult result = new SFTPResult(ActionType.remove, null, target.getAbsolutePath());
        boolean doLog = true;
        if (channelSftp == null) {
            result.setErrMsg("ChannelSftp is null, fail to remove.");
        } else {
            try {
                if (isDir) {
                    List<LsEntry> files = dirList(target, false);
                    if (!files.isEmpty()) {
                        for (LsEntry file : files) {
                            remove(new File(target.getAbsoluteFile() + "/" + file.getFilename()), file.getAttrs().isDir());
                        }
                    }
                } else {
                    channelSftp.rm(target.getAbsolutePath());
                    result.setCommand("rm");
                    result.setSuccess(true);
                }
                if (isDir && isRemoteDirExists(target, false).isSuccess()) {
                    try {
                        channelSftp.rmdir(target.getAbsolutePath());
                        result.setCommand("rm -r");
                        result.setSuccess(true);
                    } catch (SftpException ignore) {
                        doLog = false;
                    }
                }
            } catch (SftpException e) {
                result.setErrMsg(e.getMessage());
            }
        }
        if (doLog) {
            log.info(result.toString());
        }
        return result;
    }

    /**
     * Sets modify time.
     *
     * @param target        the target
     * @param isDir         true if the target is directory
     * @param newModifyTime the new modify time
     * @return the sftp result
     */
    public SFTPResult setModifyTime(File target, boolean isDir, long newModifyTime) {
        SFTPResult result = new SFTPResult(ActionType.setModifyTime, null, target.getAbsolutePath());
        if (channelSftp == null) {
            result.setErrMsg("ChannelSftp is null, fail to set modify time.");
        } else {
            try {
                SFTPResult checkResult;
                if (isDir) {
                    checkResult = isRemoteDirExists(target, false);
                } else {
                    checkResult = isRemoteFileExists(target);
                }
                if (!checkResult.isSuccess()) {
                    throw new FileNotFoundException("Remote file or dir is not exists.");
                }
                channelSftp.setMtime(target.getAbsolutePath(), Integer.parseInt(String.valueOf(newModifyTime / 1000L)));
                result.setCommand("touch -d " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(newModifyTime));
                result.setSuccess(true);
            } catch (Exception e) {
                result.setErrMsg(e.getMessage());
            }
        }
        log.info(result.toString());
        return result;
    }

    /**
     * Symlink.
     *
     * @param isDir   true if the target is directory
     * @param oldPath the old path
     * @param newPath the new path
     * @return the sftp result
     */
    public SFTPResult symlink(boolean isDir, File oldPath, File newPath) {
        SFTPResult result = new SFTPResult(ActionType.symlink, oldPath.getAbsolutePath(), newPath.getAbsolutePath());
        if (channelSftp == null) {
            result.setErrMsg("ChannelSftp is null, fail to modify symlink.");
        } else {
            try {
                // Check file exists
                SFTPResult checkResult;
                if (isDir) {
                    checkResult = isRemoteDirExists(oldPath, false);
                } else {
                    checkResult = isRemoteFileExists(oldPath);
                }
                if (!checkResult.isSuccess()) {
                    throw new FileNotFoundException("Remote file or dir is not exists.");
                }
                channelSftp.symlink(oldPath.getAbsolutePath(), newPath.getAbsolutePath());
                result.setSuccess(true);
            } catch (Exception e) {
                result.setErrMsg(e.getMessage());
            }
        }
        log.info(result.toString());
        return result;
    }

    /**
     * Gets stat.
     *
     * @param isDir  true if the target is directory
     * @param target the target
     * @return the SftpATTRS
     */
    public SftpATTRS getStat(boolean isDir, File target) {
        SftpATTRS result = null;
        if (channelSftp != null) {
            try {
                SFTPResult checkResult;
                if (isDir) {
                    checkResult = isRemoteDirExists(target, false);
                } else {
                    checkResult = isRemoteFileExists(target);
                }
                if (!checkResult.isSuccess()) {
                    throw new FileNotFoundException("Remote file or dir is not exists.");
                }
                result = channelSftp.lstat(target.getAbsolutePath());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        if (result != null) {
            log.info("[stat] target:" + target.getAbsolutePath() + " " + result.toString());
        }
        return result;
    }

    /**
     * Sets stat.
     *
     * @param isDir    true if the target is directory
     * @param target   the target
     * @param newAttrs the new attrs
     * @return the sftp result
     */
    public SFTPResult setStat(boolean isDir, File target, SftpATTRS newAttrs) {
        SFTPResult result = new SFTPResult(ActionType.setStatus, null, target.getAbsolutePath());
        if (channelSftp == null) {
            result.setErrMsg("ChannelSftp is null, fail to modify status.");
        } else {
            try {
                SFTPResult checkResult;
                if (isDir) {
                    checkResult = isRemoteDirExists(target, false);
                } else {
                    checkResult = isRemoteFileExists(target);
                }
                if (!checkResult.isSuccess()) {
                    throw new FileNotFoundException("Remote file or dir is not exists.");
                }
                channelSftp.setStat(target.getAbsolutePath(), newAttrs);
            } catch (Exception e) {
                result.setErrMsg(e.getMessage());
            }
            if (isSftpATTRSEquals(newAttrs, getStat(isDir, target))) {
                result.setOutput(getStat(isDir, target).toString());
                result.setSuccess(true);
                result.setErrMsg(null);
            }
        }
        log.info(result.toString());
        return result;
    }

    private boolean isSftpATTRSEquals(SftpATTRS attrs1, SftpATTRS attrs2) {
        return attrs1.getGId() == attrs2.getGId()
                && attrs1.getATime() == attrs2.getATime()
                && attrs1.getMTime() == attrs2.getMTime()
                && attrs1.getSize() == attrs2.getSize()
                && attrs1.getPermissions() == attrs2.getPermissions();
    }

}
