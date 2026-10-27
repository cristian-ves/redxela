package com.redxela.api;

import com.redxela.api.view.Snapshot;
import com.redxela.api.view.SnapshotAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/* The same state the WebSocket pushes, available on demand; handy to inspect the simulation with curl. */
@RestController
@RequiredArgsConstructor
public class SnapshotController {

    private final SnapshotAssembler assembler;

    @GetMapping("/api/snapshot")
    public Snapshot snapshot() {
        return assembler.assemble();
    }
}