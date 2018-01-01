package com.daisyworks.demo.model;

import java.util.Arrays;

import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class NeuralNet {
	private final int inputFeatureCnt;
	MultiLayerNetwork net;

	public NeuralNet(int iterations, float learningRate, int inputFeatureCnt, int ouitputClassificationCnt) {
		this.inputFeatureCnt = inputFeatureCnt;

		NeuralNetConfiguration.ListBuilder listBuilder = new NeuralNetConfiguration.Builder() //
				.iterations(iterations) //
				.learningRate(learningRate) //
				.seed(123) //
				.useDropConnect(false) //
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT) //
				.biasInit(0) //
				.miniBatch(false) //
				.list() //

				.pretrain(false) //
				.backprop(true) //

				.layer(0, new DenseLayer.Builder() //
						.nIn(inputFeatureCnt) //
						.nOut(inputFeatureCnt) //
						.name("Input") //
						.build()) //

				.layer(1, new DenseLayer.Builder() //
						.nIn(inputFeatureCnt) //
						.nOut(ouitputClassificationCnt) //
						.name("Hidden") //
						.build()) //

				.layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD) //
						.nIn(ouitputClassificationCnt) //
						.nOut(ouitputClassificationCnt) //
						.name("Output") //
						.activation(Activation.SOFTMAX) //
						.weightInit(WeightInit.DISTRIBUTION) //
						.dist(new UniformDistribution(0, 1)) //
						.build()); //

		MultiLayerNetwork net = new MultiLayerNetwork(listBuilder.build());
		net.init();
		this.net = net;

		// Print the number of parameters in the network (and for each layer)
		Layer[] layers = net.getLayers();
		int totalNumParams = 0;
		for (int i = 0; i < layers.length; i++) {
			int nParams = layers[i].numParams();
			System.out.println("Number of parameters in layer " + i + ": " + nParams);
			totalNumParams += nParams;
		}
		System.out.println("Total number of network parameters: " + totalNumParams);

		net.setListeners(new ScoreIterationListener(100));
	}

	/**
	 * Not static class because the feature size is dependent on the neural net configuration.
	 *
	 */
	public class Observation {
		public final float[] features = new float[inputFeatureCnt];
		public final int classificationIdx;

		/**
		 * Only set inputs that are relevant.
		 * 
		 * @param classificationIdx
		 * @param f
		 */
		public Observation(int classificationIdx, float... f) {
			this.classificationIdx = classificationIdx;
			for (int i = 0; i < f.length; i++) {
				features[i] = f[i];
			}
		}

		public Observation(float... f) {
			this.classificationIdx = -1;
			for (int i = 0; i < f.length; i++) {
				features[i] = f[i];
			}
		}

		public String toString() {
			return String.format("ClassificationIdx: %d\r\nFeatures: \r\n%s", classificationIdx, Arrays.toString(features));
		}
	}
}
