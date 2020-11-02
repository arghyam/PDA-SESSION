package com.pda.session.service.impl;

import java.io.IOException;
import java.util.List;

import com.pda.session.dto.CleverTapEventData;
import com.pda.session.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pda.session.service.CleverTapDao;
import com.pda.session.service.CleverTapService;
import com.pda.session.dto.ResponseDTO;
import com.pda.session.utils.HttpUtils;

import retrofit2.Call;
import retrofit2.Response;

@Service
public class CleverTapServiceImpl implements CleverTapService{

	@Autowired
	CleverTapDao cleverTapDao;

	private static final Logger LOGGER = LoggerFactory.getLogger(CleverTapServiceImpl.class);


	@Override
	public ResponseDTO uploadSingleEvent(CleverTapEventData request) {

		try {
			Call<CleverTapEventData> uploadEvent = cleverTapDao.uploadSingleEvent(request);
			Response<CleverTapEventData> response = uploadEvent.execute();
			if (response.isSuccessful() && null != response.body()) {
				return HttpUtils.success(response.body(), "Successfully pushed the events");
			}
			return HttpUtils.onFailure(404, Constants.ERROR_UPLOADING_IN_CLEVERTAP);
		} catch (IOException e) {
			LOGGER.error(Constants.ERROR_UPLOADING_IN_CLEVERTAP);
			return HttpUtils.onFailure(404, Constants.ERROR_UPLOADING_IN_CLEVERTAP);
		}
	}
	
	@Override
	public ResponseDTO uploadMultipleEvent(List<CleverTapEventData> request) {
		
		try {
			Call<List<CleverTapEventData>> uploadEvent = cleverTapDao.uploadMultipleEvent(request);
			Response<List<CleverTapEventData>> response = uploadEvent.execute();

			if (response.isSuccessful() && null != response.body()) {
				return HttpUtils.success(response.body(), "Successfully pushed the events");
			}
			return HttpUtils.onFailure(404, Constants.ERROR_UPLOADING_IN_CLEVERTAP);
		} catch (IOException e) {
			LOGGER.error(Constants.ERROR_UPLOADING_IN_CLEVERTAP);
			return HttpUtils.onFailure(404, Constants.ERROR_UPLOADING_IN_CLEVERTAP);
		}
	}
}
