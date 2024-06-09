package com.amw.datawave.user;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.HttpHeaders;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    public List<User> showAllUsers() {
        return userService.showAllUsers();
    }

    @GetMapping("/id/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PutMapping("/update/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }


    @GetMapping("/export/json")
    public ResponseEntity<byte[]> exportUsersToJson() {
        try {
            byte[] jsonData = userService.exportUsersToJson();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.json");
            return new ResponseEntity<>(jsonData, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/import/json")
    public ResponseEntity<Void> importUsersFromJson(@RequestParam("file") MultipartFile file) throws IOException {
        String jsonData = new String(file.getBytes(), StandardCharsets.UTF_8);
        userService.importUsersFromJson(jsonData);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/export/xml")
    public ResponseEntity<byte[]> exportUsersToXml() {
        try {
            byte[] xmlData = userService.exportUsersToXml();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xml");
            return new ResponseEntity<>(xmlData, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/import/xml")
    public ResponseEntity<Void> importUsersFromXml(@RequestParam("file") MultipartFile file) throws Exception {
        String xmlData = new String(file.getBytes(), StandardCharsets.UTF_8);
        userService.importUsersFromXml(xmlData);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
