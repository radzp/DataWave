package com.amw.datawave.gusApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BDLMeasureUnitResponse {
    @JsonProperty("results")
    private List<MeasureUnit> results;

    @Data
    public static class MeasureUnit {
        @JsonProperty("id")
        private int id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("description")
        private String description;
    }
}