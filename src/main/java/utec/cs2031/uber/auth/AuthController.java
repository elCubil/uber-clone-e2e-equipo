package utec.cs2031.uber.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import utec.cs2031.uber.user.domain.User;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        User user = authService.register(req);
        return new AuthResponse(authService.generateToken(user));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        User user = authService.login(req);
        return new AuthResponse(authService.generateToken(user));
    }
}
