package utec.cs2031.uber.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import utec.cs2031.uber.exception.ResourceNotFoundException;
import utec.cs2031.uber.user.domain.User;
import utec.cs2031.uber.user.infrastructure.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public String generateToken(User user) {
        return JWT.create()
                .withIssuer("uber-clone")
                .withSubject(user.getEmail())
                .withClaim("role", user.getRole())
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    public String validateTokenAndGetEmail(String token) {
        return JWT.require(Algorithm.HMAC256(jwtSecret))
                .withIssuer("uber-clone")
                .build()
                .verify(token)
                .getSubject();
    }

    public User register(RegisterRequest req) {
        User user = new User();
        user.setFirstName(req.firstName());
        user.setLastName(req.lastName());
        user.setEmail(req.email());
        user.setPassword(req.password());
        user.setRole(req.role());
        return userRepository.save(user);
    }

    public User login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!user.getPassword().equals(req.password())) {
            throw new utec.cs2031.uber.exception.UnauthorizedException("Invalid credentials");
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
