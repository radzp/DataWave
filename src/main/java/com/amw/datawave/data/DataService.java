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

}
