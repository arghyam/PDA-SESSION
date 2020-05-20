package com.socion.session.service;

import com.socion.session.dto.CleverTapEventData;
import com.socion.session.dto.ResponseDTO;

import java.util.List;

public interface CleverTapService {

    public ResponseDTO uploadSingleEvent(CleverTapEventData request);

    public ResponseDTO uploadMultipleEvent(List<CleverTapEventData> request);
}
