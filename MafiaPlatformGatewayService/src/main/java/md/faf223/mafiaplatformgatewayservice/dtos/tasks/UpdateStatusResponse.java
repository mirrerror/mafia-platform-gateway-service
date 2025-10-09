package md.faf223.mafiaplatformgatewayservice.dtos.tasks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusResponse {
    private long taskId;
    private String status;
    private Reward reward;
}


