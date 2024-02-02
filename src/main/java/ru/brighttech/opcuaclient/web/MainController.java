package ru.brighttech.opcuaclient.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.brighttech.opcuaclient.domain.persistence.entity.DeviceOld;
import ru.brighttech.opcuaclient.domain.persistence.repository.DeviceRepo;
import ru.brighttech.opcuaclient.domain.service.OpcUaConnetor;
import ru.brighttech.opcuaclient.web.payload.DeviceRequest;
import ru.brighttech.opcuaclient.web.payload.ListDto;


import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@EnableScheduling
public class MainController {

    private DeviceRepo deviceRepo;
    private OpcUaConnetor opcUaConnetor;

    @Data
    public static class Devices {
        private String ip;
        private Integer namespace;
        private String address;
    }

    @PostMapping("/json")
    public void json(@RequestBody ListDto<DeviceRequest> request) {
        deviceRepo.saveAll(
                request.getData()
                        .stream()
                        .map(
                                i -> new DeviceOld(
                                        null,
                                        "example_name",
                                        i.getIp(),
                                        ""
                                )
                        )
                        .collect(Collectors.toList())
        );
    }

    @PostMapping("multi")
    public void multiThreadRun(@RequestBody List<Devices> devices) {
        ExecutorService service = Executors.newFixedThreadPool(16);

        for (Devices device : devices) {
            service.execute(() -> {
                try {
                    opcUaConnetor.sphinxConnector(device.getIp(), device.getNamespace(), device.getAddress());
                } catch (UaException | ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
