package pl.polak.nikodem.whiteboard.dtos.socket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JoinProjectData {
    private String projectId;
    private String jwtToken;
}
