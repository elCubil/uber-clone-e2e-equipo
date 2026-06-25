package utec.cs2031.uber.trip.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import utec.cs2031.uber.trip.domain.*;
import utec.cs2031.uber.user.domain.User;

import java.util.List;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('PASSENGER')")
    public TripResponse create(@Valid @RequestBody CreateTripRequest req,
                               @AuthenticationPrincipal User passenger) {
        return tripService.create(req, passenger);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PASSENGER')")
    public List<TripResponse> myTrips(@AuthenticationPrincipal User passenger) {
        return tripService.getMyTrips(passenger);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('DRIVER')")
    public List<TripResponse> myDriverTrips(@AuthenticationPrincipal User driver) {
        return tripService.getMyDriverTrips(driver);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('DRIVER')")
    public List<TripResponse> pendingTrips() {
        return tripService.getPendingTrips();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public TripResponse getById(@PathVariable Long id,
                                @AuthenticationPrincipal User user) {
        return tripService.getById(id, user);
    }

    @PatchMapping("/{id}/accept")
    @PreAuthorize("hasAuthority('DRIVER')")
    public TripResponse accept(@PathVariable Long id,
                               @AuthenticationPrincipal User driver) {
        return tripService.accept(id, driver);
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('DRIVER')")
    public TripResponse complete(@PathVariable Long id,
                                 @AuthenticationPrincipal User driver) {
        return tripService.complete(id, driver);
    }

    @PostMapping("/{id}/rate")
    @PreAuthorize("hasAuthority('PASSENGER')")
    public TripResponse rate(@PathVariable Long id,
                             @Valid @RequestBody RateTripRequest req,
                             @AuthenticationPrincipal User passenger) {
        return tripService.rate(id, req, passenger);
    }
}
