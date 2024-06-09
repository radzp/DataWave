package com.amw.datawave.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "data")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "data_value")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataValue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlElement(name = "id")
    private Long id;

    @Column(name = "year")
    @XmlElement(name = "year")
    private int year;

    @Column(name = "value")
    @XmlElement(name = "value")
    private double value;
}