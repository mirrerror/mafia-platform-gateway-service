package md.faf223.mafiaplatformgatewayservice.controllers;

import md.faf223.mafiaplatformgatewayservice.dtos.roleplayservice.NightEventRequest;
import md.faf223.mafiaplatformgatewayservice.dtos.roleplayservice.NightEventResponse;
import md.faf223.mafiaplatformgatewayservice.dtos.roleplayservice.PhaseUpdateRequest;
import md.faf223.mafiaplatformgatewayservice.dtos.roleplayservice.PhaseUpdateResponse;
import md.faf223.mafiaplatformgatewayservice.responses.ApiResponse;
import md.faf223.mafiaplatformgatewayservice.services.communication.RoleplayServiceCommunication;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class RoleplayController {

    private final RoleplayServiceCommunication roleplayServiceCommunication;
    private static final String BULKHEAD_NAME = "roleplayApi";

    @PostMapping("/phase-update")
    @Bulkhead(name = BULKHEAD_NAME)
    public ResponseEntity<ApiResponse<PhaseUpdateResponse>> phaseUpdate(@RequestBody PhaseUpdateRequest request) {
        PhaseUpdateResponse result = roleplayServiceCommunication.handlePhaseUpdate(request);
        return ResponseEntity.ok(new ApiResponse<>(result));
    }

    @PostMapping("/night-events")
    @Bulkhead(name = BULKHEAD_NAME)
    public ResponseEntity<ApiResponse<NightEventResponse>> registerNightEvent(@RequestBody NightEventRequest request) {
        NightEventResponse result = roleplayServiceCommunication.registerNightEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(result));
    }
}