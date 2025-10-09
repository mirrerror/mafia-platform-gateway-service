package md.faf223.mafiaplatformgatewayservice.dtos.tasks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TasksListResponse {
    private List<TaskView> tasks;
}


