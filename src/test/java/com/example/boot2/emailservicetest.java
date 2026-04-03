package com.example.boot2;

import com.example.boot2.model.SentimentData;
import com.example.boot2.scheduler.UserScheduler;
import com.example.boot2.service.Emailservice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
public class emailservicetest {

    @Autowired
    private Emailservice emailservice;

    @Autowired
    private UserScheduler userScheduler;


    @Autowired
    private KafkaTemplate<String, SentimentData> kafkaTemplate; // Add this!

    @Test
    public void testKafkaFlow() throws InterruptedException {
        // 1. Create a dummy SentimentData object
        SentimentData dummyData = SentimentData.builder()
                .email("shravansoni179@gmail.com")
                .sentiment("Manual Test: Your week was Amazing!")
                .build();

        // 2. Send it DIRECTLY to the Kafka topic
        System.out.println("Sending manual message to Confluent Cloud...");
        kafkaTemplate.send("weekly_sentiments", dummyData.getEmail(), dummyData);

        // 3. Wait for your @KafkaListener to wake up and process it
        System.out.println("Waiting 30 seconds for Consumer to pick it up...");
        Thread.sleep(30000);

        System.out.println("Test complete.");
    }
     @Test
    public void testemailservice() {
        emailservice.sendMail("shravansoni179@gmail.com",
                "to test the mail from springbootapp ",
                "hii i am good to test the mail from springbootapp");
     }
     @Test
     public void testschedule(){
         userScheduler.fetchuserdetailstosendmail();
     }
}
