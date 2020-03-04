import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
class UserName {
    private String name;
    static UserName fromJson(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, UserName.class);
        } catch (JsonProcessingException e) {
            log.error("Unable to deserialize UserName", e);
            return new UserName("UNNAMED");
        }
    }
}
