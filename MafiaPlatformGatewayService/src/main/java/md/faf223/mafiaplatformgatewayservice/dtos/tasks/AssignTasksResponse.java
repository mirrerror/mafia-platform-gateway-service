package md.faf223.mafiaplatformgatewayservice.dtos.tasks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignTasksResponse {
    private long playerId;
    private long gameId;
    private String playerCareer;
    private String playerRole;
    private List<TaskView> tasks;
}


