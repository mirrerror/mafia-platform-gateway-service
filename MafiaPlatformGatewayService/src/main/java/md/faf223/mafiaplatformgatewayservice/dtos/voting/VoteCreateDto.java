package md.faf223.mafiaplatformgatewayservice.dtos.voting;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteCreateDto {
    private long gameId;         // >= 1
    private long voterId;        // >= 1
    private long targetPlayerId; // >= 1
}
