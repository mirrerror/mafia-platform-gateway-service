// src/main/java/md/faf223/mafiaplatformgatewayservice/services/communication/VotingServiceCommunication.java
package md.faf223.mafiaplatformgatewayservice.services.communication;

import md.faf223.mafiaplatformgatewayservice.dtos.voting.*;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.responses.FinalizeResponse;
import md.faf223.mafiaplatformgatewayservice.responses.VoteResponse;
import md.faf223.mafiaplatformgatewayservice.responses.VotesListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class VotingServiceCommunication extends BaseCommunication {

    public VotingServiceCommunication(
            @Value("${VOTING_SERVICE_HOST}") String baseUrl,
            @Value("${VOTING_SERVICE_PORT}") String port
    ) {
        super(baseUrl, port, "VotingService");
    }


    // ---------- Votes ----------
    public VoteResponse createVote(VoteCreateDto body) {
        String uri = "/vote";
        return makePostRequest(
                uri,
                body,
                new ParameterizedTypeReference<ApiResponse<VoteResponse>>() {}
        );
    }

    public VoteResponse changeVote(long voteId, VoteChangeDto body) {
        String uri = String.format("/vote/%d", voteId);
        return makePutRequest(
                uri,
                body,
                new ParameterizedTypeReference<ApiResponse<VoteResponse>>() {}
        );
    }

    public VotesListResponse getVotes(long gameId) {
        String uri = String.format("/votes/%d", gameId);
        return makeGetRequest(
                uri,
                new ParameterizedTypeReference<ApiResponse<VotesListResponse>>() {}
        );
    }

    public FinalizeResponse finalizeDay(long gameId) {
        String uri = String.format("/vote/finalize/%d", gameId);
        return makePostRequest(
                uri,
                null,
                new ParameterizedTypeReference<ApiResponse<FinalizeResponse>>() {}
        );
    }
}
