package com.omarcosallan.planner.participant;

import com.omarcosallan.planner.exceptions.ResourceNotFoundException;
import com.omarcosallan.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository repository;

    public Participant confirmParticipant(UUID participantId, ParticipantRequestPayload payload) {
        Participant participant = repository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found for id " + participantId));

        participant.setIsConfirmed(true);
        participant.setName(payload.name());

        return repository.save(participant);
    }

    public void registerParticipantsToEvent(List<String> participantsToInvite, Trip trip) {
        List<Participant> participants = participantsToInvite.stream().map(email -> new Participant(email, trip)).toList();

        repository.saveAll(participants);
    }

    public ParticipantCreateResponse registerParticipantToEvent(String email, Trip trip) {
        Participant newParticipant = new Participant(email, trip);
        repository.save(newParticipant);

        return new ParticipantCreateResponse(newParticipant.getId());
    }

    public void triggerConfirmationEmailToParticipants(UUID tripId) {
    }

    public void triggerConfirmationEmailToParticipant(String email) {
    }

    public List<ParticipantData> getAllParticipantsFromTrip(UUID tripId) {
        return repository.findByTripId(tripId)
                .stream()
                .map(participant -> new ParticipantData(participant.getId(), participant.getName(), participant.getEmail(), participant.getIsConfirmed()))
                .toList();
    }
}
