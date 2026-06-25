package utec.cs2031.uber.trip.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utec.cs2031.uber.exception.InvalidTripStateException;
import utec.cs2031.uber.exception.ResourceNotFoundException;
import utec.cs2031.uber.user.domain.User;
import utec.cs2031.uber.user.domain.UserService;
import utec.cs2031.uber.user.infrastructure.UserRepository;
import utec.cs2031.uber.trip.infrastructure.TripRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public TripResponse toResponse(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getStatus(),
                trip.getPickupAddress(),
                trip.getDropoffAddress(),
                trip.getRequestedAt(),
                trip.getAcceptedAt(),
                trip.getCompletedAt(),
                userService.toResponse(trip.getPassenger()),
                trip.getDriver() != null ? userService.toResponse(trip.getDriver()) : null,
                trip.getPassengerRating(),
                trip.getRatingComment()
        );
    }

    public TripResponse create(CreateTripRequest req, User passenger) {
        Trip trip = new Trip();
        trip.setPassenger(passenger);
        trip.setPickupAddress(req.pickupAddress());
        trip.setDropoffAddress(req.dropoffAddress());
        trip.setRequestedAt(Instant.now());
        return toResponse(tripRepository.save(trip));
    }

    public List<TripResponse> getMyTrips(User passenger) {
        return tripRepository.findByPassengerId(passenger.getId())
                .stream().map(this::toResponse).toList();
    }

    public List<TripResponse> getMyDriverTrips(User driver) {
        return tripRepository.findByDriverId(driver.getId())
                .stream().map(this::toResponse).toList();
    }

    public List<TripResponse> getPendingTrips() {
        return tripRepository.findByStatus(TripStatus.PENDING)
                .stream().map(this::toResponse).toList();
    }

    public TripResponse getById(Long id, User requester) {
        Trip trip = findTrip(id);
        boolean isParticipant = trip.getPassenger().getId().equals(requester.getId())
                || (trip.getDriver() != null && trip.getDriver().getId().equals(requester.getId()));
        if (!isParticipant) {
            throw new org.springframework.security.access.AccessDeniedException("Access denied");
        }
        return toResponse(trip);
    }

    public TripResponse accept(Long id, User driver) {
        Trip trip = findTrip(id);
        if (!driver.getAvailable()) {
            throw new InvalidTripStateException("Driver is not available");
        }
        if (trip.getStatus() != TripStatus.PENDING) {
            throw new InvalidTripStateException("Trip is not available for acceptance");
        }
        trip.setDriver(driver);
        trip.setStatus(TripStatus.IN_PROGRESS);
        trip.setAcceptedAt(Instant.now());
        driver.setAvailable(false);
        userRepository.save(driver);
        return toResponse(tripRepository.save(trip));
    }

    public TripResponse complete(Long id, User driver) {
        Trip trip = findTrip(id);
        if (trip.getStatus() != TripStatus.IN_PROGRESS) {
            throw new InvalidTripStateException("Trip is not in progress");
        }
        if (!trip.getDriver().getId().equals(driver.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Access denied");
        }
        trip.setStatus(TripStatus.COMPLETED);
        trip.setCompletedAt(Instant.now());
        driver.setAvailable(true);
        userRepository.save(driver);
        return toResponse(tripRepository.save(trip));
    }

    public TripResponse rate(Long id, RateTripRequest req, User passenger) {
        Trip trip = findTrip(id);
        if (!trip.getPassenger().getId().equals(passenger.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Access denied");
        }
        if (trip.getStatus() != TripStatus.COMPLETED) {
            throw new InvalidTripStateException("Trip must be completed before rating");
        }
        if (trip.getPassengerRating() != null) {
            throw new InvalidTripStateException("Trip has already been rated");
        }
        trip.setPassengerRating(req.rating());
        trip.setRatingComment(req.comment());

        User tripDriver = trip.getDriver();
        int newCount = tripDriver.getRatingCount() + 1;
        double newRating = ((tripDriver.getRating() * tripDriver.getRatingCount()) + req.rating()) / newCount;
        tripDriver.setRatingCount(newCount);
        tripDriver.setRating(Math.round(newRating * 10.0) / 10.0);
        userRepository.save(tripDriver);

        return toResponse(tripRepository.save(trip));
    }

    private Trip findTrip(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found: " + id));
    }
}
