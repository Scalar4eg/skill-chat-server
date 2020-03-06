package com.skillbox.cryptochat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.util.logging.Level;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Log
class UserName {
    private String name;
    static UserName fromJson(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, UserName.class);
        } catch (JsonProcessingException e) {
            log.log(Level.SEVERE, "Unable to deserialize com.skillbox.cryptochat.UserName", e);
            return new UserName("UNNAMED");
        }
    }
}
