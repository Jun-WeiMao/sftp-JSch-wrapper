package org.sftpjschwrapper.utils;

/**
 * Utils class.
 */
public class CommonUtils {

    private static CommonUtils commonUtils;

    private CommonUtils() {
    }

    /**
     * Gets instance.
     *
     * @return the CommonUtils instance
     */
    public static CommonUtils getInstance() {
        if (commonUtils == null) {
            commonUtils = new CommonUtils();
        }
        return commonUtils;
    }

    /**
     * Append slash string.
     *
     * @param s string to modify
     * @return string with slash appended
     */
    public String appendSlash(String s) {
        return s.endsWith("/") ? s : s + "/";
    }

    /**
     * Start with slash string.
     *
     * @param s string to modify
     * @return string start with slash
     */
    public String startWithSlash(String s) {
        return s.startsWith("/") ? s : "/" + s;
    }

}
