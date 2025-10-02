package com.example.ingest_api.event;

import com.example.ingest_api.event.dto.EventInDTO;
import com.example.ingest_api.event.dto.EventOutDTO;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EventController {

    private final EventService service;

    public EventController(EventService service) { this.service = service; }

    /** iOS/web send JSON body: Content-Type: application/json */
    @PostMapping(value = "/ingest", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventOutDTO> ingestJson(
            @Valid @RequestBody EventInDTO body,
            @RequestHeader(value = "X-Source", required = false) String source
    ) {
        var created = service.ingest(body, source == null ? "ios" : source);
        return ResponseEntity.created(URI.create("/api/events/" + created.id())).body(created);
    }

    /** iOS/web upload .json file (multipart/form-data) */
    @PostMapping(value = "/ingest-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventOutDTO> ingestFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "timestamp", required = false) String timestamp,
            @RequestHeader(value = "X-Source", required = false) String source
    ) throws Exception {
        try (InputStream in = file.getInputStream()) {
            // parse file content into Map
            var json = new String(in.readAllBytes());
            @SuppressWarnings("unchecked")
            Map<String,Object> payload = new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, Map.class);
            var dto = new com.example.ingest_api.event.dto.EventInDTO(type, userId, timestamp, payload);
            var created = service.ingest(dto, source == null ? "ios" : source);
            return ResponseEntity.created(URI.create("/api/events/" + created.id())).body(created);
        }
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventOutDTO> get(@PathVariable String id) {
        var out = service.get(id);
        return out == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(out);
    }

    @GetMapping("/events")
    public List<EventOutDTO> query(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "userId", required = false) String userId
    ) {
        if (type != null) return service.byType(type);
        if (userId != null) return service.byUser(userId);
        return List.of();
    }
}
