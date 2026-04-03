package com.example.boot2.repository;

import com.example.boot2.entity.Journal;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface enitityrepository extends MongoRepository<Journal, ObjectId> {


    Journal removeById(ObjectId id);
}
//Controller--> service --> repository