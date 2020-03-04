import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import static java.lang.System.exit;

@Slf4j
class WSServer extends WebSocketServer {

    private static final Map<Integer, WebSocket> users = new ConcurrentHashMap<>();
    private static final Map<WebSocket, User> connections = new ConcurrentHashMap<>();

    private WSServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        User user = new User(IdGenerator.getNextId(), "ToDO");
        users.put(user.getId(), conn);
        connections.put(conn, user);

        broadcast(PacketPacker.pack(new UserStatus(user, true)));

        log.debug(user + " entered the room!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        User user = connections.get(conn);
        if (user == null) {
            log.warn("User not found in connections map");
            return;
        }
        connections.remove(conn);
        users.remove(user.getId());
        broadcast(PacketPacker.pack(new UserStatus(user, false)));
        log.debug(user + " has left the room!");
    }

    @Override
    public void onMessage(WebSocket conn, String json) {
        Message decoded = Message.fromJson(json);
        if (decoded.isCorrupted()) {
            log.error("Corrupted message");
            return;
        }
        if (decoded.isGroupChat()) {
            broadcast(PacketPacker.pack(decoded));
            return;
        }

        WebSocket receiver = users.get(decoded.getReceiver());

        if (receiver == null) {
            log.error("Unable to find receiver");
            return;
        }

        receiver.send(PacketPacker.pack(decoded));

    }

    private static void startServer(int port) throws InterruptedException, IOException {
        log.info("Running on port {}", port);
        WSServer s = new WSServer(port);
        s.start();
        BufferedReader systemInput = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = systemInput.readLine();
            s.broadcast(in);
            if (in.equals("exit")) {
                s.stop(1000);
                break;
            }
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            log.error("No port specified");
            exit(1);
        }
        try {
            startServer(Integer.parseInt(args[0]));
        } catch (Exception E) {
            log.error("Exception in startServer", E);
        }

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        log.error("SERVER onError", ex);
    }

    @Override
    public void onStart() {
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

}