package ru.nsu.ccfit.vmem;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;

import java.util.Iterator;
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
            this.timePoint = Ticker.ticker.incrementAndGet();
            this.value = value;
        }
    }

    /**
     * Merge handler trigger.
     * @param pending - revision that needs to be merged with the last local revision.
     * @return returns a result of merge produced by merge handler.
     * @throws Exception
     */
    public Object merge(Object pending) throws Exception {
        Revision parent = this.findActualRevision(VTransaction.getInstance().getStartPoint());
        Object result;
        if (parent == null) {
            result = this.mergeHandler.invoke(null, pending, this.rHistory.getLast());
        } else {
            result = this.mergeHandler.invoke(parent.value, pending, this.rHistory.getLast());
        }

        return this.set(result);
    }

    public Object deref() {

        VTransaction tr = VTransaction.getInstance();
        if (!tr.isRunning()) {
            return this.rHistory.getLast().value;
        }
        Object val = tr.inCache(this);
        if (val != null) {
            return val;
        }
        Revision r = this.findActualRevision(tr.getStartPoint());
        if (r == null) {
            return null;
        }
        return r.value;
    }

    public Object alter(IFn modifier, ISeq args) {
        return this.set(modifier.applyTo(RT.cons(rHistory.getLast().value, args)));
    }

    public Object set(Object value) throws IllegalStateException {
        VTransaction tr = VTransaction.getInstance();
        if (!tr.isRunning()) {
            throw new IllegalStateException();
        }
        if (tr.getStatus() == VTransaction.COMMITTING) {
            this.rHistory.add(new Revision(value));
            return value;
        }
        return tr.updCache(this, value);
    }

    private Revision findActualRevision(int startPoint) {
        Iterator<Revision> it = this.rHistory.descendingIterator();
        while (it.hasNext()) {
            Revision r = it.next();
            if (r.timePoint < startPoint) {
                return r;
            }
        }
        return null;
    }
}
