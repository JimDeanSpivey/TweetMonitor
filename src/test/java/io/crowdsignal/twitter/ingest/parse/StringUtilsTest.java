package io.crowdsignal.twitter.ingest.parse;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jimmy Spivey
 */
public class StringUtilsTest {

    @Test
    public void testRemoveEnclosingPunctuation() throws Exception {
        StringUtils su = new StringUtils();
        Assert.assertEquals(
                "This is a test",
                su.removeEnclosingPunctuation("(This is a test)")
        );


    }

}
