package com.example.ingest_api.event.dto;

import java.time.Instant;
import java.util.Map;

public record EventOutDTO(
        String id,
        String type,
        String userId,
        String timestamp,
        Map<String, Object> normalized,
        Map<String, Object> raw,
        String source,
        Instant createdAt
) {}
