package md.faf223.mafiaplatformgatewayservice.controllers;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.dtos.tasks.*;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.services.communication.TaskServiceCommunication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TasksController {

    private final TaskServiceCommunication communication;
    private static final String BULKHEAD_NAME = "gatewayApi";

    @PostMapping("/assign/{gameId}/{playerId}")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<AssignTasksResponse> assign(
            @PathVariable long gameId, 
            @PathVariable long playerId,
            @RequestBody AssignTasksBody body) {
        return new ApiResponse<>(communication.assignTasks(gameId, playerId, body));
    }

    @GetMapping("/player/{playerId}/tasks")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<TasksListResponse> listTasks(
            @PathVariable Long playerId,
            @RequestParam Long gameId) {
        System.out.println("GameId: " + gameId);
        return new ApiResponse<>(communication.getTasks(playerId, gameId));
    }

    @GetMapping("/game/{gameId}")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<TasksListResponse> listAllTasks(
            @PathVariable Long gameId) {
        return new ApiResponse<>(communication.getTasks(gameId));
    }

    @PutMapping("/{gameId}/{taskId}/status")
    @Bulkhead(name = BULKHEAD_NAME)
    public ApiResponse<UpdateStatusResponse> updateStatus(
            @PathVariable long gameId,
            @PathVariable long taskId,
            @RequestBody UpdateStatusBody body) {
        log.info("Got request to update status for task with id: " + taskId);
        return new ApiResponse<>(communication.updateStatus(gameId, taskId, body));
    }
}


