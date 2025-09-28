package md.faf223.mafiaplatformgatewayservice.exceptions;

public class UserWithThisEmailAlreadyExistsException extends RuntimeException {
    public UserWithThisEmailAlreadyExistsException(String message) {
        super(message);
    }
}
