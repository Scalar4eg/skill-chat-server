class PacketPacker {
    public final static int USER_STATUS = 1;
    public final static int MESSAGE = 2;
    public final static int USER_NAME = 3;

    public static int getType(String json) {
        if (json == null || json.length() == 0) {
            return 0;
        }
        return json.charAt(0) - '0';
    }

    public static Message unpackMessage(String json) {
        return Message.fromJson(json.substring(1));
    }

    public static UserName unpackUserName(String json) {
        return UserName.fromJson(json.substring(1));
    }



    static String pack(Message message) {
        return MESSAGE + message.toJson();
    }

    static String pack(UserStatus status) {
        return USER_STATUS + status.toJson();
    }
}
