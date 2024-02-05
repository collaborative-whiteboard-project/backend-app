package pl.polak.nikodem.whiteboard.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import pl.polak.nikodem.whiteboard.enums.ProjectMemberRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users_projects", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "project_id"}))
public class UserProject {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false)
    private ProjectMemberRole memberRole;
}


