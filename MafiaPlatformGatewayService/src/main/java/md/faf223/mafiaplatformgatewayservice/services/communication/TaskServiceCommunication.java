package md.faf223.mafiaplatformgatewayservice.services.communication;

import md.faf223.mafiaplatformgatewayservice.dtos.tasks.*;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceCommunication extends BaseCommunication {

    public TaskServiceCommunication(@Value("${TASK_SERVICE_HOST}") String baseUrl,
                                   @Value("${TASK_SERVICE_PORT}") String port) {
        super(baseUrl, port, "TaskService");
    }

    public AssignTasksResponse assignTasks(long gameId, long playerId) {
        return makePostRequest(
                String.format("/tasks/assign/%d/%d", gameId, playerId),
                null,
                new ParameterizedTypeReference<ApiResponse<AssignTasksResponse>>() {}
        );
    }

    public TasksListResponse getTasks(Long playerId, Long gameId) {
        String uri = String.format("/player/%d/tasks?gameId=%d%s",
                playerId, gameId, "");
        return makeGetRequest(
                uri,
                new ParameterizedTypeReference<ApiResponse<TasksListResponse>>() {}
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
}


