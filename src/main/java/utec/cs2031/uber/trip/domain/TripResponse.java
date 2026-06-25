package utec.cs2031.uber.trip.domain;

import utec.cs2031.uber.user.domain.UserResponse;

import java.time.Instant;

public record TripResponse(
        Long id,
        TripStatus status,
        String pickupAddress,
        String dropoffAddress,
        Instant requestedAt,
        Instant acceptedAt,
        Instant completedAt,
        UserResponse passenger,
        UserResponse driver,
        Integer passengerRating,
        String ratingComment
) {}
