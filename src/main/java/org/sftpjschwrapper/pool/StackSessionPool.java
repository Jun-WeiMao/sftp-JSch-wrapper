package org.sftpjschwrapper.pool;

import com.jcraft.jsch.Session;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.impl.StackKeyedObjectPool;
import org.sftpjschwrapper.pool.vo.ServerDetails;

public class StackSessionPool {
    private int max;
    private final static int DEFAULT_MAX_POOL = 8;
    private KeyedObjectPool<ServerDetails, Session> pool;

    private static StackSessionPool INSTANCE;

    public static StackSessionPool getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StackSessionPool();
        }
        return INSTANCE;
    }

    private StackSessionPool() {
        startPool(max);
    }

    public KeyedObjectPool<ServerDetails, Session> getPool() {
        return pool;
    }

    public void startPool(int max) {
        pool = new StackKeyedObjectPool<ServerDetails, Session>(new SessionFactory(), max == 0 ? DEFAULT_MAX_POOL : max);
    }

    public void setMax(int max) {
        this.max = max;
    }

}
