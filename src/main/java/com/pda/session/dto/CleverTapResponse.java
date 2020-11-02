package com.pda.session.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CleverTapResponse {

    @JsonProperty("status")
    String status;

    @JsonProperty("processed")
    Integer processed;

    @JsonProperty("unprocessed")
    List<Object> unprocessed;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getProcessed() {
        return processed;
    }

    public void setProcessed(Integer processed) {
        this.processed = processed;
    }

    public List<Object> getUnprocessed() {
        return unprocessed;
    }

    public void setUnprocessed(List<Object> unprocessed) {
        this.unprocessed = unprocessed;
    }


}
