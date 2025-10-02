package com.example.ingest_api.event;

import lombok.*;
import org.bson.Document;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.Map;

@org.springframework.data.mongodb.core.mapping.Document(collection = "events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventDoc {
    @Id
    private String id;

    private String type;
    private String userId;
    private String timestamp;
    private Map<String, Object> normalized;
    private Document raw;

    private String source;
    private Instant createdAt;
}
