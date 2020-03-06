package com.skillbox.cryptochat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import lombok.extern.java.Log;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

@Log
class WSServer extends WebSocketServer {
    private static WSServer instance;
    private static final Map<Long, WebSocket> users = new ConcurrentHashMap<>();

    private WSServer(int port) {
        super(new InetSocketAddress(port));
    }

    private void broadcastExcept(String data, WebSocket current) {
        List<WebSocket> clients = new ArrayList<>();
        for(WebSocket c : getConnections()) {
            if (c != current) {
                clients.add(c);
            }
        }
        broadcast(data, clients);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        User user = new User(IdGenerator.getNextId(), "ToDO");
        users.put(user.getId(), conn);
        conn.setAttachment(user);
        log.info(user + " entered the room!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        User user = conn.getAttachment();
        if (user == null) {
            log.log(Level.WARNING, "com.skillbox.cryptochat.User not found in connections map");
            return;
        }
        users.remove(user.getId());
        broadcastExcept(PacketPacker.pack(new UserStatus(user, false)), conn);
        log.info(user + " has left the room!");
    }

    private void handleMessage(Message message, WebSocket conn) {
        if (message.isCorrupted()) {
            log.log(Level.SEVERE, "Corrupted message");
            return;
        }
        User sender = conn.getAttachment();
        if (sender == null) {
            log.log(Level.WARNING,"Sender not found in connections map");
            return;
        }

        message.setSender(sender.getId());

        if (message.isGroupChat()) {
            broadcastExcept(PacketPacker.pack(message), conn);
            return;
        }

        WebSocket receiver = users.get(message.getReceiver());

        if (receiver == null) {
            log.log(Level.WARNING,"Unable to find receiver");
            return;
        }

        receiver.send(PacketPacker.pack(message));
    }

    private void handleUserName(UserName name, WebSocket conn) {
        User user = conn.getAttachment();
        if (user == null) {
            log.log(Level.WARNING,"com.skillbox.cryptochat.User not found in connections map");
            return;
        }
        user.setName(name.getName());
        broadcastExcept(PacketPacker.pack(new UserStatus(user, true)), conn);
        log.info(user + " set their name");

        for (WebSocket peerConn : getConnections()) {
            User peer = peerConn.getAttachment();
            if (peer == null) {
                log.log(Level.WARNING,"Peer not found in connections map");
                continue;
            }
            conn.send(PacketPacker.pack(new UserStatus(peer, true)));
        }
    }
    @Override
    public void onMessage(WebSocket conn, String json) {
        int type = PacketPacker.getType(json);
        log.info("Unpacked message type " + type);
        if (type == PacketPacker.MESSAGE) {
            Message decoded = PacketPacker.unpackMessage(json);
            handleMessage(decoded, conn);
        }
        if (type == PacketPacker.USER_NAME) {
            UserName decoded = PacketPacker.unpackUserName(json);
            handleUserName(decoded, conn);
        }


    }

    public static void stopServer() {
        try {
            instance.stop();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void startServer(int port) throws InterruptedException, IOException {
        log.info("Running on port " + port);
        WSServer s = new WSServer(port);
        instance = s;
        s.start();
        BufferedReader systemInput = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            systemInput.readLine();
        }
    }

    public static void main(String[] args) {
        int port = 8881;
        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }
        try {
            startServer(port);
        } catch (Exception E) {
            log.log(Level.WARNING,"Exception in startServer", E);
        }

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        log.log(Level.WARNING,"SERVER onError", ex);
    }

    @Override
    public void onStart() {
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }
}