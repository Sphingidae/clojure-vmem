package ru.nsu.ccfit.vmem.core;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: manul
 * Date: 12/18/13
 * Time: 12:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class VRef {

    private final LinkedList<Revision> rHistory = new LinkedList<Revision>();
    private final IFn mergeHandler;

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
    public Object merge(LinkedList<Object> pending) throws Exception {
        Revision parent = this.findActualRevision(VTransaction.getInstance().getStartPoint());
        synchronized (this.rHistory) {
            if (!parent.equals(this.rHistory.getLast())) {
                Object result = this.mergeHandler.invoke(this.getLinkedList(parent), pending);
                return this.set(result);
            }
        }
        return this.set(pending.getLast());
    }

    private LinkedList<Object> getLinkedList(Object parent) {
        LinkedList<Object> result = new LinkedList<Object>();
        Object tmp;
        if (parent == null) {
            synchronized (this.rHistory) {
                for (Iterator<Revision> it = this.rHistory.iterator(); it.hasNext();) {

                    result.add(it.next().value);
                }
            }
            return result;
        }
        //а если parent не было, а потом случайно появился?

        synchronized (this.rHistory) {
            Iterator<Revision> it = this.rHistory.iterator();
            while (it.hasNext()) {
                tmp = it.next().value;
                if (tmp.equals(parent)) {
                    result.add(tmp);
                    break;
                }
            }

            while (it.hasNext()) {
                result.add(it.next().value);
            }
        }
        return result;
    }

    public Object deref() {

        VTransaction tr = VTransaction.getInstance();
        if (!tr.isRunning()) {
            synchronized (this.rHistory) {
                return this.rHistory.getLast().value;
            }
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

        Object value;
        synchronized (this.rHistory) {
            value = rHistory.getLast().value;
        }

        return this.set(modifier.applyTo(RT.cons(value, args)));
    }

    public Object set(Object value) throws IllegalStateException {
        VTransaction tr = VTransaction.getInstance();
        if (!tr.isRunning()) {
            throw new IllegalStateException();
        }
        if (tr.getStatus() == VTransaction.COMMITTING) {
            synchronized (this.rHistory) {
                this.rHistory.add(new Revision(value));
            }
            return value;
        }
        return tr.updCache(this, value);
    }

    private Revision findActualRevision(int startPoint) {
        synchronized (this.rHistory) {
            Iterator<Revision> it = this.rHistory.descendingIterator();
            while (it.hasNext()) {
                Revision r = it.next();
                if (r.timePoint < startPoint) {
                    return r;
                }
            }
        }
        return null;
    }
}
