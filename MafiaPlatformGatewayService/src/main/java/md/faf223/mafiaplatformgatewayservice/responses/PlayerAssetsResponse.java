package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import md.faf223.mafiaplatformgatewayservice.dtos.PlayerAssetsDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerAssetsResponse {
    private PlayerAssetsDto assets;
}
