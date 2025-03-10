package org.example.retea_socializare.repository.file;


import org.example.retea_socializare.domeniu.Utilizator;
import org.example.retea_socializare.domeniu.validators.Validator;

/**
 * CRUD operations for file storage
 */

public class UtilizatorRepository extends AbstractFileRepository<Integer, Utilizator> {

    /**
     * Constructor
     *
     * @param validator - the validator for the entities
     * @param fileName  - the name of the file
     */
    public UtilizatorRepository(Validator<Utilizator> validator, String fileName) {
        super(validator, fileName);
    }

    /**
     * Method for reading a line from the file and converting it to an entity
     *
     * @param line - the line to be converted
     * @return - the entity
     */

    @Override
    public Utilizator lineToEntity(String line) {
        String[] splited = line.split(";");
        Utilizator u = new Utilizator(splited[1], splited[2], splited[3], splited[4]);
        u.setId(Integer.parseInt(splited[0]));
        return u;
    }

    /**
     * Method that converts an entity to a line
     *
     * @param entity - the entity to convert
     * @return - the line
     */

    @Override
    public String EntitytoLine(Utilizator entity) {
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName() + ";" + entity.getUsername();
    }
}
