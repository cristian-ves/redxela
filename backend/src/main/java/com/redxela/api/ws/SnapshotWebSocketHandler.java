package com.redxela.api.ws;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/*
 * Keeps the connected browsers and sends them each snapshot. The interface
 * never sends anything through the socket: commands go through REST and the
 * socket is one-way, server to client.
 *
 * A session is wrapped in a decorator that serializes the sends and drops a
 * client that cannot keep up, so one slow browser can never stall the
 * publisher or exhaust memory with an ever-growing send buffer.
 */
@Slf4j
@Component
public class SnapshotWebSocketHandler extends TextWebSocketHandler {

    private static final int SEND_TIME_LIMIT_MILLIS = 2_000;
    private static final int BUFFER_LIMIT_BYTES = 1024 * 1024;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(),
                new ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT_MILLIS, BUFFER_LIMIT_BYTES));
        log.info("Interface connected ({} open)", sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        log.info("Interface disconnected ({} open)", sessions.size());
    }

    public boolean hasClients() {
        return !sessions.isEmpty();
    }

    public void broadcast(String json) {
        TextMessage message = new TextMessage(json);
        sessions.values().forEach(session -> send(session, message));
    }

    private void send(WebSocketSession session, TextMessage message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(message);
            }
        } catch (Exception failure) {
            log.warn("Dropping interface {}: {}", session.getId(), failure.getMessage());
            sessions.remove(session.getId());
            closeQuietly(session);
        }
    }

    private void closeQuietly(WebSocketSession session) {
        try {
            session.close(CloseStatus.SERVER_ERROR);
        } catch (Exception ignored) {
            // The session is already unusable; there is nothing else to do with it.
        }
    }
}