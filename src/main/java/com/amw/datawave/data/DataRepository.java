package com.amw.datawave.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataRepository extends JpaRepository<DataModel, Long>{

    List<DataModel> findByName(String name);
}
