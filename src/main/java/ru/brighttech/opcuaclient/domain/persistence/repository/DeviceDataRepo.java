package ru.brighttech.opcuaclient.domain.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.brighttech.opcuaclient.domain.persistence.entity.DeviceData;

public interface DeviceDataRepo extends JpaRepository<DeviceData, Integer> {
}
