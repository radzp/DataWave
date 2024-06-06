package com.amw.datawave.data;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/data")
@AllArgsConstructor
public class DataController {
    private final DataService dataService;

    @GetMapping("/showAllData")
    public ResponseEntity<List<DataModel>> showAllData() {
        List<DataModel> dataModels = dataService.showAllData();
        return ResponseEntity.ok(dataModels);
    }
}
