package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import md.faf223.mafiaplatformgatewayservice.dtos.PlayerAssetsDto;

@Data
@AllArgsConstructor
public class PlayerAssetsResponse {
    private PlayerAssetsDto assets;
}
