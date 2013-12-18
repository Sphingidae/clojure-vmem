package ru.nsu.ccfit.vmem;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;

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
    private IFn mergeHandler;
    private static AtomicInteger ticker = new AtomicInteger();

    public VRef(IFn mergeHandler) {
        this.mergeHandler = mergeHandler;
    }

    public VRef(IFn mergeHandler, Object value) {
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
        return this.mergeHandler.invoke(parent, pending, this.rHistory.getLast());
    }

    public Object deref() {
        return this.rHistory.getLast().value;
    }

    public Object alter(IFn modifier, ISeq args) {
        return this.set(modifier.applyTo(RT.cons(rHistory.getLast().value, args)));
    }

    public Object set(Object value) {
        this.rHistory.add(new Revision(value));
        return this.rHistory.getLast().value;
    }

}
