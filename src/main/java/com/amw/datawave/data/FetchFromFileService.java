package com.amw.datawave.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

@Service
@AllArgsConstructor
public class FetchFromFileService {
    private final DataRepository dataRepository;

    private static final Map<String, String> MEASURE_UNIT_DESCRIPTIONS = new HashMap<>() {{
        put("zł", "złoty");
        put("tys. zł", "tysiąc złotych");
        put("mln zł", "milion złotych");
    }};

    @Transactional
    public void fetchDataFromFileAndSave() {
        List<DataModel> dataModels = fetchData();
        dataRepository.saveAll(dataModels);
    }

    private List<DataModel> fetchData() {
        try {
            InputStream inputStream = new ClassPathResource("swiadczenia2.json").getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            JsonDataModel[] jsonDataModels = mapper.readValue(inputStream, JsonDataModel[].class);

            Map<String, DataModel> dataModelMap = new HashMap<>();

            for (JsonDataModel jsonDataModel : jsonDataModels) {
                String key = jsonDataModel.getZmienna() + " - " + jsonDataModel.getNazwa_pozycja_2();
                DataModel dataModel = dataModelMap.get(key);

                if (dataModel == null) {
                    dataModel = new DataModel();
                    dataModel.setName(key);
                    String measureUnitName = jsonDataModel.getTyp_informacji().replace("[", "").replace("]", "");
                    dataModel.setMeasureUnitName(measureUnitName);
                    dataModel.setMeasureUnitDescription(MEASURE_UNIT_DESCRIPTIONS.getOrDefault(measureUnitName, measureUnitName));
                    dataModel.setData(new ArrayList<>());
                    dataModelMap.put(key, dataModel);
                }

                DataValue dataValue = new DataValue();
                dataValue.setYear(jsonDataModel.getId_daty());
                String wartosc = jsonDataModel.getWartosc();
                if (wartosc.isEmpty()) {
                    continue;
                } else {
                    dataValue.setValue(Double.parseDouble(wartosc.replace(",", ".")));
                }
                dataModel.getData().add(dataValue);
            }

            return new ArrayList<>(dataModelMap.values());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}