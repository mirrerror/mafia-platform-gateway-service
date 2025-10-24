package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InformationResponse implements IResponse {

    private String message;
    private long timestamp;

    public InformationResponse(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

}