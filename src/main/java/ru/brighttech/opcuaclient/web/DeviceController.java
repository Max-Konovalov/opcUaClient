package ru.brighttech.opcuaclient.web;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.brighttech.opcuaclient.domain.persistence.entity.DeviceOld;
import ru.brighttech.opcuaclient.domain.persistence.repository.DeviceRepo;

import java.util.List;

@Controller
@RequestMapping(value = "/device")
@AllArgsConstructor
public class DeviceController {

    @Autowired
    private DeviceRepo deviceRepo;
    @GetMapping
    public ResponseEntity<List<DeviceOld>> getDeviceList() {
        return ResponseEntity.ok().body(deviceRepo.findAll());
    }

}
