package com.opthema.hcm.java_microservice.web.controller.TimeAndAbsence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opthema.hcm.java_microservice.application.service.TimeAndAbsence.EventService;
import com.opthema.hcm.java_microservice.domain.dto.TimeAndAbsence.EventDTO;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time_and_absence/event")
//@PreAuthorize("hasRole('ROLE_USER')")
public class EventController {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.my-topic}")
    private String TOPIC;

    @Autowired
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;
    @GetMapping("/events")
    public ResponseEntity<List<EventDTO>> Events() {
      return eventService.getAllEvents();
    }
    @PostMapping("/create_event")
    public ResponseEntity<?> CreateEvent(@RequestBody EventDTO eventDTO) {
        return eventService.createEvent(eventDTO);
    }
    @DeleteMapping("/delete_event")
    public ResponseEntity<?> DeleteEvent(@RequestParam Long id) {
        return eventService.deleteEvent(id);
    }
    @PutMapping("/update_event")
    public ResponseEntity<?> DeleteEvent(@RequestBody EventDTO eventDTO) {
        return eventService.updateEvent(eventDTO);
    }
    @PostMapping("/create_event_kafka")
    public String CreateEventWithKafka(@RequestHeader("Authorization") String authorization,
                                       @RequestBody EventDTO eventDTO) {
        try {
            String jwtToken = authorization.replace("Bearer ", "");
            String jsonEvent = objectMapper.writeValueAsString(eventDTO);

            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, "key", jsonEvent);
            record.headers().add("Authorization", jwtToken.getBytes());

            kafkaTemplate.send(record);

            return "Message sent to Kafka topic " + TOPIC + " with JWT token: " + jwtToken;
        } catch (JsonProcessingException e) {
            return "Error serializing EventDTO: " + e.getMessage();
        }
    }
}
