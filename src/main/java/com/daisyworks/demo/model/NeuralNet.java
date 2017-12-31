package com.daisyworks.demo.model;

import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class NeuralNet {
	MultiLayerNetwork net;

	public NeuralNet() {
		int interations = 10; // 4000
		float learningRate = 0.1f;
		int inputCnt = 5;
		int outputCnt = 11;

		NeuralNetConfiguration.ListBuilder listBuilder = new NeuralNetConfiguration.Builder() //
				.iterations(interations) //
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
						.nIn(inputCnt) //
						.nOut(inputCnt) //
						.name("Input") //
						.build()) //

				.layer(1, new DenseLayer.Builder() //
						.nIn(inputCnt) //
						.nOut(outputCnt) //
						.name("Hidden") //
						.build()) //

				.layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD) //
						.nIn(outputCnt) //
						.nOut(outputCnt) //
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
	}
}
