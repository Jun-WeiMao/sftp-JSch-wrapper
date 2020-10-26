package vo;

import vo.enums.ActionType;

public class SFTPResult {

    public SFTPResult(ActionType actionType, String sourcePath, String destPath) {
        this.actionType = actionType;
        this.sourcePath = sourcePath;
        this.destPath = destPath;
    }

    private ActionType actionType;
    private String sourcePath;
    private String destPath;
    private boolean success;
    private String errMsg;

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
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

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return "SFTPResult{" +
                "actionType='" + actionType + '\'' +
                ", sourcePath='" + sourcePath + '\'' +
                ", destPath='" + destPath + '\'' +
                ", success=" + success +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }
}
