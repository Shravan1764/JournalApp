package com.example.boot2.service;

import com.example.boot2.model.SentimentData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SentimentConsumerService {
    @Autowired
    private Emailservice emailservice;

    @KafkaListener(topics = "weekly_sentiments", groupId = "weekly-sentiment-group")
    public void listen(SentimentData sentimentData) {
        try {
            System.out.println("Received sentiment for: " + sentimentData.getEmail());
            sendemail(sentimentData);
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
    private void sendemail(SentimentData sentimentData) {
        emailservice.sendMail(sentimentData.getEmail(),"sentiment for previous week",sentimentData.getSentiment());
    }
}
