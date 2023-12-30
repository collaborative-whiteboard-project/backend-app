package pl.polak.nikodem.whiteboard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.polak.nikodem.whiteboard.entities.UserProject;

public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
}
