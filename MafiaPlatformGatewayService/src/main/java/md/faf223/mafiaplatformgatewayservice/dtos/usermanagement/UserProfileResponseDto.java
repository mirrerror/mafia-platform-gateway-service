package md.faf223.mafiaplatformgatewayservice.dtos.usermanagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {
    private UserProfileData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileData {
        private Long id;
        private String username;
        private String email;
        private Map<String, Integer> currency;
    }
}
