package com.amw.datawave.data;

import com.amw.datawave.gusApi.BDLApiResponse;
import com.amw.datawave.gusApi.BDLMeasureUnitResponse;
import com.amw.datawave.measure.MeasureInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
@AllArgsConstructor
public class ExternalDataFetchService implements CommandLineRunner {
    private final DataRepository dataRepository;
    private final RestTemplate restTemplate;

    @Value("#{'${api.ids}'.split(',')}")
    private List<String> ids;

    @Value("#{'${api.names}'.split(',')}")
    private List<String> names;

    @Value("#{'${api.formats}'.split(',')}")
    private List<String> formats;


    // Metoda run, która jest wywoływana przy starcie aplikacji
    @Override
    public void run(String... args) throws Exception {
        fetchDataAndSave();
    }

    // Metody zapisywania danych z zewnetrznych api do bazy danych
    public void fetchDataAndSave() {
        List<DataModel> dataModels = fetchData();
        dataRepository.saveAll(dataModels);
    }

    private List<DataModel> fetchData() {
        String jsonFormat = formats.get(0);
        List<DataModel> dataModels = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // map id z nazwą
        Map<String, String> idNameMap = IntStream.range(0, ids.size())
                .boxed()
                .collect(Collectors.toMap(ids::get, names::get));

        // fetch measure info
        Map<Integer, MeasureInfo> measureMap = fetchMeasureInfo();
        for (String id : ids) {
            String url = String.format("https://bdl.stat.gov.pl/api/v1/data/by-variable/%s?format=%s&lang=pl&unit-level=0", id, jsonFormat);
            String response = restTemplate.getForObject(url, String.class);

            try {
                BDLApiResponse apiResponse = mapper.readValue(response, BDLApiResponse.class);
                int measureUnitId = apiResponse.getMeasureUnitId();

                DataModel dataModel = new DataModel();
                dataModel.setMeasureUnitId(measureUnitId);
                dataModel.setName(idNameMap.get(id));
                dataModel.setMeasureUnitName(measureMap.get(measureUnitId).getName());
                dataModel.setMeasureUnitDescription(measureMap.get(measureUnitId).getDescription());

                List<DataValue> dataValues = new ArrayList<>();
                for (BDLApiResponse.Result result : apiResponse.getResults()) {
                    for (BDLApiResponse.Value value : result.getValues()) {
                        DataValue dataValue = new DataValue();
                        dataValue.setYear(Integer.parseInt(value.getYear()));
                        dataValue.setValue(value.getValue());
                        dataValues.add(dataValue);
                    }
                }
                dataModel.setData(dataValues);
                dataModels.add(dataModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return dataModels;
    }

    private Map<Integer, MeasureInfo> fetchMeasureInfo() {
        String url = "https://bdl.stat.gov.pl/api/v1/measures?format=json&lang=pl";
        String response = restTemplate.getForObject(url, String.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Map<Integer, MeasureInfo> measureInfoMap = new HashMap<>();

        try {
            BDLMeasureUnitResponse measureUnitResponse = mapper.readValue(response, BDLMeasureUnitResponse.class);
            for (BDLMeasureUnitResponse.MeasureUnit measureUnit : measureUnitResponse.getResults()) {
                MeasureInfo measureInfo = new MeasureInfo(measureUnit.getName(), measureUnit.getDescription());
                measureInfoMap.put(measureUnit.getId(), measureInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return measureInfoMap;
    }


}