package org.sftpjschwrapper.vo.enums;

/**
 * The enum Action type.
 */
public enum ActionType implements EnumI {
    /**
     * Is connected action type.
     */
    isConnected(0),
    /**
     * Close connection action type.
     */
    closeConnection(1),
    /**
     * Get home path action type.
     */
    getHomePath(2),
    /**
     * Is remote file exists action type.
     */
    isRemoteFileExists(3),
    /**
     * Is remote dir exists action type.
     */
    isRemoteDirExists(4),
    /**
     * Make directories action type.
     */
    makeDirectories(5),
    /**
     * Download file action type.
     */
    downloadFile(11),
    /**
     * Batch download files action type.
     */
    batchDownloadFiles(111),
    /**
     * Upload file action type.
     */
    uploadFile(22),
    /**
     * Batch upload files action type.
     */
    batchUploadFiles(222),
    /**
     * Change owner action type.
     */
    changeOwner(33),
    /**
     * Change mode action type.
     */
    changeMode(44),
    /**
     * Rename action type.
     */
    rename(55),
    /**
     * Remove action type.
     */
    remove(66),
    /**
     * Set modify time action type.
     */
    setModifyTime(77),
    /**
     * Symlink action type.
     */
    symlink(88),
    /**
     * Set status action type.
     */
    setStatus(99);

    private final int val;

    ActionType(int val) {
        this.val = val;
    }

    /**
     * Gets val.
     *
     * @return the val
     */
    public int getVal() {
        return val;
    }

    /**
     * @param name enum string name
     * @return enum otherwise null
     */
    @Override
    public Enum<?> findEByName(String name) {
        if (name != null && !name.isEmpty()) {
            for (ActionType a : ActionType.values()) {
                if (a.name().equals(name)) {
                    return a;
                }
            }
        }
        return null;
    }

    /**
     * @param val enum int val
     * @return enum otherwise null
     */
    @Override
    public Enum<?> findEByVal(int val) {
        for (ActionType a : ActionType.values()) {
            if (a.getVal() == val) {
                return a;
            }
        }
        return null;
    }

}
