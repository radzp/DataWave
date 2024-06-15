package com.amw.datawave.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// W DataRepository.java
public interface DataRepository extends JpaRepository<DataModel, Long> {
    List<DataModel> findByName(String name);

    List<DataModel> findByNameIn(List<String> names);

    List<DataModel> findByMeasureUnitName(String measureUnit);
}
