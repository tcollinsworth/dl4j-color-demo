package com.daisyworks.demo.model;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class Inferrer {
	NeuralNet nn;

	public Inferrer(NeuralNet nn) {
		this.nn = nn;
	}

	public Output infer(float i1, float i2, float i3, float i4, float i5) {
		INDArray inputs = Nd4j.zeros(1, 5);
		inputs.putScalar(new int[] { 0, 0 }, i1);
		inputs.putScalar(new int[] { 0, 1 }, i2);
		inputs.putScalar(new int[] { 0, 2 }, i3);
		inputs.putScalar(new int[] { 0, 3 }, i4);
		inputs.putScalar(new int[] { 0, 4 }, i5);

		System.out.println(inputs.toString());

		long start = System.nanoTime();
		int[] outputs = nn.net.predict(inputs); // 512us for 1 row
		// System.out.println(outputs.length);
		// System.out.println(Arrays.toString(outputs));
		// System.out.println(nn.net.summary());
		long timeUs = System.nanoTime() - start;
		float timeMs = ((float) timeUs) / 1000000;

		return new Output(outputs, timeMs);
	}

	public static class Output {
		public final int[] outputs;
		public final float timeMs;

		public Output(int[] outputs, float timeMs) {
			this.outputs = outputs;
			this.timeMs = timeMs;
		}
	}
}
