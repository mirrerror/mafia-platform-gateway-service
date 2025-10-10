package md.faf223.mafiaplatformgatewayservice.dtos.usermanagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserManagementRegisterResponseDto {
    private RegisterData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterData {
        private Long id;
        private String username;
    }
}
