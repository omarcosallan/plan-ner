package com.omarcosallan.planner.trip;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record TripRequestPayload(@NotBlank String destination,
                                 @NotBlank @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$") String starts_at,
                                 @NotBlank @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$") String ends_at,
                                 List<@Email String> emails_to_invite,
                                 @Email String owner_email,
                                 @NotBlank String owner_name) {
}
