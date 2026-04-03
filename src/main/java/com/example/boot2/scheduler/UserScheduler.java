package com.example.boot2.scheduler;

import com.example.boot2.cache.AppCache;
import com.example.boot2.entity.Journal;
import com.example.boot2.entity.Users;
import com.example.boot2.enums.Sentiment;
import com.example.boot2.model.SentimentData;
import com.example.boot2.repository.UserRepoImpl;
import com.example.boot2.service.Emailservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserScheduler {

    @Autowired
    private Emailservice emailservice;

    @Autowired
    private UserRepoImpl userRepoI;

    @Autowired
    private AppCache appCache;

    @Autowired
    private KafkaTemplate<String, SentimentData> kafkaTemplate;


 //   @Scheduled(cron = "0 0/1 * 1/1 * ? ")
    public void fetchuserdetailstosendmail(){
        List<Users>  user = userRepoI.getuserforSA();
        for(Users u : user){
        List<Journal>  jentries = u.getJournalEntries();
        List<Sentiment> sentiments = jentries.stream().filter(x -> x.getDate().
                isAfter(LocalDateTime.now().minusDays(7)))
                .map(x-> x.getSentiment())
                .collect(Collectors.toList());
            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
            for (Sentiment sentiment : sentiments) {
                if (sentiment != null)
                    sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
            }
            Sentiment mostFrequentSentiment = null;
            int maxCount = 0;
            for (Map.Entry<Sentiment, Integer> entry : sentimentCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostFrequentSentiment = entry.getKey();
                }
            }
            if (mostFrequentSentiment != null) {
                SentimentData sentimentData = SentimentData.builder().email(u.getEmail()).sentiment("Sentiment for last 7 days " + mostFrequentSentiment).build();
                kafkaTemplate.send("weekly_sentiments", sentimentData.getEmail(), sentimentData);

            }
        }
    }

    //@Scheduled(cron ="0 0/5 * 1/1 * ?")
    public void clearcache(){
        appCache.init();
    }
}
