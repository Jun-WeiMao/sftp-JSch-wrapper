package org.sftpjschwrapper.utils;

public class CommonUtils {

    private static CommonUtils commonUtils;

    private CommonUtils() {
    }

    public static CommonUtils getInstance() {
        if (commonUtils == null) {
            commonUtils = new CommonUtils();
        }
        return commonUtils;
    }

    /**
     * @param s string to modify
     * @return string with slash appended
     */
    public String appendSlash(String s) {
        return s.endsWith("/") ? s : s + "/";
    }

    /**
     * @param s string to modify
     * @return string start with slash
     */
    public String startWithSlash(String s) {
        return s.startsWith("/") ? s : "/" + s;
    }

}
