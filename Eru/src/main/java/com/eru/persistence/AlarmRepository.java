package com.eru.persistence;

import com.eru.entities.Alarm;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends CrudRepository<Alarm, Long> {

    Alarm findFirstByOrderByTimeStampAsc();

    List<Alarm> findAllByOrderByTimeStampAsc();

    List<Alarm> findAllByOrderByTimeStampAsc(Pageable pageable);
}