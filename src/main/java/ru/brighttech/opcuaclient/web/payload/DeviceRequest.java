package ru.brighttech.opcuaclient.web.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRequest {
    private String ip;
    private Integer namespace;
    private String address;
}