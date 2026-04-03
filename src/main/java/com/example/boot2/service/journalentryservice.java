package com.example.boot2.service;

import com.example.boot2.entity.Journal;
import com.example.boot2.entity.Users;
import com.example.boot2.repository.enitityrepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class journalentryservice {



    @Autowired
    private enitityrepository enitityrepo;
    @Autowired
    private userService uService;

   @Transactional
    public void save(Journal journal, String username) {
       try {
           Users usr = uService.findByUserName(username);
           journal.setDate(LocalDateTime.now());
           Journal saved = enitityrepo.save(journal);
           usr.getJournalEntries().add(saved);
           //at this position if I change the name of the user or set the user to null then
           //the journal entries will be saved but the user will not be saved in the journal[] part that you have created in user entity
           // the solution is that eiher one should run or no one which is called as trasactional annotation
           //transationsal create a box in which related to this function all the methods will be there
           //it helps to achive the atomicity for different users the individual container will be created an will be
           //treated as one
           uService.save(usr);
       }catch (Exception e) {

           throw new RuntimeException("An error ahs occured while saving the entry"+e);
       }
    }
    public void save(Journal journal) {
        enitityrepo.save(journal);
    }
    public  List<Journal> jetall() {
        return enitityrepo.findAll();
    }
    //Optional can be considered as a box either it can contain data or not
    public Optional<Journal> findentry(@PathVariable ObjectId myid) {
        return enitityrepo.findById(myid);
    }

    @Transactional
    public boolean deleteentity(@PathVariable ObjectId myid, @PathVariable String username) {
        boolean removed = false;
       try {
           Users usr = uService.findByUserName(username);
            removed = usr.getJournalEntries().removeIf(x -> x.getId().equals(myid));
           if (removed) {
               uService.save(usr);
               enitityrepo.deleteById(myid);

           }
       }catch (Exception e) {
           System.out.println(e);
           throw new RuntimeException("An error ahs occured while saving the entry"+e);
       }
       return removed;
    }
}
