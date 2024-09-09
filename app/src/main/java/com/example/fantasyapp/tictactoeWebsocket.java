package com.example.fantasyapp;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class tictactoeWebsocket extends WebSocketClient {

    public tictactoeWebsocket(URI serverUri) {
        super(serverUri, getCustomHeaders());
    }

    private static Map<String, String> getCustomHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("ngrok-skip-browser-warning", "true"); // Custom header to bypass ngrok warning
        return headers;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server");
    }


    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        // Handle the received message
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from server");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

}
