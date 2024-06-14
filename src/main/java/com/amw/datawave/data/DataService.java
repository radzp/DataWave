package com.amw.datawave.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DataService {
    private final DataRepository dataRepository;

    public List<DataModel> showAllData() {
        return dataRepository.findAll();
    }


public List<DataModel> showByName(List<String> names, List<Integer> years) {
    List<DataModel> dataModels = dataRepository.findByNameIn(names);

    if (years != null && !years.isEmpty()) {
        for (DataModel dataModel : dataModels) {
            List<DataValue> filteredData = dataModel.getData().stream()
                    .filter(dataValue -> years.contains(dataValue.getYear()))
                    .collect(Collectors.toList());
            dataModel.setData(filteredData);
        }
    }

    return dataModels;
}


public List<DataModel> showById(List<Long> ids, List<Integer> years) {
    List<DataModel> dataModels = dataRepository.findAllById(ids);

    if (years != null && !years.isEmpty()) {
        for (DataModel dataModel : dataModels) {
            List<DataValue> filteredData = dataModel.getData().stream()
                    .filter(dataValue -> years.contains(dataValue.getYear()))
                    .collect(Collectors.toList());
            dataModel.setData(filteredData);
        }
    }

    return dataModels;
}

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void saveData(DataModel dataModel) {
        dataRepository.save(dataModel);
    }

    public ResponseEntity<byte[]> exportDataToJson() throws Exception {
        List<DataModel> dataModels = showAllData();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(dataModels);

        byte[] isr = jsonData.getBytes();
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentLength(isr.length);
        respHeaders.setContentType(new MediaType("text", "json"));
        respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.json");

        return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
    }

    public ResponseEntity<byte[]> exportDataToXml() throws Exception {
        List<DataModel> dataModels = showAllData();

        DataModelList dataModelList = new DataModelList();
        dataModelList.setItems(dataModels);

        JAXBContext jaxbContext = JAXBContext.newInstance(DataModelList.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(dataModelList, sw);
        String xmlContent = sw.toString();

        byte[] xmlBytes = xmlContent.getBytes(StandardCharsets.UTF_8);

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(new MediaType("application", "xml"));
        respHeaders.setContentLength(xmlBytes.length);
        respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xml");

        return new ResponseEntity<>(xmlBytes, respHeaders, HttpStatus.OK);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void importDataFromJson(String jsonData) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        List<DataModel> dataModels = objectMapper.readValue(jsonData, new TypeReference<>() {
        });

        for (DataModel dataModel : dataModels) {
            saveData(dataModel);
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void importDataFromXml(String xmlData) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(DataModelList.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        StringReader reader = new StringReader(xmlData);
        DataModelList dataModelList = (DataModelList) jaxbUnmarshaller.unmarshal(reader);

        for (DataModel dataModel : dataModelList.getItems()) {
            saveData(dataModel);
        }
    }


    public List<BenefitName> getBenefitNames() {
        return dataRepository.findAll().stream()
                .map(dataModel -> new BenefitName(dataModel.getId(), dataModel.getName()))
                .collect(Collectors.toList());
    }
}