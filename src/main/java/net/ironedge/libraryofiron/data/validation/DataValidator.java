package net.ironedge.libraryofiron.data.validation;

import java.util.ArrayList;
import java.util.List;

public final class DataValidator<T> {

    private final List<String> errors = new ArrayList<>();

    public void validate(T object, Validator<T> validator) {
        String error = validator.validate(object);
        if (error != null) {
            errors.add(error);
        }
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void throwIfInvalid() {
        if (hasErrors()) {
            // Improved error message for better debugging
            throw new RuntimeException("Data validation failed with the following errors: " + String.join(", ", errors));
        }
    }

    @FunctionalInterface
    public interface Validator<T> {
        String validate(T obj); // return null if valid, error message if invalid
    }
}