package md.faf223.mafiaplatformgatewayservice.dtos.communicationservice;

import lombok.Data;

import java.util.List;

@Data
public class PrivateChannelDto {
    private String channelName;
    private List<Long> memberIds;
}