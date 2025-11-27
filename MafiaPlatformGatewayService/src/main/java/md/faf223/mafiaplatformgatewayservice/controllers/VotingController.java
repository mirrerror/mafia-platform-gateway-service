// src/main/java/md/faf223/mafiaplatformgatewayservice/controllers/VotingController.java
package md.faf223.mafiaplatformgatewayservice.controllers;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.voting.VoteChangeDto;
import md.faf223.mafiaplatformgatewayservice.dtos.voting.VoteCreateDto;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.responses.FinalizeResponse;
import md.faf223.mafiaplatformgatewayservice.responses.VoteResponse;
import md.faf223.mafiaplatformgatewayservice.responses.VotesListResponse;
import md.faf223.mafiaplatformgatewayservice.services.grpc_communication.VotingServiceGrpcCommunication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voting")
@RequiredArgsConstructor
public class VotingController {

    private final VotingServiceGrpcCommunication communication;
    private static final String BULKHEAD_NAME = "gatewayApi";

    @PostMapping("/vote")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<VoteResponse> createVote(@RequestBody VoteCreateDto body) {
        return new ApiResponse<>(communication.createVote(body));
    }

    @PutMapping("/vote/{voteId}")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<VoteResponse> changeVote(@PathVariable long voteId, @RequestBody VoteChangeDto body) {
        return new ApiResponse<>(communication.changeVote(voteId, body));
    }

    @GetMapping("/{gameId}/votes")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<VotesListResponse> getVotes(@PathVariable long gameId) {
        return new ApiResponse<>(communication.getVotes(gameId));
    }

    @PostMapping("/{gameId}/finalize")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<FinalizeResponse> finalizeDay(@PathVariable long gameId) {
        return new ApiResponse<>(communication.finalizeDay(gameId));
    }
}
