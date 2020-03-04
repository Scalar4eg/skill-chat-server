import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class User {
    private int id;
    private String name;
    public String toString() {
        return String.format("ID=%d,NAME=%s", id, name);
    }
}
