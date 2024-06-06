package com.amw.datawave.data;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@AllArgsConstructor
public class DataService {
    private final DataRepository dataRepository;
    private final RestTemplate restTemplate;

    @Value("${api.ids}")
    private List<String> ids;

    @Value("${api.names}")
    private List<String> names;

    @Value("${api.formats}")
    private List<String> formats;

    // TODO: metoda do fetchowania z api
}