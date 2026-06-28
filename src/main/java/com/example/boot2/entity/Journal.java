package com.example.boot2.entity;

import com.example.boot2.enums.Sentiment;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "Journal_entries")
@Data
// instance of a journal entry willbe equivalant to a row
public class Journal {

    @Id
    private ObjectId id;
    @NonNull
    private String title;
    private String description;
    private LocalDateTime date;
    private Sentiment sentiment;
}
