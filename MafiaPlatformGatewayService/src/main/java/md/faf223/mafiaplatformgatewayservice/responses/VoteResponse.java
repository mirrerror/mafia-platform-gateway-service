package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResponse {
    private long voteId;
    private long voterId;
    private long targetPlayerId;
}
