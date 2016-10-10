package io.crowdsignal.twitter.ingest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jimmy Spivey
 */
@Component
public class WordCounts {

    private static final Logger log = LoggerFactory.getLogger(WordCounts.class);

    private ConcurrentHashMap<String,ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>> wordCounts
            = new ConcurrentHashMap<>();

    public ConcurrentHashMap.KeySetView<String, ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> getAllBuckets() {
        return wordCounts.keySet();
    }

    public ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> getZsets(String bucket) {
        ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> zsets = wordCounts.get(bucket);
        if (zsets == null) {
            ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> value = new ConcurrentHashMap<>();
            wordCounts.put(bucket, value);
            return value;
        }
        return zsets;
    }

    public ConcurrentHashMap<String,Integer> getZset(String bucket, String redisKey) {
        ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> zsets = getZsets(bucket);
        ConcurrentHashMap<String, Integer> zset = zsets.get(redisKey);
        if (zset == null) {
            ConcurrentHashMap<String, Integer> value = new ConcurrentHashMap<>();
            zsets.put(redisKey, value);
            return value;
        }
        return zset;
    }

    public Integer incrementWordCount(String bucket, String redisKey, String word) {
        ConcurrentHashMap<String, Integer> zset = getZset(bucket, redisKey);
        Integer score = zset.get(word);
        if (score == null) {
            zset.put(word, 1);
            return null;
        } else {
            zset.put(word, score+1);
            return score;
        }
    }

    public void removeZsets(String bucket) {
        wordCounts.remove(bucket);
    }

    //TODO: wait, why remove the word counts? Juts persist the ones that meet the threshold and drop references for GC
//    public void removeLowWordCounts(String bucket, int threshold) {
//        //remove low count words in all word counts
//        this.getZsets(bucket).forEachValue(1, zset -> {
//            zset.forEachEntry(1, e -> {
//                String word = e.getKey();
//                Integer score = e.getValue();
//                if (score <= threshold) {
//                    log.trace("Removing {} from internal zset {}", word, zset);
//                    zset.remove(word);
//                }
//            });
//        });
//    }


}
