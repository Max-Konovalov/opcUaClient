package ru.brighttech.opcuaclient.web;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.brighttech.opcuaclient.domain.persistence.entity.Device;
import ru.brighttech.opcuaclient.domain.persistence.repository.DeviceRepo;
import ru.brighttech.opcuaclient.domain.service.OpcUaConnetor;
import ru.brighttech.opcuaclient.web.payload.DataRequest;

import java.util.List;


@Controller
@RequestMapping(value = "/device")
@AllArgsConstructor
public class DeviceController {
    private OpcUaConnetor opcUaConnetor;

    @Autowired
    private DeviceRepo deviceRepo;
    @GetMapping
    public ResponseEntity<List<Device>> getDeviceList() {
        return ResponseEntity.ok().body(deviceRepo.findAll());
    }

    @PostMapping(value="/getData")
    public void getDeviceData(@RequestBody DataRequest data) throws Exception {
        opcUaConnetor.myConnector();
    }

}
