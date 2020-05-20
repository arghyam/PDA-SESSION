package com.socion.session.service.impl;

import java.io.IOException;
import java.util.List;

import com.socion.session.dto.CleverTapEventData;
import com.socion.session.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.socion.session.service.CleverTapDao;
import com.socion.session.service.CleverTapService;
import com.socion.session.dto.ResponseDTO;
import com.socion.session.utils.HttpUtils;

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
