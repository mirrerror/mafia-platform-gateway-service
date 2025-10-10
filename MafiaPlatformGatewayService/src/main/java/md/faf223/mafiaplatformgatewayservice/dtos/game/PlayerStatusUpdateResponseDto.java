package md.faf223.mafiaplatformgatewayservice.dtos.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatusUpdateResponseDto {
    @JsonProperty("data")
    private PlayerStatusUpdateData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerStatusUpdateData {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("previousStatus")
        private String previousStatus;

        @JsonProperty("newStatus")
        private String newStatus;

        @JsonProperty("cause")
        private String cause;

        @JsonProperty("dayNumber")
        private Integer dayNumber;
    }
}
