package com.example.ingest_api.event;

import com.example.ingest_api.event.dto.EventInDTO;
import com.example.ingest_api.event.dto.EventOutDTO;
import org.bson.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class EventMapper {

    /** Flatten selected keys from payload into a simple map (you can add more rules here). */
    public static Map<String,Object> toNormalized(EventInDTO in) {
        Map<String,Object> norm = new HashMap<>();
        if (in.payload() != null) {
            // Example: pull out temperature, unit, and location.* if present
            Object temp = pick(in.payload(), "temperature");
            if (temp != null) norm.put("payload.temperature", temp);

            Object unit = pick(in.payload(), "unit");
            if (unit != null) norm.put("payload.unit", unit);

            Object loc = pick(in.payload(), "location");
            if (loc instanceof Map<?,?> m) {
                Object lat = m.get("lat");
                Object lng = m.get("lng");
                if (lat != null) norm.put("payload.location.lat", lat);
                if (lng != null) norm.put("payload.location.lng", lng);
            }
        }
        return norm;
    }

    private static Object pick(Map<String,Object> map, String key) {
        return map.getOrDefault(key, null);
    }

    public static EventDoc toDoc(EventInDTO in, String source) {
        var rawMap = new HashMap<String,Object>();
        rawMap.put("type", in.type());
        if (in.userId() != null) rawMap.put("userId", in.userId());
        if (in.timestamp() != null) rawMap.put("timestamp", in.timestamp());
        if (in.payload() != null) rawMap.put("payload", in.payload());

        return EventDoc.builder()
                .type(in.type())
                .userId(in.userId())
                .timestamp(in.timestamp())
                .normalized(toNormalized(in))
                .raw(new Document(rawMap))
                .source(source)
                .createdAt(Instant.now())
                .build();
    }

    @SuppressWarnings("unchecked")
    public static EventOutDTO toOut(EventDoc d) {
        Map<String,Object> rawPlain = d.getRaw() == null ? null : new HashMap<>(d.getRaw());
        return new EventOutDTO(
                d.getId(),
                d.getType(),
                d.getUserId(),
                d.getTimestamp(),
                d.getNormalized(),
                rawPlain,
                d.getSource(),
                d.getCreatedAt()
        );
    }
}
