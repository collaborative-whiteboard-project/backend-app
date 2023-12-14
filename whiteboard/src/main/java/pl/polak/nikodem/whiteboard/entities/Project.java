package pl.polak.nikodem.whiteboard.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import pl.polak.nikodem.whiteboard.helpers.JsonConverter;
import pl.polak.nikodem.whiteboard.models.WhiteboardElement;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "projects", schema = "public")
public class Project {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Convert(converter = JsonConverter.class)
    @Column(name = "whiteboard_elements" ,columnDefinition = "json")
    @ColumnTransformer(write = "?::json")
    private List<WhiteboardElement> whiteboardElementsJSON;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at")
    private Instant modifiedAt;
}
