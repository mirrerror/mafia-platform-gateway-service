package md.faf223.mafiaplatformgatewayservice.dtos.voting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteResultResponse {
    private String outcome; // e.g., targetId as string or policy name
    private Map<String, Integer> tallies;
}


