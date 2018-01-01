package com.daisyworks.demo.model;

import org.deeplearning4j.eval.Evaluation;
import org.nd4j.linalg.api.ndarray.INDArray;

import com.daisyworks.demo.Service;

public class Evaluator {
	NeuralNet nn;

	public Evaluator(NeuralNet nn) {
		this.nn = nn;
	}

	public String grade(Service service) {
		INDArray guesses = nn.net.output(service.testColorData.features);
		// INDArray guesses = nn.net.output(service.trainColoData.features);
		// let Evaluation prints stats how often the right output had the highest value
		Evaluation eval = new Evaluation(11);
		eval.eval(service.testColorData.classifications, guesses);
		// eval.eval(service.trainColoData.classifications, guesses);
		String stats = eval.stats();
		return stats;
	}
}
