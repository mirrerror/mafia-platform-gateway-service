package md.faf223.mafiaplatformgatewayservice.exceptions;

public class InvalidJWTTokenException extends RuntimeException {
    public InvalidJWTTokenException(String message) {
        super(message);
    }
}
