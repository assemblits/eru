package com.eru.persistence;

import com.eru.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {

    List<User> findAllByOrderByUserNameAsc();
}
