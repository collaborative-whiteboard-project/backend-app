package pl.polak.nikodem.whiteboard.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.polak.nikodem.whiteboard.dtos.socket.CreateWhiteboardElementData;
import pl.polak.nikodem.whiteboard.dtos.socket.DeleteWhiteboardElementData;
import pl.polak.nikodem.whiteboard.dtos.socket.UpdateWhiteboardElementData;
import pl.polak.nikodem.whiteboard.dtos.socket.WhiteboardOperationData;
import pl.polak.nikodem.whiteboard.entities.Project;
import pl.polak.nikodem.whiteboard.enums.ElementType;
import pl.polak.nikodem.whiteboard.exceptions.ProjectNotFoundException;
import pl.polak.nikodem.whiteboard.models.Rectangle;
import pl.polak.nikodem.whiteboard.models.WhiteboardElement;
import pl.polak.nikodem.whiteboard.repositories.ProjectRepository;
import pl.polak.nikodem.whiteboard.services.implementations.ProjectServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    public void ProjectService_SaveProjectChangesToDatabase() throws ProjectNotFoundException {
        Project project = Project.builder().whiteboardElementsJSON(new ArrayList<>()).build();
        List<WhiteboardElement> expectedContent = getCorrectWhiteboardContent();

        when(projectRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(project));
        when(projectRepository.save(Mockito.any(Project.class))).thenReturn(project);

        List<WhiteboardElement> projectContent = this.projectService.saveProjectChangesToDatabase("1", getWhiteboardOperationTestData());

        Assertions.assertNotNull(projectContent);
        Assertions.assertEquals(projectContent.size(), expectedContent.size());
        projectContent.forEach(element -> {
            if (element instanceof Rectangle rect) {
                Assertions.assertTrue(expectedContent.contains(rect));
            }
        });
    }


    private List<WhiteboardOperationData> getWhiteboardOperationTestData() {
        List<WhiteboardOperationData> operations = new ArrayList<>();
        operations.addAll(List.of(
                                    CreateWhiteboardElementData.builder()
                                                             .projectId("1")
                                                             .timestamp(LocalDateTime.now())
                                                             .element(getRectangle("1"))
                                                             .build(),
                                    CreateWhiteboardElementData.builder()
                                                               .projectId("1")
                                                               .timestamp(LocalDateTime.now())
                                                               .element(getRectangle("2"))
                                                               .build(),
                                    CreateWhiteboardElementData.builder()
                                                               .projectId("1")
                                                               .timestamp(LocalDateTime.now())
                                                               .element(getRectangle("3"))
                                                               .build(),
                                    UpdateWhiteboardElementData.builder()
                                                               .projectId("1")
                                                               .id("2")
                                                               .propertyName("width")
                                                               .propertyValue("300")
                                                               .timestamp(LocalDateTime.now())
                                                               .build(),
                                    UpdateWhiteboardElementData.builder()
                                                                .projectId("1")
                                                                .id("1")
                                                                .propertyName("fill-color")
                                                                .propertyValue("black")
                                                                .timestamp(LocalDateTime.now())
                                                                .build(),
                                    DeleteWhiteboardElementData.builder()
                                                                .projectId("1")
                                                                .id("3")
                                                                .timestamp(LocalDateTime.now())
                                                                .build(),
                                    CreateWhiteboardElementData.builder()
                                                                .projectId("1")
                                                                .timestamp(LocalDateTime.now())
                                                                .element(getRectangle("4"))
                                                                .build())
        );

        return operations;
    }

    private Rectangle getRectangle(String id) {
        return Rectangle.builder()
                        .id(id)
                        .elementType(ElementType.RECTANGLE)
                        .xPosition("100")
                        .yPosition("100")
                        .width("200")
                        .height("100")
                        .strokeWidth("1")
                        .strokeColor("black")
                        .fillColor("white")
                        .fillOpacity("1")
                        .transform("translate(0,0)")
                        .build();

    }

    private List<WhiteboardElement> getCorrectWhiteboardContent() {
        Rectangle rect1 = getRectangle("1");
        Rectangle rect2 = getRectangle("2");
        Rectangle rect4 = getRectangle("4");
        rect2.setWidth("300");
        rect1.setFillColor("black");
        return List.of(rect1, rect2, rect4);
    }
}
