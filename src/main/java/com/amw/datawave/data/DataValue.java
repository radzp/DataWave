package com.amw.datawave.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "data_value")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataValue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "year")
    private Integer year;
    @Column(name = "value")
    private Double value;
}