package com.example.ingest_api.event;

import com.example.ingest_api.event.dto.EventInDTO;
import com.example.ingest_api.event.dto.EventOutDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    public EventOutDTO ingest(EventInDTO dto, String source) {
        var saved = repo.save(EventMapper.toDoc(dto, source));
        return EventMapper.toOut(saved);
    }

    public EventOutDTO get(String id) {
        return repo.findById(id)
                .map(EventMapper::toOut)   // works now, because repo returns Optional<EventDoc>
                .orElse(null);
    }

    public List<EventOutDTO> byType(String type) {
        return repo.findByTypeOrderByCreatedAtDesc(type)
                .stream()
                .map(EventMapper::toOut)   // works now, because it’s Stream<EventDoc>
                .toList();
    }

    public List<EventOutDTO> byUser(String userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(EventMapper::toOut)
                .toList();
    }
}
