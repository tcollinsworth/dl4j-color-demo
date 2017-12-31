package com.daisyworks.demo.color;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import com.daisyworks.demo.RequestHandler;
import com.daisyworks.demo.Service;

public class ColorTrainValidateRequestHandler extends RequestHandler {
	public ColorTrainValidateRequestHandler(RoutingContext rc, Service service) {
		super(rc, service);
	}

	@Override
	public void handle() {
		JsonObject respObj = ColorInferenceRequestHandler.getColorInference(service, bodyJson);

		// TODO add time, model metrics

		rc.response().end(respObj.encode());
	}
}