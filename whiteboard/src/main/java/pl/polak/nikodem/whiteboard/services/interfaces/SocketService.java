package pl.polak.nikodem.whiteboard.services.interfaces;

import com.corundumstudio.socketio.SocketIOClient;
import pl.polak.nikodem.whiteboard.dtos.socket.WhiteboardOperationData;
import pl.polak.nikodem.whiteboard.exceptions.*;

public interface SocketService {

    void joinRoom(SocketIOClient client, String jwtToken, String projectId) throws UserNotAuthenticatedException, ProjectNotFoundException, UserNotAProjectMemberException, InsufficientProjectMemberRoleException, UserNotFoundException;
    void leaveRoom(SocketIOClient client, String jwtToken, String projectId) throws UserNotAuthenticatedException, ProjectNotFoundException;
    void disconnectUser(SocketIOClient client) throws ProjectNotFoundException;
    void editWhiteboard(SocketIOClient client, WhiteboardOperationData data);
    void sendErrorMessage(SocketIOClient client, String errorMessage);
}
