package utec.cs2031.uber.trip.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import utec.cs2031.uber.trip.domain.Trip;
import utec.cs2031.uber.trip.domain.TripStatus;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByPassengerId(Long passengerId);
    List<Trip> findByDriverId(Long driverId);
    List<Trip> findByStatus(TripStatus status);
}
