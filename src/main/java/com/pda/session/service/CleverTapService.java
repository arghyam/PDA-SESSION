package com.pda.session.service;

import com.pda.session.dto.CleverTapEventData;
import com.pda.session.dto.ResponseDTO;

import java.util.List;

public interface CleverTapService {

    public ResponseDTO uploadSingleEvent(CleverTapEventData request);

    public ResponseDTO uploadMultipleEvent(List<CleverTapEventData> request);
}
