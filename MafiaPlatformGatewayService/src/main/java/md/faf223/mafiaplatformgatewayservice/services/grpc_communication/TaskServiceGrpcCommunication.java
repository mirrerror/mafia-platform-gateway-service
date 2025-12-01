package md.faf223.mafiaplatformgatewayservice.services.grpc_communication;

import md.faf223.mafiaplatformgatewayservice.dtos.AssignTasksBody;
import md.faf223.mafiaplatformgatewayservice.dtos.tasks.*;
import md.faf223.mafiaplatformgatewayservice.responses.MovementEventResponse;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import org.springframework.stereotype.Service;

import md.faf223.mafiaplatformgatewayservice.grpc.task.TaskServiceGrpc;
import md.faf223.mafiaplatformgatewayservice.grpc.task.TaskServiceProto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceGrpcCommunication extends BaseGrpcCommunication {

    public TaskServiceGrpcCommunication(DiscoveryServiceClient discoveryServiceClient) {
        super("task-service", discoveryServiceClient);
    }

    public AssignTasksResponse assignTasks(long gameId, long playerId, AssignTasksBody body) {
        TaskServiceProto.AssignTasksResponse grpcResponse = executeGrpcCall(channel -> {
            TaskServiceGrpc.TaskServiceBlockingStub stub = TaskServiceGrpc.newBlockingStub(channel);
            return stub.assignTask(TaskServiceProto.AssignTaskRequest.newBuilder()
                    .setGameId(gameId)
                    .setPlayerId(playerId)
                    .setCareer(body.getCareer())
                    .setDayNumber(body.getDayNumber())
                    .build());
        });

        return new AssignTasksResponse(
                grpcResponse.getPlayerId(),
                grpcResponse.getGameId(),
                grpcResponse.getPlayerCareer(),
                grpcResponse.getPlayerRole(),
                convertTaskViews(grpcResponse.getTasksList())
        );
    }

    public TasksListResponse getTasks(Long playerId, Long gameId) {
        TaskServiceProto.TasksListResponse grpcResponse = executeGrpcCall(channel -> {
            TaskServiceGrpc.TaskServiceBlockingStub stub = TaskServiceGrpc.newBlockingStub(channel);
            return stub.getPlayerTasks(TaskServiceProto.GetPlayerTasksRequest.newBuilder()
                    .setGameId(gameId)
                    .setPlayerId(playerId)
                    .build());
        });

        return new TasksListResponse(convertTaskViews(grpcResponse.getTasksList()));
    }

    public TasksListResponse getTasks(Long gameId) {
        TaskServiceProto.TasksListResponse grpcResponse = executeGrpcCall(channel -> {
            TaskServiceGrpc.TaskServiceBlockingStub stub = TaskServiceGrpc.newBlockingStub(channel);
            return stub.getAllTasks(TaskServiceProto.GetAllTasksRequest.newBuilder()
                    .setGameId(gameId)
                    .build());
        });

        return new TasksListResponse(convertTaskViews(grpcResponse.getTasksList()));
    }

    public UpdateStatusResponse updateStatus(long gameId, long taskId, UpdateStatusBody body) {
        TaskServiceProto.UpdateStatusResponse grpcResponse = executeGrpcCall(channel -> {
            TaskServiceGrpc.TaskServiceBlockingStub stub = TaskServiceGrpc.newBlockingStub(channel);
            return stub.updateTaskStatus(TaskServiceProto.UpdateTaskStatusRequest.newBuilder()
                    .setGameId(gameId)
                    .setTaskId(taskId)
                    .setStatus(body.getStatus())
                    .build());
        });

        return new UpdateStatusResponse(
                grpcResponse.getTaskId(),
                grpcResponse.getStatus(),
                new Reward(grpcResponse.getReward().getCoins(), grpcResponse.getReward().getDiamonds())
        );
    }

    public MovementEventResponse postMovement(MovementEventBody body) {
        TaskServiceProto.MovementEventResponse grpcResponse = executeGrpcCall(channel -> {
            TaskServiceGrpc.TaskServiceBlockingStub stub = TaskServiceGrpc.newBlockingStub(channel);
            return stub.handleMovement(TaskServiceProto.MovementEventRequest.newBuilder()
                    .setGameId(body.getGameId())
                    .setPlayerId(body.getPlayerId())
                    .setLocationId(Math.toIntExact(body.getLocationId()))
                    .build());
        });

        return new MovementEventResponse(
                grpcResponse.getGameId(),
                grpcResponse.getPlayerId(),
                grpcResponse.getLocationId(),
                grpcResponse.getLocationName(),
                grpcResponse.getUpdatedTaskIdsList(),
                grpcResponse.getResult()
        );
    }


    private List<TaskView> convertTaskViews(List<TaskServiceProto.TaskView> protoList) {
        return protoList.stream()
                .map(t -> new TaskView(
                        t.getId(),
                        t.getName(),
                        t.getDescription(),
                        new Reward(t.getReward().getCoins(), t.getReward().getDiamonds()),
                        t.getStatus(),
                        t.getLocation(),
                        t.getPlayerId(),
                        t.getDayNumber()
                ))
                .collect(Collectors.toList());
    }
}