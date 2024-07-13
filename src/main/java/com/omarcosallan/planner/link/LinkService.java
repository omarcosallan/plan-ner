package com.omarcosallan.planner.link;

import com.omarcosallan.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LinkService {

    @Autowired
    private LinkRepository repository;

    public LinkResponse registerLink(LinkRequestPayload payload, Trip trip) {
        Link link = new Link(payload.title(), payload.url(), trip);

        repository.save(link);

        return new LinkResponse(link.getId());
    }

    public List<LinkData> getAllLinksFromTrip(UUID tripId) {
        return repository.findByTripId(tripId)
                .stream()
                .map(link -> new LinkData(link.getId(), link.getTitle(), link.getUrl()))
                .toList();
    }
}
