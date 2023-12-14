package pl.polak.nikodem.whiteboard.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ElementType {
    RECT("rectangle"),
    CIRCLE("circle"),
    LINE("line"),
    PATH("path"),
    TEXT("text");
    private final String type;
}
