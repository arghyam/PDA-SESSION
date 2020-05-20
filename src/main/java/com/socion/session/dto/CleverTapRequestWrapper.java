package com.socion.session.dto;

import java.util.List;

public class CleverTapRequestWrapper {

    private List<CleverTapEventData> data;

    public List<CleverTapEventData> getData() {
        return data;
    }

    public void setData(List<CleverTapEventData> data) {
        this.data = data;
    }

    public CleverTapRequestWrapper(List<CleverTapEventData> data) {
        this.data = data;
    }

    public CleverTapRequestWrapper() {
    }
}
