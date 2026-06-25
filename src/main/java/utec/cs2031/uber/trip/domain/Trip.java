package utec.cs2031.uber.trip.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import utec.cs2031.uber.user.domain.User;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;

    private String pickupAddress;
    private String dropoffAddress;

    @Enumerated(EnumType.STRING)
    private TripStatus status = TripStatus.PENDING;

    private Instant requestedAt;
    private Instant acceptedAt;
    private Instant completedAt;

    private Integer passengerRating;
    private String ratingComment;
}
