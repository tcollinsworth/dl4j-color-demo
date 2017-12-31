package com.daisyworks.demo.color;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import com.daisyworks.demo.RequestHandler;
import com.daisyworks.demo.Service;
import com.daisyworks.demo.model.Inferrer.Output;

public class ColorInferenceRequestHandler extends RequestHandler {
	public static final float ColorDiscriminator = 0.1f;
	public static final float RgbScale = 1f / 255f;

	public static enum Colors {
		white, grey, black, brown, red, orange, yellow, green, blue, violet, pink
	};

	public ColorInferenceRequestHandler(RoutingContext rc, Service service) {
		super(rc, service);
	}

	@Override
	public void handle() {
		JsonObject respObj = getColorInference(service, bodyJson);
		rc.response().end(respObj.encode());
	}

	public static JsonObject getColorInference(Service service, JsonObject bodyJson) {
		JsonArray rgbArray = bodyJson.getJsonArray("rgb");
		System.out.println("bodyJson: " + rgbArray.toString());
		int red = rgbArray.getInteger(0);
		int green = rgbArray.getInteger(1);
		int blue = rgbArray.getInteger(2);

		System.out.println("rgb: " + red + "," + green + "," + blue);

		float r = red * RgbScale;
		float g = green * RgbScale;
		float b = blue * RgbScale;

		System.out.println("scaled rgb: " + r + "," + g + "," + b);

		Output output = service.inferrer.infer(ColorDiscriminator, r, g, b, 0);

		int classificationIdx = output.outputs[0];
		String color = Colors.values()[classificationIdx].name();

		JsonObject respObj = new JsonObject();
		respObj.put("color", color);
		respObj.put("timeMs", output.timeMs);

		return respObj;
	}
}