package ru.nsu.ccfit.vmem.core;

import clojure.lang.AFn;
import org.junit.Assert;
import org.junit.Test;
import ru.nsu.ccfit.vmem.core.VRef;
import ru.nsu.ccfit.vmem.core.VTransaction;

/**
 * Created with IntelliJ IDEA.
 * User: manul
 * Date: 12/19/13
 * Time: 12:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class VRefTest {

    @org.junit.Test
    public void testMerge() throws Exception {

    }

    @org.junit.Test
    public void testDeref() throws Exception {
        VRef ref = new VRef(null, new Integer(1));
        Assert.assertEquals(new Integer(1), ref.deref());
    }

    @Test
    public void testAlter() throws Exception {
        //TODO: test alter
    }

    @Test
    public void testSet() throws Exception {
        final VRef ref = new VRef(new AFn() {
            @Override
            public Object invoke(Object arg1, Object arg2, Object arg3) {
                return arg2;
            }
        }, new Integer(1));
        VTransaction.runInTransaction(new AFn() {
            @Override
            public Object invoke() {
                ref.set(new Integer(2));
                Assert.assertEquals(new Integer(2), ref.deref());
                return null;
            }
        });
        Assert.assertEquals(new Integer(2), ref.deref());
    }
}
