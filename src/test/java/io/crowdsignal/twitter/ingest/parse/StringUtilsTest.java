package io.crowdsignal.twitter.ingest.parse;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jimmy Spivey
 */
public class StringUtilsTest {

    StringUtils su = new StringUtils();

    @Test
    public void testRemoveEnclosingPunctuation() throws Exception {
        Assert.assertEquals(
                "This is a test",
                su.removeEnclosingPunctuation("(This is a test)")
        );
    }

    @Test
    public void testTrimUrlsAndHashTags() throws Exception {
        String trimmed;
        final String EXPECTED =
                "The best cosplay costumes #we saw at London Comic Con in 2016";

        trimmed = su.trimUrlsAndHashTags(
                "#bah #bah The best cosplay costumes #we saw at London Comic Con in 2016 https://t.co/R1v7KketMy https://t.co/xuBMnHVuEE https://t.co/xuBMnHVuEE #hastag https://t.co/xuBMnHVuEE"
        );
        Assert.assertEquals(EXPECTED, trimmed);
        trimmed = su.trimUrlsAndHashTags(
                "The best cosplay costumes #we saw at London Comic Con in 2016 https://t.co/R1v7KketMy https://t.co/xuBMnHVuEE https://t.co/xuBMnHVuEE #hastag https://t.co/xuBMnHVuEE"
        );
        Assert.assertEquals(EXPECTED, trimmed);
        trimmed = su.trimUrlsAndHashTags(
                "#bah #bah The best cosplay costumes #we saw at London Comic Con in 2016"
        );
        Assert.assertEquals(EXPECTED, trimmed);
        trimmed = su.trimUrlsAndHashTags(
                "The best cosplay costumes #we saw at London Comic Con in 2016"
        );
        Assert.assertEquals(EXPECTED, trimmed);
        trimmed = su.trimUrlsAndHashTags(
                "The best cosplay costumes we saw at London Comic Con in 2016"
        );
        Assert.assertEquals(
                "The best cosplay costumes we saw at London Comic Con in 2016",
                trimmed
        );
    }

}
