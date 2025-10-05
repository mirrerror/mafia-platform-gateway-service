package md.faf223.mafiaplatformgatewayservice.dtos.communicationservice;

import lombok.Data;
import java.util.Map;

@Data
public class PrivateChannelResponseDto {
    private String name;
    private Map<String, Boolean> members;
}