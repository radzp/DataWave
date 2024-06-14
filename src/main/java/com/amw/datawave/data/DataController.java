package com.amw.datawave.data;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
public class DataController {
    private final DataService dataService;
    private final Environment environment;

    @GetMapping(value = "/all", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<DataModel>> showAllData() {
        List<DataModel> dataModels = dataService.showAllData();
        return ResponseEntity.ok(dataModels);
    }

    @GetMapping("/benefitNames")
    public ResponseEntity<List<BenefitName>> getBenefitNames() {
        try {
            List<BenefitName> benefits = dataService.getBenefitNames();
            return ResponseEntity.ok(benefits);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping(value = "/name", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<DataModel>> getByName(@RequestParam List<String> name, @RequestParam(required = false) List<Integer> year) {
        List<DataModel> dataModels = dataService.showByName(name, year);
        return ResponseEntity.ok(dataModels);
    }

    @GetMapping(value = "/id", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<DataModel>> getById(@RequestParam List<Long> id, @RequestParam(required = false) List<Integer> year) {
        List<DataModel> dataModels = dataService.showById(id, year);
        return ResponseEntity.ok(dataModels);
    }

    @GetMapping(value = "/export/json", produces = "application/json")
    public ResponseEntity<byte[]> exportDataToJson() throws Exception {
        return dataService.exportDataToJson();
    }

    @GetMapping(value = "/export/xml", produces = "application/xml")
    public ResponseEntity<byte[]> exportDataToXml() throws Exception {
        return dataService.exportDataToXml();
    }

    @PostMapping(value = "/import/json", consumes = "multipart/form-data")
    public ResponseEntity<Void> importDataFromJson(@RequestParam("file") MultipartFile file) throws Exception {
        String jsonData = new String(file.getBytes(), StandardCharsets.UTF_8);
        dataService.importDataFromJson(jsonData);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/import/xml", consumes = "multipart/form-data")
    public ResponseEntity<Void> importDataFromXml(@RequestParam("file") MultipartFile file) throws Exception {
        String xmlData = new String(file.getBytes(), StandardCharsets.UTF_8);
        dataService.importDataFromXml(xmlData);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}