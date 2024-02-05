package ru.brighttech.opcuaclient.domain.persistence.repository;




import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;
import ru.brighttech.opcuaclient.domain.persistence.entity.Plant;



@Repository
public interface PlantRepo extends JpaRepositoryImplementation<Plant, Long> {
}