package com.amw.datawave.data;

import com.amw.datawave.gusApi.BDLApiResponse;
import com.amw.datawave.gusApi.BDLMeasureUnitResponse;
import com.amw.datawave.measure.MeasureInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
@AllArgsConstructor
public class ExternalDataFetchService implements CommandLineRunner {
    private final DataRepository dataRepository;
    private final FetchFromFileService fetchFromFileService;

    // RestTemplate-klasa z Springa, która pozwala na wykonywanie zapytań HTTP
    private final RestTemplate restTemplate;

    // Wartości pobierane z pliku application.properties
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
        fetchFromFileService.fetchDataFromFileAndSave();

    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void fetchDataAndSave() {
        List<DataModel> dataModels = fetchData();
        List<DataModel> newDataModels = new ArrayList<>();
        for (DataModel dataModel : dataModels) {
            // Check if the data already exists in the database
            Optional<DataModel> existingDataModel = dataRepository.findByNameAndMeasureUnitName(
                    dataModel.getName(), dataModel.getMeasureUnitName()
            );
            if (existingDataModel.isEmpty()) {
                // If the data does not exist, add it to the list to be saved
                newDataModels.add(dataModel);
            }
        }
        dataRepository.saveAll(newDataModels);
    }

    // Metoda pobierająca dane z zewnętrznego api
    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected List<DataModel> fetchData() {
        final int YEARS_TO_FETCH = 10;
        int startYear = Year.now().getValue() - YEARS_TO_FETCH;

        List<DataModel> dataModels = new ArrayList<>();

        // ObjectMapper z Jacksona, który pozwala na mapowanie na obiekty
        ObjectMapper mapper = new ObjectMapper();
        // Ignorowanie nieznanych pól
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // map id z nazwą
        Map<String, String> idNameMap = IntStream.range(0, ids.size())
                .boxed()
                .collect(Collectors.toMap(ids::get, names::get));

        Map<Integer, MeasureInfo> measureMap = fetchMeasureInfo();

        for (String id : ids) {
            String url = String.format("https://bdl.stat.gov.pl/api/v1/data/by-variable/%s?format=%s&lang=pl&unit-level=0", id, formats.get(0));
            String response = restTemplate.getForObject(url, String.class);

            try {
                BDLApiResponse apiResponse = mapper.readValue(response, BDLApiResponse.class);
                int measureUnitId = apiResponse.getMeasureUnitId();

                DataModel dataModel = new DataModel();
                dataModel.setName(idNameMap.get(id));
                dataModel.setMeasureUnitName(measureMap.get(measureUnitId).getName());
                dataModel.setMeasureUnitDescription(measureMap.get(measureUnitId).getDescription());

                List<DataValue> dataValues = new ArrayList<>();
                for (BDLApiResponse.Result result : apiResponse.getResults()) {
                    for (BDLApiResponse.Value value : result.getValues()) {
                        int year = Integer.parseInt(value.getYear());
                        if (year >= startYear) {
                            DataValue dataValue = new DataValue();
                            dataValue.setYear(Integer.parseInt(value.getYear()));
                            dataValue.setValue(value.getValue());
                            dataValues.add(dataValue);
                        }
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

    // Metoda pobierająca informacje o jednostkach miary
    private Map<Integer, MeasureInfo> fetchMeasureInfo() {
        String url = "https://bdl.stat.gov.pl/api/v1/measures?format=json&lang=pl";
        String response = restTemplate.getForObject(url, String.class);
        // ObjectMapper z Jacksona, który pozwala na mapowanie JSONa na obiekty
        ObjectMapper mapper = new ObjectMapper();
        // Ignorowanie nieznanych pól
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