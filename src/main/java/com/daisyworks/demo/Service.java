package com.daisyworks.demo;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.io.IOException;

import com.daisyworks.demo.color.ColorInferenceRequestHandler;
import com.daisyworks.demo.color.ColorTrainValidateRequestHandler;
import com.daisyworks.demo.model.Inferrer;
import com.daisyworks.demo.model.NeuralNet;
import com.daisyworks.demo.model.Trainer;
import com.daisyworks.demo.model.Validator;

public class Service {
	static final int PORT = 8080;

	public NeuralNet nn = new NeuralNet();
	public Inferrer inferrer = new Inferrer(nn);
	public Trainer trainer = new Trainer(nn);
	public Validator validator = new Validator(nn);

	public static void main(String[] args) throws IOException, InterruptedException {
		// for dev, also requires staticHandler.setCacheEntryTimeout(1) and browser cache disable
		System.setProperty("vertx.disableFileCaching", "true");

		Service service = new Service();
		service.trainer.fit();

		Vertx vertx = Vertx.vertx();
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.route(HttpMethod.POST, "/color-inference").handler(routingContext -> new ColorInferenceRequestHandler(routingContext, service));
		router.route(HttpMethod.POST, "/color-train-validate").handler(routingContext -> new ColorTrainValidateRequestHandler(routingContext, service));
		router.route("/*").handler(StaticHandler.create().setCacheEntryTimeout(1));

		vertx.createHttpServer().requestHandler(router::accept).listen(PORT, res -> {
			if (res.succeeded()) {
				System.out.println("Listening: " + PORT);
			} else {
				System.out.println("Failed to launch server: " + res.cause());
				System.exit(-1);
			}
		});
	}
}
