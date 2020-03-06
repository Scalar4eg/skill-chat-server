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
class UserStatus {
    private User user;
    private boolean connected;

    String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.log(Level.SEVERE, "Unable to serialize com.skillbox.cryptochat.UserStatus");
            return "";
        }
    }
}
