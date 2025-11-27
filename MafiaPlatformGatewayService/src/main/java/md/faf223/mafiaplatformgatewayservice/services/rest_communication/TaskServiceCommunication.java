package md.faf223.mafiaplatformgatewayservice.services.rest_communication;

import md.faf223.mafiaplatformgatewayservice.dtos.AssignTasksBody;
import md.faf223.mafiaplatformgatewayservice.dtos.tasks.*;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.responses.MovementEventResponse;
import md.faf223.mafiaplatformgatewayservice.services.DiscoveryServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceCommunication extends BaseRestCommunication {

    public TaskServiceCommunication(DiscoveryServiceClient discoveryServiceClient) {
        super("task-service", discoveryServiceClient);
    }

    public AssignTasksResponse assignTasks(long gameId, long playerId, AssignTasksBody body) {
        return makePostRequest(
                String.format("/tasks/assign/%d/%d", gameId, playerId),
                body,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public TasksListResponse getTasks(Long playerId, Long gameId) {
        String uri = String.format("/player/%d/tasks?gameId=%d%s",
                playerId, gameId, "");
        return makeGetRequest(
                uri,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public TasksListResponse getTasks(Long gameId) {
        String uri = String.format("/tasks/%d", gameId);
        return makeGetRequest(
                uri,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public UpdateStatusResponse updateStatus(long gameId, long  taskId, UpdateStatusBody body) {
        return makePutRequest(
                String.format("/tasks/%d/%d/status", gameId, taskId),
                body,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public MovementEventResponse postMovement(MovementEventBody body) {
        return makePostRequest(
                "/events/movement",
                body,
                new ParameterizedTypeReference<>() {
                }
        );
    }
}


