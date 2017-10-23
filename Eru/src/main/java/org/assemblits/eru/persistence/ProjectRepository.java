package org.assemblits.eru.persistence;


import org.assemblits.eru.entities.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Integer> {

    List<Project> findAll();

}
