package ru.brighttech.opcuaclient.domain.persistence.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "devices_data")
public class DeviceData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "received_time", nullable = false)
    private LocalDateTime receivedTime;

    @ManyToOne
    @JoinColumn(name = "device_id", referencedColumnName = "id", nullable = false)
    private Device device;


    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private String receivedData;

}
