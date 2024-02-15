package ru.brighttech.opcuaclient.web;


import lombok.AllArgsConstructor;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.brighttech.opcuaclient.domain.persistence.entity.DeviceOld;
import ru.brighttech.opcuaclient.domain.persistence.repository.DeviceRepo;
import ru.brighttech.opcuaclient.domain.service.OpcUaConnetor;
import ru.brighttech.opcuaclient.web.payload.DataRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


@Controller
@RequestMapping(value = "/device")
@AllArgsConstructor
public class DeviceController {
    private OpcUaConnetor opcUaConnetor;

    @Autowired
    private DeviceRepo deviceRepo;
    @GetMapping
    public ResponseEntity<List<DeviceOld>> getDeviceList() {
        return ResponseEntity.ok().body(deviceRepo.findAll());
    }

    @PostMapping(value="/getData")
    public void getDeviceData(@RequestBody DataRequest data) throws Exception {
        opcUaConnetor.myConnector(data.getNamespace(), data.getAddress());

        System.out.println("\"PLANT CH STATUS\".\"CH02\".\"ACT_CH_ENG\"");
    }

}
