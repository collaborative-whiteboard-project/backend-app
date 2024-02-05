package pl.polak.nikodem.whiteboard.validators.enums.projectMemberRole;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.polak.nikodem.whiteboard.enums.ProjectMemberRole;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectMemberRoleEnumValidatorConstraint implements ConstraintValidator<ProjectMemberRoleEnumValidator, ProjectMemberRole> {

    Set<String> values;

    @Override
    public void initialize(ProjectMemberRoleEnumValidator constraintAnnotation) {
        values = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
                       .map(Enum::name)
                       .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(ProjectMemberRole value, ConstraintValidatorContext constraintValidatorContext) {
        return values.contains(value.name());
    }
}
