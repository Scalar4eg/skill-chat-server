import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@RequiredArgsConstructor
@NoArgsConstructor
class Message {
    private final static int GROUP_CHAT = 1;
    private int sender;
    @NonNull
    private int receiver;
    @NonNull
    private String encodedText;
    private boolean corrupted = false;

    private Message(boolean corrupted) {
        this.corrupted = corrupted;
    }

    boolean isGroupChat() {
        return receiver == GROUP_CHAT;
    }

    String toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("Unable to serialize Message", e);
            return "";
        }
    }

    static Message fromJson(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, Message.class);
        } catch (JsonProcessingException e) {
            log.error("Unable to deserialize Message", e);
            return new Message();
        }
    }
}
