package md.faf223.mafiaplatformgatewayservice.dtos.usermanagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserManagementLoginResponseDto {
    private LoginData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginData {
        private String token;
        private String username;
    }
}
