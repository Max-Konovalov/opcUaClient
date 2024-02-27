package ru.brighttech.opcuaclient.web.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataRequest {
    private int namespace;
    private List<String> address;
}
