package com.example.ingest_api.event;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface EventRepository extends MongoRepository<EventDoc, String> {
    List<EventDoc> findByTypeOrderByCreatedAtDesc(String type);
    List<EventDoc> findByUserIdOrderByCreatedAtDesc(String userId);
}
