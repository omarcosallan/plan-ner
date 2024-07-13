package com.omarcosallan.planner.trip;

import com.omarcosallan.planner.activity.ActivityData;
import com.omarcosallan.planner.activity.ActivityRequestPayload;
import com.omarcosallan.planner.activity.ActivityResponse;
import com.omarcosallan.planner.activity.ActivityService;
import com.omarcosallan.planner.link.LinkData;
import com.omarcosallan.planner.link.LinkRequestPayload;
import com.omarcosallan.planner.link.LinkResponse;
import com.omarcosallan.planner.link.LinkService;
import com.omarcosallan.planner.participant.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private ParticipantService participantService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private TripRepository repository;

    @Autowired
    private TripService service;

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody @Valid TripRequestPayload payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTip(payload));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id) {
        Trip tripsList = service.getTripsDetails(id);
        return ResponseEntity.ok(tripsList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody @Valid TripRequestPayload payload) {
        return ResponseEntity.ok(service.updateTrip(id, payload));
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id) {
        return ResponseEntity.ok(service.confirmTrip(id));
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, @RequestBody @Valid ActivityRequestPayload payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registerActivity(id, payload));
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id) {
        List<ActivityData> activitiesList = activityService.getAllActivitiesFromTrip(id);
        return ResponseEntity.ok(activitiesList);
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload) {
        ParticipantCreateResponse participantCreateResponse = service.inviteParticipant(id, payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(participantCreateResponse);
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id) {
        List<ParticipantData> participantsList = participantService.getAllParticipantsFromTrip(id);
        return ResponseEntity.ok(participantsList);
    }

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayload payload) {
        LinkResponse linkResponse = service.registerLink(id, payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(linkResponse);
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkData>> getAllLinks(@PathVariable UUID id) {
        List<LinkData> linksList = linkService.getAllLinksFromTrip(id);
        return ResponseEntity.ok(linksList);
    }
}
