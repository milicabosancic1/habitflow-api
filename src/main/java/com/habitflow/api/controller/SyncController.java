package com.habitflow.api.controller;

import com.habitflow.api.dto.SyncRequest;
import com.habitflow.api.dto.SyncResponse;
import com.habitflow.api.security.CurrentUser;
import com.habitflow.api.service.SyncService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping
    public SyncResponse sync(@RequestBody SyncRequest req) {
        return syncService.sync(CurrentUser.id(), req);
    }
}
