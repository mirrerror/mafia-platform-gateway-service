package md.faf223.mafiaplatformgatewayservice.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ServiceInstance {
    private String serviceId;
    private String instanceId;
    private String host;
    private Integer port;
    private String lastHeartbeat;
    private String status;
    private Map<String, String> metadata;
    private Integer errorCount;
    private String lastErrorTime;
    private String healthCheckUrl;

    @JsonProperty("circuitBreakerOpen")
    private boolean circuitBreakerOpen;

    private String serviceUrl;
}
