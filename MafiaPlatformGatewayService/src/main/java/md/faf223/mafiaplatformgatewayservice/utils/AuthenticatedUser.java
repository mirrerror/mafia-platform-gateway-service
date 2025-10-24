package md.faf223.mafiaplatformgatewayservice.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to hold authenticated user information extracted from JWT token
 * This can be used to pass user context to downstream services
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser {
    private Long userId;
    private String username;
}
