package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetResponse {
    private Long assetId;
    private boolean equipped;
}
