package utec.cs2031.uber.user.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import utec.cs2031.uber.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRoleAndAvailableTrue(String role);
}
