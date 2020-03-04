class PacketPacker {
    private final static int USER_STATUS = 1;
    private final static int MESSAGE = 2;

    static String pack(Message message) {
        return MESSAGE + message.toJson();
    }

    static String pack(UserStatus status) {
        return USER_STATUS + status.toJson();
    }
}
