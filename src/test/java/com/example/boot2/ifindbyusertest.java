package com.example.boot2;

import com.example.boot2.repository.userRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ifindbyusertest {

    @Autowired
    private userRepo userRepo;

    @Test
    public void findbyusertest(){
        assertEquals(4,2+2);
        assertNotNull(userRepo.findByUsername("Shravan"));
    }

    @ParameterizedTest
    @CsvSource({
            "1,1,2",
            "2,10,12",
            "2,8,12"
    })
    public void teste(int a , int b , int expected){
        assertEquals(expected,a+b);
    }
}
