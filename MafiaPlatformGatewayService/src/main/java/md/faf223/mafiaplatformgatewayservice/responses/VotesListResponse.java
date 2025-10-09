package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.*;
import md.faf223.mafiaplatformgatewayservice.dtos.voting.VotingSessionView;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotesListResponse {
    // FastAPI returns: { "data": { "votingSessions": [...] } }
    // BaseCommunication should already unwrap top-level ApiResponse, so we mirror the inner "data".
    private List<VotingSessionView> votingSessions;
}
