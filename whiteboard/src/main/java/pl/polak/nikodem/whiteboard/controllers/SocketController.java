package pl.polak.nikodem.whiteboard.controllers;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.listener.DataListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.polak.nikodem.whiteboard.dtos.socket.JoinProjectData;
import pl.polak.nikodem.whiteboard.dtos.socket.LeaveProjectData;
import pl.polak.nikodem.whiteboard.dtos.socket.WhiteboardOperationData;
import pl.polak.nikodem.whiteboard.exceptions.ProjectNotFoundException;
import pl.polak.nikodem.whiteboard.services.interfaces.SocketService;

@Component
@Slf4j
public class SocketController {

    private final SocketIOServer socketIOServer;
    private final SocketService socketService;

    SocketController(SocketIOServer socketIOServer, SocketService socketService) {
        this.socketIOServer = socketIOServer;
        this.socketService = socketService;
        this.socketIOServer.addConnectListener(this::onConnected);
        this.socketIOServer.addDisconnectListener(this::onDisconnected);
        this.socketIOServer.addEventListener("joinProject", JoinProjectData.class, onJoinProject());
        this.socketIOServer.addEventListener("leaveProject", LeaveProjectData.class, onLeaveProject());
        this.socketIOServer.addEventListener("editWhiteboard", WhiteboardOperationData.class, onWhiteboardEdit());
    }

    @OnConnect
    private void onConnected(SocketIOClient client) {
        log.debug("Connected");
    }

    @OnDisconnect
    private void onDisconnected(SocketIOClient client) {
        try {
            socketService.disconnectUser(client);
        } catch (ProjectNotFoundException e) {
            this.socketService.sendErrorMessage( client, e.getMessage());
        }
        log.debug("Disconnected");
    }

    private DataListener<JoinProjectData> onJoinProject() {
        return (client, data, ackSender) -> {
            try {
                socketService.joinRoom(client, data.getJwtToken(), data.getProjectId());
            } catch (Exception e) {
                this.socketService.sendErrorMessage( client, e.getMessage());
            }
        };
    }

    private DataListener<WhiteboardOperationData> onWhiteboardEdit() {
        return (client, data, ackSender) -> {
            socketService.editWhiteboard(client, data);
        };
    }

    private DataListener<LeaveProjectData> onLeaveProject() {
        return (client, data, ackSender) -> {
            try {
                socketService.leaveRoom(client, data.getJwtToken(), data.getProjectId());
            } catch (Exception e) {
                this.socketService.sendErrorMessage( client, e.getMessage());
            }
        };
    }
}
