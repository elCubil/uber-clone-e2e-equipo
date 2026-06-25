package utec.cs2031.uber.user.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utec.cs2031.uber.user.infrastructure.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getAvailable(),
                user.getRating()
        );
    }

    public List<UserResponse> getAvailableDrivers() {
        return userRepository.findByRoleAndAvailableTrue("DRIVER")
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
