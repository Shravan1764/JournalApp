package com.example.boot2.cache;


import com.example.boot2.entity.ConfigJournalEntity;
import com.example.boot2.repository.ConfigJournalRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    public enum keys{
        weather_api
    }

    @Autowired
    private ConfigJournalRepo conrepo;

    public Map<String, String> appCache;
// we are going to user appcache as the cache memory as when the bean is created for the first time of the Appcache class
//the postconstruct method runs and brings the apikey, value  from the mongodb and stores it in the appcache variable

    @PostConstruct
    public void init() {
        appCache = new HashMap<>();
        List<ConfigJournalEntity>  all = conrepo.findAll();
        for (ConfigJournalEntity cje : all) {
            appCache.put(cje.getKey(), cje.getValue());
        }
    }
}
