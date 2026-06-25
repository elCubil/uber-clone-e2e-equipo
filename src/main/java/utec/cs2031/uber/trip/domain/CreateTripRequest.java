package utec.cs2031.uber.trip.domain;

import jakarta.validation.constraints.NotBlank;

public record CreateTripRequest(
        @NotBlank String pickupAddress,
        @NotBlank String dropoffAddress
) {}
