package bg.softuni.matchday.exception;

public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException(String message) {
        super(message);
    }

    public UserDoesNotExistException(){

    }
}
