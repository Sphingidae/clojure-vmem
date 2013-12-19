package ru.nsu.ccfit.vmem.core;

import clojure.lang.AFn;
import junit.framework.Assert;
import org.junit.Test;
import ru.nsu.ccfit.vmem.core.VTransaction;

/**
 * Created with IntelliJ IDEA.
 * User: manul
 * Date: 12/19/13
 * Time: 2:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class VTransactionTest {
    @Test
    public void testRun() throws Exception {
        final VTransaction tr = VTransaction.getInstance();
        Assert.assertEquals(VTransaction.STOPPED, tr.getStatus());
        Object res = tr.run(new AFn() {
            @Override
            public Object invoke() {
                Assert.assertEquals(VTransaction.RUNNING, tr.getStatus());
                return "Everything is fine, bro";
            }
        });
        Assert.assertEquals("Everything is fine, bro", res);
        Assert.assertEquals(VTransaction.STOPPED, tr.getStatus());

    }
}
