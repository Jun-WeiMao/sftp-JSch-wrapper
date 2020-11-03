package org.sftpjschwrapper.pool;

import com.jcraft.jsch.Session;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.impl.StackKeyedObjectPool;
import org.sftpjschwrapper.pool.vo.ServerDetails;

/**
 * The Stack session pool initialize {@link StackKeyedObjectPool} by providing {@link SessionFactory}.
 */
public class StackSessionPool {
    private int max;
    private final static int DEFAULT_MAX_POOL = 8;
    private KeyedObjectPool<ServerDetails, Session> pool;

    private static StackSessionPool INSTANCE;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static StackSessionPool getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StackSessionPool();
        }
        return INSTANCE;
    }

    private StackSessionPool() {
        startPool(max);
    }

    /**
     * Gets pool.
     *
     * @return the instance of KeyedObjectPool<ServerDetails, Session> that initialize earlier
     */
    public KeyedObjectPool<ServerDetails, Session> getPool() {
        return pool;
    }

    private void startPool(int max) {
        pool = new StackKeyedObjectPool<ServerDetails, Session>(new SessionFactory(), max == 0 ? DEFAULT_MAX_POOL : max);
    }

    /**
     * Sets max.
     *
     * @param max a integer representation of the max of pool size
     */
    public void setMax(int max) {
        this.max = max;
    }

}
