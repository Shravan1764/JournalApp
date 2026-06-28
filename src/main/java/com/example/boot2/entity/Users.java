package com.example.boot2.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// instance of a journal entry willbe equivalant to a row
public class Users {

    @Id
    private ObjectId id;
    @Indexed(unique=true)
    @NonNull
    private String username;
    private String email;
    private boolean sentimentAnalysis;
    @NonNull
    private String password;
    @DBRef
    private List<Journal> journalEntries = new ArrayList<>();

    private List<String> roles = new ArrayList<>();

    public boolean getSentimentAnalysis() {
        return sentimentAnalysis;
    }
}
