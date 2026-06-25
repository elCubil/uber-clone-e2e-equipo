package utec.cs2031.uber;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import utec.cs2031.uber.trip.domain.Trip;
import utec.cs2031.uber.trip.domain.TripStatus;
import utec.cs2031.uber.trip.infrastructure.TripRepository;
import utec.cs2031.uber.user.domain.User;
import utec.cs2031.uber.user.infrastructure.UserRepository;

import java.time.Instant;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    @Bean
    CommandLineRunner seed() {
        return args -> {
            // Drivers
            User carlos = createUser("Carlos", "Rios",   "carlos@uber.com", "pass123", "DRIVER", true,  4.8, 10);
            User lucia  = createUser("Lucia",  "Vargas", "lucia@uber.com",  "pass123", "DRIVER", false, 4.2, 5);
            User pedro  = createUser("Pedro",  "Salas",  "pedro@uber.com",  "pass123", "DRIVER", true,  3.9, 8);

            // Passengers
            User ana   = createUser("Ana",   "Garcia",  "ana@uber.com",   "pass123", "PASSENGER", true, 0.0, 0);
            User mario = createUser("Mario", "Lopez",   "mario@uber.com", "pass123", "PASSENGER", true, 0.0, 0);
            User sofia = createUser("Sofia", "Mendez",  "sofia@uber.com", "pass123", "PASSENGER", true, 0.0, 0);

            // Trip 1: COMPLETED, rated
            Trip t1 = new Trip();
            t1.setPassenger(ana);
            t1.setDriver(carlos);
            t1.setPickupAddress("Av. Javier Prado 1200, San Isidro");
            t1.setDropoffAddress("Miraflores, Lima");
            t1.setStatus(TripStatus.COMPLETED);
            t1.setRequestedAt(Instant.parse("2026-06-20T10:00:00Z"));
            t1.setAcceptedAt(Instant.parse("2026-06-20T10:02:00Z"));
            t1.setCompletedAt(Instant.parse("2026-06-20T10:25:00Z"));
            t1.setPassengerRating(5);
            t1.setRatingComment("Excelente conductor, muy puntual");
            tripRepository.save(t1);

            // Trip 2: IN_PROGRESS
            Trip t2 = new Trip();
            t2.setPassenger(mario);
            t2.setDriver(lucia);
            t2.setPickupAddress("Plaza Mayor, Lima");
            t2.setDropoffAddress("Barranco, Lima");
            t2.setStatus(TripStatus.IN_PROGRESS);
            t2.setRequestedAt(Instant.now().minusSeconds(600));
            t2.setAcceptedAt(Instant.now().minusSeconds(540));
            tripRepository.save(t2);

            // Trip 3: PENDING
            Trip t3 = new Trip();
            t3.setPassenger(sofia);
            t3.setPickupAddress("Aeropuerto Jorge Chavez, Callao");
            t3.setDropoffAddress("San Borja, Lima");
            t3.setStatus(TripStatus.PENDING);
            t3.setRequestedAt(Instant.now().minusSeconds(60));
            tripRepository.save(t3);
        };
    }

    private User createUser(String firstName, String lastName, String email,
                             String password, String role,
                             boolean available, double rating, int ratingCount) {
        User u = new User();
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setEmail(email);
        u.setPassword(password);
        u.setRole(role);
        u.setAvailable(available);
        u.setRating(rating);
        u.setRatingCount(ratingCount);
        return userRepository.save(u);
    }
}
