package com.daisyworks.demo;

import io.vertx.ext.web.RoutingContext;

import java.io.IOException;

public class ModelAdminRequestHandler extends RequestHandler {

	public ModelAdminRequestHandler(RoutingContext rc, Service service) {
		super(rc, service);
	}

	@Override
	public void handle() {
		boolean saveModel = bodyJson.getBoolean("saveModel", false);
		boolean resetModel = bodyJson.getBoolean("resetModel", false);
		boolean loadModel = bodyJson.getBoolean("loadModel", false);

		String modelFilename = bodyJson.getString("modelFilename", null);

		try {
			if (saveModel && modelFilename != null && !modelFilename.isEmpty()) {
				service.nn.saveModel(modelFilename, true);
				System.out.println("Saved model: " + modelFilename);
			}

			if (resetModel) {
				service.nn.initializeNewModel();
				System.out.println("Reset model");
			}

			if (loadModel && modelFilename != null && !modelFilename.isEmpty()) {
				service.nn.restoreModel(modelFilename, true);
				System.out.println("Loaded model: " + modelFilename);
			}

			rc.response().end();
		} catch (IOException e) {
			e.printStackTrace();
			rc.response().setStatusCode(500).end(e.getMessage());
		}
	}
}
