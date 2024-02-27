package ru.brighttech.opcuaclient.domain.persistence.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;
import ru.brighttech.opcuaclient.domain.persistence.entity.Device;
import ru.brighttech.opcuaclient.domain.persistence.entity.DeviceData;
import ru.brighttech.opcuaclient.domain.persistence.entity.DeviceOld;


@Repository
public interface DeviceRepo extends JpaRepository<Device, Integer> {

}