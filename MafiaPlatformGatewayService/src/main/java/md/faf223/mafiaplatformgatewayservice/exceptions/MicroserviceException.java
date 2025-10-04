package md.faf223.mafiaplatformgatewayservice.exceptions;

public class MicroserviceException extends RuntimeException {
    private final int statusCode;
    private final String errorBody;

    public MicroserviceException(int statusCode, String errorBody) {
        super(errorBody);
        this.statusCode = statusCode;
        this.errorBody = errorBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorBody() {
        return errorBody;
    }
}
