package pl.polak.nikodem.whiteboard.validators.enums;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.polak.nikodem.whiteboard.enums.UserRole;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserRoleEnumValidatorConstraint implements ConstraintValidator<UserRoleEnumValidator, UserRole> {
    Set<String> values;


    @Override
    public void initialize(UserRoleEnumValidator constraintAnnotation) {
        values = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(UserRole value, ConstraintValidatorContext constraintValidatorContext) {
        return values.contains(value.name());
    }
}
