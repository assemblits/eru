package com.eru.persistence;

import com.eru.entities.Project;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class ProjectDao extends Dao<Project> {

    public ProjectDao(EntityManager em) {
        super(em, Project.class);
    }
}
