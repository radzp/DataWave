package com.amw.datawave.dbUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/db")
@RequiredArgsConstructor
public class DBController {
    private final DBImportExportService dbImportExportService;

    @GetMapping("/export")
    public ResponseEntity<Resource> exportDatabase() {
        try {
            byte[] data = dbImportExportService.exportDatabase();
            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=database.json")
                    .body(resource);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while exporting database", e);
        }
    }

    @PostMapping("/import")
    public ResponseEntity<Void> importDatabase(@RequestParam("file") MultipartFile file) {
        try {
            dbImportExportService.importDatabase(file);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while importing database", e);
        }
    }
}