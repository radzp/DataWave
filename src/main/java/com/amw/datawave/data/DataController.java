package com.amw.datawave.data;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
public class DataController {
    private final DataService dataService;

    @GetMapping("/all")
    public ResponseEntity<List<DataModel>> showAllData() {
        List<DataModel> dataModels = dataService.showAllData();
        return ResponseEntity.ok(dataModels);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<DataModel>> getByName(@PathVariable String name) {
        List<DataModel> dataModels = dataService.showByName(name);
        return ResponseEntity.ok(dataModels);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<DataModel> getById(@PathVariable Long id) {
        DataModel dataModel = dataService.showById(id);
        return ResponseEntity.ok(dataModel);
    }
}
