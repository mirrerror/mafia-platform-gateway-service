package md.faf223.mafiaplatformgatewayservice.dtos.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameEventsResponseDto {
    @JsonProperty("data")
    private GameEventsData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameEventsData {
        @JsonProperty("events")
        private List<GameEvent> events;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameEvent {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("type")
        private String type;

        @JsonProperty("message")
        private String message;
    }
}
