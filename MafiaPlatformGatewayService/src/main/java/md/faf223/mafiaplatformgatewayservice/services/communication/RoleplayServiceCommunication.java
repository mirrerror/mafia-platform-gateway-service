package md.faf223.mafiaplatformgatewayservice.services.communication;

import md.faf223.mafiaplatformgatewayservice.dtos.roleplayservice.*;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class RoleplayServiceCommunication extends BaseCommunication {

    public RoleplayServiceCommunication(@Value("${services.roleplay.url}") String baseUrl,
                                        @Value("${services.roleplay.port}") String port,
                                        DiscoveryServiceClient discoveryServiceClient) {
        super("roleplay-service", discoveryServiceClient);
    }

    public PhaseUpdateResponse handlePhaseUpdate(PhaseUpdateRequest request) {
        return makePostRequest(
                "/phase-update",
                request,
                new ParameterizedTypeReference<ApiResponse<PhaseUpdateResponse>>() {}
        );
    }

    public NightEventResponse registerNightEvent(NightEventRequest request) {
        return makePostRequest(
                "/night-events",
                request,
                new ParameterizedTypeReference<ApiResponse<NightEventResponse>>() {}
        );
    }
}