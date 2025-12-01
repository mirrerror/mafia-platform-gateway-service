package md.faf223.mafiaplatformgatewayservice.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ServiceInstance {
    private String serviceId;
    private String instanceId;
    private String host;
    private int restPort;
    private int rpcPort;
    private String topicName;
    private Map<String, String> metadata;

    @JsonProperty("circuitBreakerOpen")
    private boolean circuitBreakerOpen;

    private String serviceUrl;

    public String getRPCUrl() {
        return host + ":" + rpcPort;
    }
}
