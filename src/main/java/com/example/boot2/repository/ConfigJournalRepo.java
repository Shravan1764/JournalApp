package com.example.boot2.repository;

import com.example.boot2.entity.ConfigJournalEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigJournalRepo extends MongoRepository<ConfigJournalEntity, String> {
}
