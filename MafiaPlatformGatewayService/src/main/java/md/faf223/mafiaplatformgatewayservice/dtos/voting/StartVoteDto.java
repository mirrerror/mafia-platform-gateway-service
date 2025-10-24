package md.faf223.mafiaplatformgatewayservice.dtos.voting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartVoteDto {
    private long initiatorId;
    private String topic; // e.g., "eliminate", "policy", etc.
    private long targetId; // optional by service rules, use 0 if N/A
}


