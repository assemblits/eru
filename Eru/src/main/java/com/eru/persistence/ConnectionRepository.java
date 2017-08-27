package com.eru.persistence;

import com.eru.entities.Connection;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConnectionRepository extends CrudRepository<Connection, Integer> {

    List<Connection> findAllByOrderByNameAsc();
}
