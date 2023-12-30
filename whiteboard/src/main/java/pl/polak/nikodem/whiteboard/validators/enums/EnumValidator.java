package pl.polak.nikodem.whiteboard.validators.enums;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumValidatorConstraint.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@NotNull
public @interface EnumValidator {

    Class<? extends Enum<?>> enumClass();
    String message() default "not_an_enum {enum}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
