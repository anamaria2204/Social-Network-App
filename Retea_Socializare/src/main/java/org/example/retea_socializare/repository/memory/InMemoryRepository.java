package org.example.retea_socializare.repository.memory;


import org.example.retea_socializare.domeniu.Entity;
import org.example.retea_socializare.domeniu.validators.Validator;
import org.example.retea_socializare.exceptions.ValidationException;
import org.example.retea_socializare.repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/*
 * CRUD operations repository for in-memory storage
 * @param <ID> - type E must have an atribute of type ID
 * @param <E> - type of entities saved in repository
 */

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    private Validator<E> validator;
    protected Map<ID, E> entities;

    /**
     * Constructor
     *
     * @param validator - the validator for the entities
     */

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<>();
    }

    /**
     * Method for finding an entity
     *
     * @param id - the id of the entity to be found
     * @return the entity with the given id
     */

    @Override
    public Optional findOne(ID id) {
        if (id == null) {
            throw new ValidationException("Id-ul nu este valid");
        }
        return Optional.ofNullable(entities.get(id));
    }

    /**
     * Method for finding all entities
     *
     * @return all entities
     */

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional save(E entity) throws ValidationException {

        /**
         *
         * @param entity
         *         entity must be not null
         * @return null- if the given entity is saved
         *         otherwise returns the entity (id already exists)
         * @throws ValidationException
         *            if the entity is not valid
         * @throws IllegalArgumentException
         *             if the given entity is null.     *
         */

        if (entity == null)
            throw new IllegalArgumentException("ENTITY CANNOT BE NULL");
        validator.validate(entity);
        return Optional.ofNullable(entities.put(entity.getId(), entity));

    }

    /**
     * Method for deleting an entity with a given id
     *
     * @param id - the id of the entity to be deleted
     * @return the removed entity
     */

    @Override
    public Optional delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID-ul nu este valid");
        }
        return Optional.ofNullable(entities.remove(id));
    }

    /**
     * Method for updating an entity
     *
     * @param entity - the entity to be deleted
     * @return the entity or null if the entity is updated
     */

    @Override
    public Optional update(E entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entitatea nu este valida");
        if (entities.get(entity.getId()) != null) {
            return Optional.ofNullable(entities.put(entity.getId(), entity));

        }
        return Optional.ofNullable(entities.get(entity.getId()));
    }
}
