package md.faf223.mafiaplatformgatewayservice.dtos.voting;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteChangeDto {
    private long targetPlayerId; // >= 1
}
