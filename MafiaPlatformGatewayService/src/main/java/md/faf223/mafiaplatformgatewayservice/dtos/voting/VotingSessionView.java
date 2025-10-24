package md.faf223.mafiaplatformgatewayservice.dtos.voting;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotingSessionView {
    private long sessionId;
    private int dayNumber;
    private List<VoteAggregate> votes;
    private int totalVotes;
}
