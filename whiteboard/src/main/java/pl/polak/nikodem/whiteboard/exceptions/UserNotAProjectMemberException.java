package pl.polak.nikodem.whiteboard.exceptions;

public class UserNotAProjectMemberException extends Exception {
    public UserNotAProjectMemberException(String msg) {
        super(msg);
    }
}

