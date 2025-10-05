package md.faf223.mafiaplatformgatewayservice.exceptions.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.faf223.mafiaplatformgatewayservice.exceptions.*;
import md.faf223.mafiaplatformgatewayservice.responses.InformationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler({
            InvalidJWTTokenException.class,
            InvalidCredentialsException.class,
            InvalidSortDirectionException.class,
            YouCanNotDoThisToYourselfException.class,
            IncorrectPasswordException.class,
            PasswordMismatchException.class
    })
    public ResponseEntity<InformationResponse> handleBadRequest(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new InformationResponse(exception.getMessage()));
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            NotFoundException.class
    })
    public ResponseEntity<InformationResponse> handleNotFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new InformationResponse(exception.getMessage()));
    }

    @ExceptionHandler({
            UserNotAuthenticatedException.class
    })
    public ResponseEntity<InformationResponse> handleUnauthorized(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new InformationResponse(exception.getMessage()));
    }

    @ExceptionHandler({
            InsufficientPermissionsException.class,
            NotYourEmailException.class,
            YouHaveToWaitBeforeDoingThatAgainException.class,
    })
    public ResponseEntity<InformationResponse> handleForbidden(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new InformationResponse(exception.getMessage()));
    }

    @ExceptionHandler({
            UserWithThisEmailAlreadyExistsException.class,
    })
    public ResponseEntity<InformationResponse> handleConflict(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new InformationResponse(exception.getMessage()));
    }

    @ExceptionHandler(MicroserviceException.class)
    public ResponseEntity<String> handleServiceException(MicroserviceException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.getErrorBody());
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<InformationResponse> handleAllOtherExceptions(Exception exception) {
        exception.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new InformationResponse("An unexpected error occurred: " + exception.getMessage()));
    }

//    @MessageExceptionHandler(UserNotAuthenticatedException.class)
//    public void handleAuthException(UserNotAuthenticatedException ex, Authentication authentication) {
//        log.warn("User not authenticated: {}", ex.getMessage());
//
//        Map<String, Object> errorResponse = Map.of(
//                "error", "NOT_AUTHENTICATED",
//                "message", ex.getMessage(),
//                "type", "UserNotAuthenticatedException",
//                "timestamp", System.currentTimeMillis()
//        );
//
//        if (authentication != null) {
//            try {
//                User currentUser = userService.getCurrentUserOrElseThrow(authentication);
//                String userErrorTopic = "/api/topic/user-" + currentUser.getId() + "/errors";
//                simpMessagingTemplate.convertAndSend(userErrorTopic, errorResponse);
//            } catch (Exception e) {
//                log.error("Failed to send auth error to user topic: {}", e.getMessage());
//            }
//        }
//    }

}