package pl.polak.nikodem.whiteboard.services.interfaces;

import org.springframework.security.core.userdetails.UserDetailsService;
import pl.polak.nikodem.whiteboard.entities.User;

public interface UserService {
    UserDetailsService userDetailsService();
    User save(User newUser);
}
