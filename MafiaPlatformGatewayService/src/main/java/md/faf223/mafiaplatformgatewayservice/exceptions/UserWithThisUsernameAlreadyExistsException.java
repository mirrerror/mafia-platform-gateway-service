package md.faf223.mafiaplatformgatewayservice.exceptions;

public class UserWithThisUsernameAlreadyExistsException extends RuntimeException {
    public UserWithThisUsernameAlreadyExistsException(String message) {
        super(message);
    }
}
