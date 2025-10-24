package md.faf223.mafiaplatformgatewayservice.responses;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

public class CustomResponse implements IResponse {

    @Getter
    private final Map<String, Object> response;

    public CustomResponse() {
        this.response = new LinkedHashMap<>();
    }

    public CustomResponse addEntry(String key, Object value) {
        this.response.put(key, value);
        return this;
    }

    public CustomResponse addEntries(Map<String, Object> entries) {
        this.response.putAll(entries);
        return this;
    }

    public void clear() {
        this.response.clear();
    }

}
