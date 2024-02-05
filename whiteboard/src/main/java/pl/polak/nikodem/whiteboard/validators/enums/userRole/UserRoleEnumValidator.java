package pl.polak.nikodem.whiteboard.validators.enums.userRole;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserRoleEnumValidatorConstraint.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@NotNull
public @interface UserRoleEnumValidator {

    Class<? extends Enum<?>> enumClass();
    String message() default "not_an_enum {enum}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
