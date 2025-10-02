package com.example.ingest_api.event.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

/** Incoming JSON from clients (iOS/web). Extra fields allowed; we capture them in "raw". */
public record EventInDTO(
        @NotBlank String type,
        String userId,
        String timestamp,
        Map<String, Object> payload // anything nested goes here, optional
) {}
