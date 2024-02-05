package ru.brighttech.opcuaclient.web;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.brighttech.opcuaclient.domain.persistence.entity.Plant;
import ru.brighttech.opcuaclient.domain.persistence.repository.PlantRepo;


import java.util.List;

@Controller
@RequestMapping(value = "/plant")
@AllArgsConstructor
public class PlantController {

    @Autowired
    private PlantRepo plantRepo;
    @GetMapping
    public ResponseEntity<List<Plant>> getPlantList() {
        return ResponseEntity.ok().body(plantRepo.findAll());
    }

}