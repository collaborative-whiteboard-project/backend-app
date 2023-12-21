package pl.polak.nikodem.whiteboard.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.polak.nikodem.whiteboard.entities.User;
import pl.polak.nikodem.whiteboard.repositories.UserRepository;
import pl.polak.nikodem.whiteboard.services.interfaces.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository.findByEmail(username)
                                     .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
            }
        };
    }

    public User save(User newUser) {
        return userRepository.save(newUser);
    }
}
