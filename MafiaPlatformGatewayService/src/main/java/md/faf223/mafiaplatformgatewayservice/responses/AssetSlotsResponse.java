package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AssetSlotsResponse {
    private List<String> slots;
}
