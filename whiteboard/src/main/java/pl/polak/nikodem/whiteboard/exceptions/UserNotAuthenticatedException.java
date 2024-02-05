package pl.polak.nikodem.whiteboard.exceptions;

public class UserNotAuthenticatedException extends Exception {
    public UserNotAuthenticatedException(String msg) {
        super(msg);
    }
}
