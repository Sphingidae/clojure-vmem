package ru.nsu.ccfit.vmem.core;

import clojure.lang.IFn;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: manul
 * Date: 12/18/13
 * Time: 12:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class VTransaction {

    private static final ThreadLocal<VTransaction> transaction = new ThreadLocal<VTransaction>();
    private AtomicInteger status = new AtomicInteger();
    private int startPoint;

    private HashMap<VRef, LinkedList<Object>> cache = new HashMap<VRef, LinkedList<Object>>();

    public static final int RUNNING = 1;
    public static final int COMMITTING = 2;
    public static final int STOPPED = 0;
    //public static final int KILLED = 3;
    //public static final int COMMITTED = 0;


    private VTransaction() {
        this.status.set(STOPPED);
    }

    public static VTransaction getInstance() {
        VTransaction tr = transaction.get();
        if (tr == null) {
            tr = new VTransaction();
            transaction.set(tr);
        }

        return tr;
    }

    public static Object runInTransaction(IFn fn) throws Exception {
        VTransaction tr = getInstance();
        if (tr.isRunning()) {
            return fn.invoke();
        }
        return tr.run(fn);
    }

    public Object run(IFn fn) throws Exception {
        try {
            this.startPoint = Ticker.ticker.incrementAndGet();
            this.status.set(RUNNING);
            Object result = fn.invoke();
            this.status.set(COMMITTING);
            for (Map.Entry<VRef, LinkedList<Object>> entry: this.cache.entrySet()) {
                entry.getKey().merge(entry.getValue());
            }
            return result;
        }
        finally {
            this.status.set(STOPPED);
        }

    }

    public int getStatus() {
        return this.status.get();
    }

    public boolean isRunning() {
        return (this.status.get() == COMMITTING) || (this.status.get() == RUNNING);
    }

    public Object updCache(VRef ref, Object value) {
        if (this.cache.get(ref) == null) {
            LinkedList<Object> ll = new LinkedList<Object>();
            ll.add(value);
            this.cache.put(ref, ll);
            return value;
        }
        this.cache.get(ref).add(value);
        return value;
    }

    public Object inCache(VRef ref) {
        return this.cache.get(ref);
    }

    public int getStartPoint() {
        return startPoint;
    }
}
