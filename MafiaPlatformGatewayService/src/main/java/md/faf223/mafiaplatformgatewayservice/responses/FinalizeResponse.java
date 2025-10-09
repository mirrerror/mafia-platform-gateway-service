package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalizeResponse {
    private Integer dayNumber;             // can be null if nothing to finalize
    private Long targetPlayerId;           // winner (could be null)
    private int voteCount;                 // top count
    private int totalVoters;               // total votes cast
    private Map<String, Integer> votingBreakdown; // targetId -> count
    private boolean finalized;             // true if finalization occurred
}
