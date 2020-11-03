package org.sftpjschwrapper.vo;

import org.sftpjschwrapper.vo.enums.ActionType;

/**
 *  Class to store the Sftp action result.
 */
@SuppressWarnings("unused")
public class SFTPResult {

    /**
     * Instantiates a new Sftp result.
     *
     * @param actionType the action type
     * @param sourcePath the source path
     * @param destPath   the dest path
     */
    public SFTPResult(ActionType actionType, String sourcePath, String destPath) {
        this.actionType = actionType;
        this.sourcePath = sourcePath;
        this.destPath = destPath;
    }

    private ActionType actionType;
    private String command;
    private String sourcePath;
    private String destPath;
    private boolean success;
    private String output;
    private String errMsg;

    /**
     * Gets action type.
     *
     * @return a enum representation of the action type
     */
    public ActionType getActionType() {
        return actionType;
    }

    /**
     * Sets action type.
     *
     * @param actionType the action type
     */
    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    /**
     * Gets command.
     *
     * @return a string representation of the command in cli format
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets command.
     *
     * @param command the command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Gets source path.
     *
     * @return a string representation of the source path
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * Sets source path.
     *
     * @param sourcePath the source path
     */
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    /**
     * Gets dest path.
     *
     * @return a string representation of the destination path
     */
    public String getDestPath() {
        return destPath;
    }

    /**
     * Sets dest path.
     *
     * @param destPath the dest path
     */
    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }

    /**
     * Is success boolean.
     *
     * @return true if action success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets success.
     *
     * @param success is success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Gets output.
     *
     * @return a string representation of the output from host
     */
    public String getOutput() {
        return output;
    }

    /**
     * Sets output.
     *
     * @param output the output
     */
    public void setOutput(String output) {
        this.output = output;
    }

    /**
     * Gets err msg.
     *
     * @return a string representation of the error message
     */
    public String getErrMsg() {
        return errMsg;
    }

    /**
     * Sets err msg.
     *
     * @param errMsg the err msg
     */
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        ActionType[] transFilesType = {ActionType.downloadFile, ActionType.batchDownloadFiles, ActionType.uploadFile, ActionType.batchUploadFiles, ActionType.rename, ActionType.symlink};
        String transFiles = "\n" +
                "[     Action] " + actionType.name() + "\n" +
                "[Trans. flow] " + sourcePath + " => " + destPath + "\n" +
                "[     Result] " + (success ? "SUCCESS" : "FAIL") + "\n" +
                "[  Error msg] " + (errMsg != null ? errMsg : "");

        ActionType[] execCmdType = {ActionType.changeOwner, ActionType.changeMode, ActionType.makeDirectories, ActionType.remove, ActionType.setModifyTime};
        String execCmd = "\n" +
                "[     Action] " + actionType.name() + "\n" +
                "[Cmd. target] " + (command != null ? command + " " + destPath : "") + "\n" +
                "[     Result] " + (success ? "SUCCESS" : "FAIL") + "\n" +
                "[  Error msg] " + (errMsg != null ? errMsg : "");

        if (in(transFilesType, actionType)) {
            return transFiles;
        } else if (in(execCmdType, actionType)) {
            return execCmd;
        } else {
            return "[Default] " + actionType.name() +
                    (command == null ? "" : ", Cmd. target:" + command) +
                    (destPath == null ? "" : ", Target path:" + destPath) +
                    ", Result:" + (success ? "SUCCESS" : "FAIL") +
                    (output == null ? "" : ", Output:" + output) +
                    (errMsg == null ? "" : ", Error msg:" + errMsg);
        }
    }

    private boolean in(ActionType[] types, ActionType target) {
        for (ActionType type : types) {
            if (target.equals(type)) {
                return true;
            }
        }
        return false;
    }

}
