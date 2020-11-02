package com.pda.session.dto.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Request {
    private Attestations attestations;

    public Attestations getAttestations() {
        return attestations;
    }

    public void setAttestations(Attestations attestations) {
        this.attestations = attestations;
    }
}
