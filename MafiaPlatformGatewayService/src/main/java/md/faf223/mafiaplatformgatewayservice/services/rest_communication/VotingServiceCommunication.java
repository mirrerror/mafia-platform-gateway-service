package md.faf223.mafiaplatformgatewayservice.services.rest_communication;

import md.faf223.mafiaplatformgatewayservice.dtos.voting.*;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.responses.FinalizeResponse;
import md.faf223.mafiaplatformgatewayservice.responses.VoteResponse;
import md.faf223.mafiaplatformgatewayservice.responses.VotesListResponse;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class VotingServiceCommunication extends BaseRestCommunication {

    public VotingServiceCommunication(
            DiscoveryServiceClient discoveryServiceClient
    ) {
        super("voting-service", discoveryServiceClient);
    }

    public VoteResponse createVote(VoteCreateDto body) {
        String uri = "/vote";
        return makePostRequest(
                uri,
                body,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public VoteResponse changeVote(long voteId, VoteChangeDto body) {
        String uri = String.format("/vote/%d", voteId);
        return makePutRequest(
                uri,
                body,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public VotesListResponse getVotes(long gameId) {
        String uri = String.format("/votes/%d", gameId);
        return makeGetRequest(
                uri,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public FinalizeResponse finalizeDay(long gameId) {
        String uri = String.format("/vote/finalize/%d", gameId);
        return makePostRequest(
                uri,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
    }
}
