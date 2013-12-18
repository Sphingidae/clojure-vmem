package ru.nsu.ccfit.vmem;

import clojure.lang.IFn;

import java.util.concurrent.CountDownLatch;
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

    public static final int RUNNING = 1;
    public static final int COMMITTING = 2;
    public static final int STOPPED = 0;
    //public static final int KILLED = 3;
    //public static final int COMMITTED = 0;


    public VTransaction() {
        this.status.set(STOPPED);
    }

    public static Object runInTransaction(IFn fn) {
        VTransaction tr = transaction.get();
        if (tr == null) {
            tr = new VTransaction();
            transaction.set(tr);
        }
        if (tr.isRunning()) {
            return fn.invoke();
        }
        return tr.run(fn);
    }

    public Object run(IFn fn) {
        try {
            this.startPoint = Ticker.ticker.incrementAndGet();
            this.status.set(RUNNING);
            Object result = fn.invoke();
            this.status.set(COMMITTING);
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

}
