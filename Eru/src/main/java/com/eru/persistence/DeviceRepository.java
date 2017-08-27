package com.eru.persistence;

import com.eru.entities.Device;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DeviceRepository extends CrudRepository<Device, Integer> {

    List<Device> findAllByOrderByNameAsc();
}
