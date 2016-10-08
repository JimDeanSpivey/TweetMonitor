//package io.crowdsignal.integrationtests;
//
//import io.crowdsignal.twitter.ingest.SearchContextProvider;
//import io.crowdsignal.twitter.ingest.TweetIngestor;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.Arrays;
//
//import static org.mockito.Mockito.when;
//
///**
// * @author Jimmy Spivey
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = TweetIngestorTest.class)
//@ComponentScan(basePackages = "io.crowdsignal.twitter.ingest.parse")
//public class TweetIngestorTest {
//
//    @Autowired
//    private TweetIngestor ingestor;
//
//    @MockBean
//    private SearchContextProvider searchContextProvider;
//
//    @Before
//    public void setup() {
//        when(searchContextProvider.allKeywords()).thenReturn(
//                Arrays.asList("nyc", "New york", "la", "los angeles")
//        );
//    }
//
//    @Test
//    public void test() {
//        ingestor.run();
//    }
//
//}
