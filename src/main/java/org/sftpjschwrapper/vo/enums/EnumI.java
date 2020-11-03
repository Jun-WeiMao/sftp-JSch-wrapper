package org.sftpjschwrapper.vo.enums;

/**
 * The interface enum.
 */
public interface EnumI {
    /**
     * Find e by name enum.
     *
     * @param name the name
     * @return the enum
     */
    Enum<?> findEByName(String name);

    /**
     * Find enum by val enum.
     *
     * @param val the val
     * @return the enum
     */
    Enum<?> findEByVal(int val);

}
