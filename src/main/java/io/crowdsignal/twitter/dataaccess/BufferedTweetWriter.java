//package io.crowdsignal.twitter.dataaccess;
//
//import io.crowdsignal.entities.TweetEntity;
//import java.util.ArrayList;
//import java.util.List;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
///**
// * Created by jspivey on 9/6/15.
// */
//@Component
//public class BufferedTweetWriter {
//
//    private static final Logger log = LoggerFactory.getLogger(BufferedTweetWriter.class);
//
//    @Autowired
//    private TweetRepo tweetRepo;
//    @Value("${io.crowdsignal.twitter.persitence.postgresql.writebuffer}")
//    private Integer limit;
//
//    private List<TweetEntity> buffer = new ArrayList<>();
//
////    @Async
//    //TODO: If this is not thread safe with regards to 'buffer' ?
//    public void saveTweet(TweetEntity tweet) {
//        buffer.add(tweet);
//
//        if (buffer.size() >= limit) {
//            log.info("Saving {} tweets", buffer.size());
//            // Shallow copy to prevent concurrent modification
//            // --Because the buffer may accept new entries in another thread?
//            List<TweetEntity> copied = new ArrayList<>(buffer);
//
//            tweetRepo.save(copied);
//            buffer.clear();
//        }
//    }
//
//}
