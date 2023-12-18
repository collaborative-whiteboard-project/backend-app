package pl.polak.nikodem.whiteboard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.polak.nikodem.whiteboard.entities.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
