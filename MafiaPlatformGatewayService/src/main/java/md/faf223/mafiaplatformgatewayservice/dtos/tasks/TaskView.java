package md.faf223.mafiaplatformgatewayservice.dtos.tasks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskView {
    private long id;
    private String name;
    private String description;
    private Reward reward;
    private String status;
    private String location;
}


