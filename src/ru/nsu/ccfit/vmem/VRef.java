package ru.nsu.ccfit.vmem;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: manul
 * Date: 12/18/13
 * Time: 12:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class VRef {

    private LinkedList<Revision> rHistory = new LinkedList<Revision>();
    private Callable<Object> mergeHandler;
    private static AtomicInteger ticker = new AtomicInteger();

    public VRef(Callable<Object> mergeHandler) {
        this.mergeHandler = mergeHandler;
    }

    public VRef(Callable<Object> mergeHandler, Object value) {
        this.mergeHandler = mergeHandler;
        rHistory.add(new Revision(value));
    }

    /**
     * One element of revision history created at timePoint with value.
     */
    static class Revision {

        private final int timePoint;
        private final Object value;

        Revision(Object value) {
            this.timePoint = ticker.incrementAndGet();
            this.value = value;
        }
    }

    /**
     * Merge handler trigger.
     * @param parent - common parent for the last revision in local history and pending revision.
     * @param pending - revision that needs to be merged with the last local revision.
     * @return returns a result of merge produced by merge handler.
     * @throws Exception
     */
    public Object merge(Revision parent, Revision pending) throws Exception {
        return this.mergeHandler.call();
    }

    public Object deref() {
        return this.rHistory.getLast().value;
    }

}
