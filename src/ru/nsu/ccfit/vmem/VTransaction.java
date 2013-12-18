package ru.nsu.ccfit.vmem;

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

    final static ThreadLocal<VTransaction> transaction = new ThreadLocal<VTransaction>();
    Info info;


    public static class Info {
        final AtomicInteger status;
        final long startPoint;
        final CountDownLatch latch;


        public Info(int status, long startPoint) {
            this.status = new AtomicInteger(status);
            this.startPoint = startPoint;
            //TODO:
            this.latch = new CountDownLatch(1);
        }

        public boolean running() {
            int s = status.get();
            //TODO:
            return false;
            //return s == RUNNING || s == COMMITTING;
        }
    }

    static VTransaction getRunning() {
        VTransaction t = transaction.get();
        if(t == null || t.info == null)
            return null;
        return t;
    }

}
