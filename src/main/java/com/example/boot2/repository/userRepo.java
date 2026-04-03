package com.example.boot2.repository;

import com.example.boot2.entity.Users;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface userRepo extends MongoRepository<Users, ObjectId> {

    Users findByUsername(String username);

    // at run time the spring boot injects the implementation of these repository methode in the
    //place where we have used it like we use them using auto wire annotation

   void deleteByUsername(String username);
}
