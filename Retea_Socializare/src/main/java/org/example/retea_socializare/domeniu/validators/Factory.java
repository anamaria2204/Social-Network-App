package org.example.retea_socializare.domeniu.validators;

public interface Factory {
    /**
     * Creates Factory for strategy
     *
     * @param strategy Strategy that decides type of Validator
     * @return Validator conform to strategy
     */
    Validator createValidator(ValidatorStrategy strategy);
}

