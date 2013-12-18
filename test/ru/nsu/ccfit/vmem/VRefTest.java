package ru.nsu.ccfit.vmem;

import org.junit.Assert;
import ru.nsu.ccfit.vmem.VRef;

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
}
