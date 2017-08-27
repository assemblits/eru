package com.eru.persistence;

import com.eru.entities.Alarm;
import com.eru.entities.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {

    List<Tag> findAllByOrderByNameAsc();
}
