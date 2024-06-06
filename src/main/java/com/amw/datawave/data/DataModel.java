package com.amw.datawave.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "data_model")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "measure_unit_id")
    private int measureUnitId;
    @Column(name = "measure_unit_name")
    private String measureUnitName;
    @Column(name = "measure_unit_description")
    private String measureUnitDescription;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "data_id")
    private List<DataValue> data;
}