package org.example.retea_socializare.domeniu.validators;

import org.example.retea_socializare.domeniu.Friendship;
import org.example.retea_socializare.exceptions.ValidationException;

import java.util.Objects;

public class FriendshipValidator implements Validator<Friendship> {

    /**
     * Method for validating a friendship
     *
     * @param entity - the friendship to be validated
     * @throws ValidationException if the friendship is not valid
     */
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String toThrow = "";

        if (entity.getSince() == null) {
            toThrow += "Data nu poate sa fie nula";
        }

        if (entity.getId().getFirst() == null || entity.getId().getSecond() == null) {
            toThrow += "Id-ul este nul";
        }

        if (Objects.equals(entity.getId().getFirst(), entity.getId().getSecond())) {
            toThrow += "Id-ul mai exita in lista de prietenii";
        }

        if (!toThrow.isEmpty()) {
            throw new ValidationException(toThrow);
        }
    }
}

