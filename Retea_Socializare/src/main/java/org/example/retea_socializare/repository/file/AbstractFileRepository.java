package org.example.retea_socializare.repository.file;


import org.example.retea_socializare.domeniu.Entity;
import org.example.retea_socializare.domeniu.validators.Validator;
import org.example.retea_socializare.repository.memory.InMemoryRepository;

import java.io.*;
import java.util.Optional;

/**
 * CRUD operations for file storage
 *
 * @param <ID> - the E must have an id
 * @param <E>  - type of entity saved in the repository
 */

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private String filename;

    /**
     * Constructor
     *
     * @param validator - the validator for entity
     * @param fileName  - the filename where the entities will be saved
     */

    public AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        filename = fileName;
        loadData();
    }

    /**
     * Abstract method for readind a line fron the file and convertind it to an entity
     *
     * @param line - the line to be converted
     * @return the entity
     */

    public abstract E lineToEntity(String line);

    /**
     * Abstract method for converting an entity to line
     *
     * @param entity - the entity to convert
     * @return the line
     */

    public abstract String EntitytoLine(E entity);

    /**
     * Method that saves an entity
     *
     * @param entity - the entity to be saved
     *               entity must be not null
     * @return the entity
     */

    @Override
    public Optional save(E entity) {
        Optional optional = super.save(entity);
        if (optional.isEmpty()) {
            appendToFile(entity);
        }
        return optional;
    }

    /**
     * Method to write an entity in a file
     */

    private void writeToFile() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (E entity : entities.values()) {
                String ent = EntitytoLine(entity);
                writer.write(ent);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Method that delets an entity
     *
     * @param id - the id of the entity to be deleted
     *           id must be not null
     * @return the entity
     */

    @Override
    public Optional delete(ID id) {
        Optional entity = super.delete(id);
        if (entity.isPresent())
            writeToFile();
        return entity;
    }

    /**
     * Method that updates an entity
     *
     * @param entity - the entity to be updated
     *               entity must not be null
     * @return - the entity
     */

    @Override
    public Optional update(E entity) {
        Optional e = super.update(entity);
        if (e.isEmpty())
            writeToFile();
        return e;
    }

    /**
     * Method that loads the data from the file
     */

    private void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                E entity = lineToEntity(line);
                entities.put(entity.getId(), entity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that appends an entity to a file
     *
     * @param entity - entity to be appended
     */

    private void appendToFile(E entity) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            String ent = EntitytoLine(entity);
            writer.write(ent);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
