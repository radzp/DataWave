package com.amw.datawave.dbUtils;

import com.amw.datawave.data.DataModel;
import com.amw.datawave.data.DataRepository;
import com.amw.datawave.user.User;
import com.amw.datawave.user.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class DBImportExportService {

    private final DataRepository dataRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    public byte[] exportDatabase() throws IOException {
        List<DataModel> dataModels = dataRepository.findAll();
        List<User> users = userRepository.findAll();

        Map<String, Object> databaseMap = new HashMap<>();
        databaseMap.put("dataModels", dataModels);
        databaseMap.put("users", users);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(databaseMap);

        return jsonData.getBytes();
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void importDatabase(MultipartFile file) throws IOException {
        String jsonData = new String(file.getBytes(), StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        JavaType type = objectMapper.getTypeFactory().constructParametricType(Map.class, String.class, Object.class);
        Map<String, Object> databaseMap = objectMapper.readValue(jsonData, type);

        JavaType dataModelType = objectMapper.getTypeFactory().constructCollectionType(List.class, DataModel.class);
        List<DataModel> dataModels = objectMapper.convertValue(databaseMap.get("dataModels"), dataModelType);

        JavaType userType = objectMapper.getTypeFactory().constructCollectionType(List.class, User.class);
        List<User> users = objectMapper.convertValue(databaseMap.get("users"), userType);

        dataRepository.saveAll(dataModels);
        userRepository.saveAll(users);
    }
}