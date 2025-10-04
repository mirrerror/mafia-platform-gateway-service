package md.faf223.mafiaplatformgatewayservice.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AssetDto {
    private String slot;
    private Long assetId;
}
