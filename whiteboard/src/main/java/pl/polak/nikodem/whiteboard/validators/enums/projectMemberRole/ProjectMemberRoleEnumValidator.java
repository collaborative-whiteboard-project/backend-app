package pl.polak.nikodem.whiteboard.validators.enums.projectMemberRole;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ProjectMemberRoleEnumValidatorConstraint.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@NotNull
public @interface ProjectMemberRoleEnumValidator {

    Class<? extends Enum<?>> enumClass();
    String message() default "not_an_enum {enum}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
