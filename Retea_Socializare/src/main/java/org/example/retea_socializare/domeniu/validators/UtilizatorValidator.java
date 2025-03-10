package org.example.retea_socializare.domeniu.validators;

import org.example.retea_socializare.domeniu.Utilizator;
import org.example.retea_socializare.exceptions.ValidationException;

public class UtilizatorValidator implements Validator<Utilizator> {
    /**
     * Method for validating a user
     *
     * @param entity - the user to be validated
     * @throws ValidationException if the user is not valid
     */
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        String toThrow;
        toThrow = validateFirstName(entity) + validateLastName(entity) + validateUsername(entity) + validatePassword(entity);
        if (toThrow.isEmpty()) {
            return;
        }
        throw new ValidationException(toThrow);
    }

    /**
     * Method for validating the first name of a user
     *
     * @param entity - the user to be validated
     * @return an empty string if the first name is valid, an error message otherwise
     */
    private String validateFirstName(Utilizator entity) {
        String toThrow = "";
        String firstName = entity.getFirstName();
        if (firstName == null || firstName.trim().isEmpty()) {
            toThrow += "Prenumele este necesar";
        }
        return toThrow;
    }

    /**
     * Method for validating the last name of a user
     *
     * @param entity - the user to be validated
     * @return an empty string if the last name is valid, an error message otherwise
     */
    private String validateLastName(Utilizator entity) {
        String toThrow = "";
        String lastName = entity.getLastName();
        if (lastName == null || lastName.trim().isEmpty()) {
            toThrow += "Numele este necesar";
        }
        return toThrow;
    }

    /**
     * Method for validating the username of a user
     *
     * @param entity - the user to be validated
     * @return an empty string if the username is valid, an error message otherwise
     */
    private String validateUsername(Utilizator entity) {
        String toThrow = "";
        String username = entity.getUsername();
        if (username == null || username.trim().isEmpty()) {
            toThrow += "Username-ul este necesar";
        }
        return toThrow;
    }

    /**
     * Method for validating the password of a user
     * @param entity - the user to be validated
     * @return an empty string if the password is valid, an error message otherwise
     */
    private String validatePassword(Utilizator entity){
       String toThrow = "";
       String password = entity.getPassword();
       if (password == null || password.trim().isEmpty()){
           toThrow += "Parola este necesara";
       }
       return toThrow;
    }
}
