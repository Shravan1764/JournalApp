package com.journal.response.pojo;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class weatherpojo {

    private Current current;

    @Data
    public class Current {
        private Integer temperature;;
        @JsonProperty("weather_descriptions")
        private List<String> weatherDescriptions = new ArrayList<String>();

        private Integer feelslike;

    }

}