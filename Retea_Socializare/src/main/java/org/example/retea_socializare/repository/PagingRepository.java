package org.example.retea_socializare.repository;

import org.example.retea_socializare.domeniu.Entity;
import org.example.retea_socializare.domeniu.Friendship;
import org.example.retea_socializare.domeniu.Utilizator;
import org.example.retea_socializare.utils.paging.Page;
import org.example.retea_socializare.utils.paging.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
        public Page<Friendship> findAllOnPage(Pageable pageable, Integer size, Iterable<Friendship> list);
}
