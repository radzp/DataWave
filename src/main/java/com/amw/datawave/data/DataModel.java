package com.amw.datawave.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity(name = "data_model")
@Table(name = "data_model")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlElement(name = "id")
    private Long id;

    @Column(name = "name")
    @XmlElement(name = "name")
    private String name;

//    @Column(name = "measure_unit_id")
//    @XmlElement(name = "measureUnitId")
//    private int measureUnitId;

    @Column(name = "measure_unit_name")
    @XmlElement(name = "measureUnitName")
    private String measureUnitName;

    @Column(name = "measure_unit_description")
    @XmlElement(name = "measureUnitDescription")
    private String measureUnitDescription;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "data_id")
    @XmlElement(name = "data")
    private List<DataValue> data;
}