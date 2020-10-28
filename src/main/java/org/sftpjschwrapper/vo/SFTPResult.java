package org.sftpjschwrapper.vo;

import org.sftpjschwrapper.vo.enums.ActionType;

@SuppressWarnings("unused")
public class SFTPResult {

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

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getDestPath() {
        return destPath;
    }

    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        ActionType[] transFilesType = {ActionType.downloadFile, ActionType.batchDownloadFiles, ActionType.uploadFile, ActionType.batchUploadFiles};
        String transFiles = "\n" +
                "[     Action] " + actionType.name() + "\n" +
                "[Trans. flow] " + sourcePath + " => " + destPath + "\n" +
                "[     Result] " + (success ? "SUCCESS" : "FAIL") + "\n" +
                "[  Error msg] " + (errMsg != null ? errMsg : "");

        ActionType[] execCmdType = {ActionType.changeOwner, ActionType.changeMode, ActionType.makeDirectories};
        String execCmd = "\n" +
                "[     Action] " + actionType.name() + "\n" +
                "[Cmd. target] " + command + " " + destPath + "\n" +
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
