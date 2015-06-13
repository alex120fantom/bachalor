package ua.kh.khpi.alex_babenko.art;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import ua.kh.khpi.alex_babenko.utils.ArrayHelper;

public class Network {
	
	private static final Logger LOG = Logger.getLogger(Network.class);

	private double[][] knowledges;
	private double[][] potentialViruses;

	private double[][] b; // коефициенты весов
	private double[][] t; // значения
	private double[][] bCopy; // коефициенты весов;
	private double[][] tCopy; // значения

	private double L;
	private double p;

	private double w1; // изначальные веса
	private double w2;
	
	public Network(int lines, Integer lineSize, double[][] knowleges, double p, double L) {
		LOG.debug("System initialization was started");
		this.knowledges = knowleges;
		this.p = p;
		this.L = L;
		this.w1 = 1 / (1 + lineSize.doubleValue()); // изначальные веса
		this.w2 = 1;
		this.b = ArrayHelper.fillArray(lineSize, lines, w1); 	// m=lineSize - макс. число кластеров
		this.t = ArrayHelper.fillArray(lines, lineSize, w2);	// n=lines - размерность входящих векоторов
		this.bCopy = new double[lineSize][lines];
		this.tCopy = new double[lines][lineSize];
		LOG.debug("System initialization was finished successcfully");
	}

	public void setPotentialViruses(double[][] potentialViruses) {
		this.potentialViruses = potentialViruses;
	}
	
	public void educate() {
		while (needNewEra()) {
			LOG.trace("Need new era for education: " + needNewEra());
			startEra(knowledges);
			bCopy = ArrayHelper.creareCopy(b);
			tCopy = ArrayHelper.creareCopy(t);
		}
	}
	
	private void startEra(double[][] input) {
		LOG.trace("New education era was started");
		for (int i = 0; i < input.length; i++) {
			double[] UinputY = countUinputY(input[i]);
			executeEducation(input[i], UinputY);
		}
		LOG.trace("New education era was finished");
	}

	private void executeEducation(double[] input, double[] UinputY) {
		int neuronWinner = findNeuronWinner(UinputY);
		double[] UoutZ = countUOutZ(input, t[neuronWinner]);
		double neuronNorma = countNorma(UoutZ);
		double inputNorma = countNorma(input);
		LOG.trace("neuronNorma=" + neuronNorma + " inputNorma=" + inputNorma);
		boolean newImage = isImageIdentified(inputNorma, neuronNorma);
		LOG.trace("newImage: " + newImage);

		if (newImage) {
			updateKnowledges(neuronWinner, UoutZ, neuronNorma);
		} else { // again to find new neuron winner
			UinputY[neuronWinner] = -1;
			LOG.trace("There is no needed neuron! Repeat education.");
			executeEducation(input, UinputY);
		}
	}
	
	private boolean needNewEra() {
		return !(Arrays.deepEquals(b, bCopy) || Arrays.deepEquals(t, tCopy));
	}

	public List<Double[]> findViruses() {
		List<Double[]> viruses = new ArrayList<>();
		for (double[] line : potentialViruses) {
			if (isVirus(line)) {
				viruses.add(ArrayUtils.toObject(line));
			}
		}
		return viruses;
	}

	private boolean isVirus(double[] input) {
		double[] UinputY = countUinputY(input);
		int neuronWinner = findNeuronWinner(UinputY);
		if (neuronWinner < 0) {
			return false;
		}
		double[] UoutZ = countUOutZ(input, t[neuronWinner]);
		double neuronNorma = countNorma(UoutZ);
		double inputNorma = countNorma(input);

		LOG.debug("neuronNorma=" + neuronNorma + " inputNorma=" + inputNorma);

		boolean identified = isImageIdentified(inputNorma, neuronNorma);
		if (identified) {
			updateKnowledges(neuronWinner, UoutZ, neuronNorma);
			return true;
		}
		return false;
	}
	
	private void updateKnowledges(int neuronWinner, double[] UoutZ,
			double neuronNorma) {
		for (int j = 0; j < b.length; j++) {
			b[j][neuronWinner] = (L * UoutZ[j]) / (L - 1 + neuronNorma);
		}
		for (int j = 0; j < t[neuronWinner].length; j++) {
			t[neuronWinner][j] = UoutZ[j];
		}
		LOG.trace("Knowledges were updated");
	}
	

	private static double[] countUOutZ(double[] inputLine, double[] t) {
		double[] UoutZ = Arrays.copyOf(inputLine, inputLine.length);
		for (int i = 0; i < UoutZ.length; i++) {
			UoutZ[i] *= t[i];
		}
		return UoutZ;
	}
	

	private boolean isImageIdentified(double inputNorma, double neuronNorma) {
		return (neuronNorma / inputNorma) > p;
	}

	private double[] countUinputY(double[] inputLine) {
		double[] j = new double[b[0].length];
		for (int i = 0; i < b[0].length; i++) {
			double resultJ = 0;
			for (int k = 0; k < b.length; k++) {
				resultJ += inputLine[k] * b[k][i];
			}
			j[i] = resultJ;
		}
		return j;
	}
	

	private int findNeuronWinner(double[] j) {
		double maxDoubleValue = Double.MIN_VALUE;
		int index = -1;
		for (int i = 0; i < j.length; i++) {
			if (j[i] > maxDoubleValue) {
				maxDoubleValue = j[i];
				index = i;
			}
		}
		LOG.debug("winner is " + index + " with value = " + maxDoubleValue);
		return index;
	}

	
	private double countNorma(double[] inputVector) {
		double norma = 0;
		for (double i : inputVector) {
			norma += i;
		}
		return norma;
	}

}
