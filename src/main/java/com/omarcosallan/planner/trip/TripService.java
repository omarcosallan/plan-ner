package com.omarcosallan.planner.trip;

import com.omarcosallan.planner.activity.ActivityRequestPayload;
import com.omarcosallan.planner.activity.ActivityResponse;
import com.omarcosallan.planner.activity.ActivityService;
import com.omarcosallan.planner.exceptions.InvalidDateException;
import com.omarcosallan.planner.exceptions.ResourceNotFoundException;
import com.omarcosallan.planner.link.LinkRequestPayload;
import com.omarcosallan.planner.link.LinkResponse;
import com.omarcosallan.planner.link.LinkService;
import com.omarcosallan.planner.participant.ParticipantCreateResponse;
import com.omarcosallan.planner.participant.ParticipantRequestPayload;
import com.omarcosallan.planner.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class TripService {

    @Autowired
    private ParticipantService participantService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private TripRepository repository;

    public TripCreateResponse createTip(TripRequestPayload payload) {
        Trip trip = new Trip(payload);

        dateIsValid(trip.getStartsAt(), trip.getEndsAt());

        repository.save(trip);
        participantService.registerParticipantsToEvent(payload.emails_to_invite(), trip);

        return new TripCreateResponse(trip.getId());
    }

    public Trip getTripsDetails(UUID tripId) {
        return repository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found for id " + tripId));
    }

    public Trip updateTrip(UUID tripId, TripRequestPayload payload) {
        Trip trip = repository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found for id " + tripId));

        LocalDateTime startsAt = LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime endsAt = LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME);

        dateIsValid(startsAt, endsAt);

        trip.setStartsAt(startsAt);
        trip.setEndsAt(endsAt);
        trip.setDestination(payload.destination());

        return repository.save(trip);
    }

    public Trip confirmTrip(UUID tripId) {
        Trip trip = repository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found for id " + tripId));

        trip.setIsConfirmed(true);
        repository.save(trip);

        participantService.triggerConfirmationEmailToParticipants(tripId);

        return trip;
    }

    public ActivityResponse registerActivity(UUID tripId, ActivityRequestPayload payload) {
        Trip trip = repository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found for id " + tripId));

        return activityService.registerActivity(payload, trip);
    }

    public ParticipantCreateResponse inviteParticipant(UUID tripId, ParticipantRequestPayload payload) {
        Trip trip = repository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found for id " + tripId));

        ParticipantCreateResponse participant = participantService.registerParticipantToEvent(payload.email(), trip);

        if (trip.getIsConfirmed()) {
            participantService.triggerConfirmationEmailToParticipant(payload.email());
        }

        return participant;
    }

    public LinkResponse registerLink(UUID tripId, LinkRequestPayload payload) {
        Trip trip = repository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found for id " + tripId));

        return linkService.registerLink(payload, trip);
    }

    private void dateIsValid(LocalDateTime startsAt, LocalDateTime endsAt) {
        if (startsAt.isAfter(endsAt) || startsAt.isEqual(endsAt)) {
            throw new InvalidDateException("The start date cannot be greater than or equal to the end date");
        }
    }
}
