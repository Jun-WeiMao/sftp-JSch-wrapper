package vo.enums;

public enum ActionType implements EnumI {
    isConnected(0),
    closeConnection(1),
    downloadFile(11),
    batchDownloadFiles(111),
    uploadFile(22),
    batchUploadFiles(222);

    private final int val;

    ActionType(int val) {
        this.val = val;
    }

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
