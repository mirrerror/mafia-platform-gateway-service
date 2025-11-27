package md.faf223.mafiaplatformgatewayservice.services.grpc_communication;

import md.faf223.mafiaplatformgatewayservice.dtos.voting.VoteAggregate;
import md.faf223.mafiaplatformgatewayservice.dtos.voting.VoteChangeDto;
import md.faf223.mafiaplatformgatewayservice.dtos.voting.VoteCreateDto;
import md.faf223.mafiaplatformgatewayservice.dtos.voting.VotingSessionView;
import md.faf223.mafiaplatformgatewayservice.grpc.voting.VotingServiceGrpc;
import md.faf223.mafiaplatformgatewayservice.grpc.voting.VotingServiceProto;
import md.faf223.mafiaplatformgatewayservice.responses.FinalizeResponse;
import md.faf223.mafiaplatformgatewayservice.responses.VoteResponse;
import md.faf223.mafiaplatformgatewayservice.responses.VotesListResponse;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VotingServiceGrpcCommunication extends BaseGrpcCommunication {

    public VotingServiceGrpcCommunication(DiscoveryServiceClient discoveryServiceClient) {
        super("voting-service", discoveryServiceClient);
    }

    public VoteResponse createVote(VoteCreateDto body) {
        VotingServiceProto.VoteResponse grpcResponse = executeGrpcCall(channel -> {
            VotingServiceGrpc.VotingServiceBlockingStub stub = VotingServiceGrpc.newBlockingStub(channel);
            return stub.createVote(VotingServiceProto.CreateVoteRequest.newBuilder()
                    .setGameId(body.getGameId())
                    .setVoterId(body.getVoterId())
                    .setTargetPlayerId(body.getTargetPlayerId())
                    .build());
        });

        return toVoteResponse(grpcResponse);
    }

    public VoteResponse changeVote(long voteId, VoteChangeDto body) {
        VotingServiceProto.VoteResponse grpcResponse = executeGrpcCall(channel -> {
            VotingServiceGrpc.VotingServiceBlockingStub stub = VotingServiceGrpc.newBlockingStub(channel);
            return stub.changeVote(VotingServiceProto.ChangeVoteRequest.newBuilder()
                    .setVoteId(voteId)
                    .setTargetPlayerId(body.getTargetPlayerId())
                    .build());
        });

        return toVoteResponse(grpcResponse);
    }

    public VotesListResponse getVotes(long gameId) {
        VotingServiceProto.VotesListResponse grpcResponse = executeGrpcCall(channel -> {
            VotingServiceGrpc.VotingServiceBlockingStub stub = VotingServiceGrpc.newBlockingStub(channel);
            return stub.getGameVotes(VotingServiceProto.GetGameVotesRequest.newBuilder()
                    .setGameId(gameId)
                    .build());
        });

        // Convert List<ProtoSession> to List<DtoSession>
        List<VotingSessionView> sessions = grpcResponse.getVotingSessionsList().stream()
                .map(this::toSessionView)
                .collect(Collectors.toList());

        return VotesListResponse.builder()
                .votingSessions(sessions)
                .build();
    }

    public FinalizeResponse finalizeDay(long gameId) {
        VotingServiceProto.FinalizeResponse grpcResponse = executeGrpcCall(channel -> {
            VotingServiceGrpc.VotingServiceBlockingStub stub = VotingServiceGrpc.newBlockingStub(channel);
            return stub.finalizeVoting(VotingServiceProto.FinalizeVotingRequest.newBuilder()
                    .setGameId(gameId)
                    .build());
        });

        return FinalizeResponse.builder()
                .dayNumber(grpcResponse.hasDayNumber() ? grpcResponse.getDayNumber() : null)
                .targetPlayerId(grpcResponse.hasTargetPlayerId() ? grpcResponse.getTargetPlayerId() : null)
                .voteCount(grpcResponse.getVoteCount())
                .totalVoters(grpcResponse.getTotalVoters())
                .finalized(grpcResponse.getFinalized())
                .votingBreakdown(grpcResponse.getVotingBreakdownMap()) // Map<String, Integer>
                .build();
    }

    // --- Helper Mappers ---

    private VoteResponse toVoteResponse(VotingServiceProto.VoteResponse proto) {
        return VoteResponse.builder()
                .voteId(proto.getVoteId())
                .voterId(proto.getVoterId())
                .targetPlayerId(proto.getTargetPlayerId())
                .build();
    }

    private VotingSessionView toSessionView(VotingServiceProto.VotingSessionView proto) {
        List<VoteAggregate> votes = proto.getVotesList().stream()
                .map(v -> new VoteAggregate(v.getTargetPlayerId(), v.getVoteCount()))
                .collect(Collectors.toList());

        return new VotingSessionView(
                proto.getSessionId(),
                proto.getDayNumber(),
                votes,
                proto.getTotalVotes()
        );
    }
}
