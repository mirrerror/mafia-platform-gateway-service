package md.faf223.mafiaplatformgatewayservice.dtos.voting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteStatusResponse {
    private String state; // e.g., OPEN, CLOSED
    private long startedAt;
    private long endsAt;
    private Map<String, Integer> tallies; // option -> count
}


