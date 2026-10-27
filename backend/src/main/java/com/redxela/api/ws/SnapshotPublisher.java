package com.redxela.api.ws;

import com.redxela.api.view.SnapshotAssembler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/*
 * Publisher thread: a few times per second it builds the snapshot, turns it
 * into JSON once and hands the text to the handler, which sends the same text
 * to every browser. With nobody connected it does no work at all.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotPublisher {

    private final SnapshotAssembler assembler;
    private final SnapshotWebSocketHandler handler;
    private final JsonMapper json;
    private Thread thread;

    @Value("${redxela.snapshot.interval-millis:250}")
    private long intervalMillis;

    @PostConstruct
    public void start() {
        thread = new Thread(this::publishForever, "snapshot-publisher");
        thread.start();
    }

    @PreDestroy
    public void stop() {
        thread.interrupt();
    }

    private void publishForever() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                publishOnce();
                Thread.sleep(intervalMillis);
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    private void publishOnce() {
        if (!handler.hasClients()) {
            return;
        }
        try {
            handler.broadcast(json.writeValueAsString(assembler.assemble()));
        } catch (RuntimeException failure) {
            log.error("Could not publish the snapshot", failure);
        }
    }
}