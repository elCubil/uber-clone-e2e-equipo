package utec.cs2031.uber.user.domain;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String role,
        Boolean available,
        Double rating
) {}
