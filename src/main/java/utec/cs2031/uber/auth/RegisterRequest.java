package utec.cs2031.uber.auth;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email @NotBlank String email,
        @Size(min = 6) String password,
        @Pattern(regexp = "PASSENGER|DRIVER") String role
) {}
