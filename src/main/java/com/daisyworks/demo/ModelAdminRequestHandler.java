package com.daisyworks.demo;

import java.io.IOException;

import io.vertx.ext.web.RoutingContext;

public class ModelAdminRequestHandler extends RequestHandler {

	public ModelAdminRequestHandler(RoutingContext rc, Service service) {
		super(rc, service);
	}

	@Override
	public void handle() {
		boolean saveModel = bodyJson.getBoolean("saveModel");
		boolean resetModel = bodyJson.getBoolean("resetModel");
		boolean loadModel = bodyJson.getBoolean("loadModel");
		String modelFilename = bodyJson.getString("modelFilename");

		try {
			if (saveModel && modelFilename != null && !modelFilename.isEmpty()) {
				service.nn.saveModel(modelFilename, true);

			}

			if (resetModel) {
				service.nn.initializeNewModel();
			}

			if (loadModel && modelFilename != null && !modelFilename.isEmpty()) {
				service.nn.restoreModel(modelFilename, true);
			}
			
			rc.response().end();
		} catch (IOException e) {
			e.printStackTrace();
			rc.response().setStatusCode(500).end(e.getMessage());
		}
	}
}
