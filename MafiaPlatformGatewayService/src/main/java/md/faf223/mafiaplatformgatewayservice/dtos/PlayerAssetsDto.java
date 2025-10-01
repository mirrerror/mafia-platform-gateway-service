package md.faf223.mafiaplatformgatewayservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlayerAssetsDto {
    private Long hair;
    private Long shirt;
    private Long pants;
    private List<Long> accessories;
}
