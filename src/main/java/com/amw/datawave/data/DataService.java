package com.amw.datawave.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DataService {
    private final DataRepository dataRepository;

    public List<DataModel> showAllData() {
        return dataRepository.findAll();
    }

    public List<DataModel> showByName(String name) {
        return dataRepository.findByName(name);
    }

    public DataModel showById(Long id) {
        return dataRepository.findById(id).orElseThrow(() -> new RuntimeException("Data not found"));
    }
}
