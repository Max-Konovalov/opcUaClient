package ru.brighttech.opcuaclient.domain.persistence.entity;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pattern_id")
    private DevicePattern pattern;

    @Column(name = "serial_num")
    private String serialNumber;

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "territory_id")
    private Territory territory;

    private boolean isDeactivated;

    @Column(name = "is_deactivated", nullable = false)
    private Boolean isDeactivated1 = false;

    @Column(name = "data")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> data;

}