package md.faf223.mafiaplatformgatewayservice.dtos.voting;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteAggregate {
    private long targetPlayerId;
    private int voteCount;
}
