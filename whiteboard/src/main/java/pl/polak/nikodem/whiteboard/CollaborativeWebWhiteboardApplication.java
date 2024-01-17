package pl.polak.nikodem.whiteboard;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class CollaborativeWebWhiteboardApplication implements CommandLineRunner {

    private final SocketIOServer server;
    public static void main(String[] args) {
        SpringApplication.run(CollaborativeWebWhiteboardApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        server.start();
    }
}