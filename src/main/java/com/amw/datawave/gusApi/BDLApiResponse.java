package com.amw.datawave.gusApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BDLApiResponse {
    @JsonProperty("measureUnitId")
    private int measureUnitId;

    @JsonProperty("results")
    private List<Result> results;


    @Data
    public static class Result {
        @JsonProperty("values")
        private List<Value> values;

    }

    @Data
    public static class Value {
        @JsonProperty("year")
        private String year;

        @JsonProperty("val")
        private Double value;

        @JsonProperty("attrId")
        private int attrId;

    }
}