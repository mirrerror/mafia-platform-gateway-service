package md.faf223.mafiaplatformgatewayservice.dtos.voting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CastVoteDto {
    private long voterId;
    private long choiceId; // could be target player id or option id
}


