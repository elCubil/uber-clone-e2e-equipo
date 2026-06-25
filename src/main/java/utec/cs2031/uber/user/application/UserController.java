package utec.cs2031.uber.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import utec.cs2031.uber.user.domain.User;
import utec.cs2031.uber.user.domain.UserResponse;
import utec.cs2031.uber.user.domain.UserService;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/me")
    @PreAuthorize("isAuthenticated()")
    public UserResponse me(@AuthenticationPrincipal User user) {
        return userService.toResponse(user);
    }

    @GetMapping("/drivers/available")
    @PreAuthorize("hasAuthority('PASSENGER')")
    public List<UserResponse> availableDrivers() {
        return userService.getAvailableDrivers();
    }
}
