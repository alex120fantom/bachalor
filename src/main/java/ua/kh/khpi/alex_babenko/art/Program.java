package ua.kh.khpi.alex_babenko.art;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class Program implements Runnable {
	
	private static final Logger LOG = Logger.getLogger(Program.class);

	private static final long ONE_MINUTE = 60_000L;
	private double[][] input = null;
	private String fileName;

	private Integer m; // макс. число кластеров
	private Integer n; // размерность входящих векоторов

	private double[][] b; // коефициенты весов
	private double[][] t; // значения
	private double[][] bCopy; // коефициенты весов;
	private double[][] tCopy; // значения

	private double L;
	private double p;

	private double w1; // изначальные веса
	private double w2;
	
	private String fileNamePotentialViruses;

	public Program(String fileName) {
		this.fileName = fileName;
		initializeSystem();
	}

	private void initializeSystem() {
		LOG.debug("System initialization was started");
		try {
			this.m = Util.countLines(fileName);
			this.n = Util.countElements(fileName);
			input = Util.readMatrixFromFile(fileName);
		} catch (IOException e) {
			LOG.error(e);
		}
		this.b = new double[n][m];
		this.t = new double[m][n];
		this.bCopy = new double[n][m];
		this.tCopy = new double[m][n];
		this.L = 2;
		this.p = 0.9;
		this.w1 = 1 / (1 + n.doubleValue()); // изначальные веса
		this.w2 = 1;
		fillB();
		fillT();
		LOG.debug("System initialization was finished successcfully");
	}
	
	private double[][] fillB() {
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b[i].length; j++) {
				b[i][j] = w1;
			}
		}
		return b;
	}

	private double[][] fillT() {
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < t[i].length; j++) {
				t[i][j] = w2;
			}
		}
		return t;
	}
	
	public void setFileNamePotentialViruses(String fileNamePotentialViruses) {
		this.fileNamePotentialViruses = fileNamePotentialViruses;
	}

	private void educate() {
		while (true) {
			startEra(input);
			LOG.trace("Need new era for education: " + needNewEra());
			if (needNewEra()) {
				makeCopies();
				continue;
			} else {
				break;
			}
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

		// System.out.println("neuronNorma=" + neuronNorma + " inputNorma=" + inputNorma);

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

	private void makeCopies() {
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b[i].length; j++) {
				bCopy[i][j] = b[i][j];
			}
		}
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < t[i].length; j++) {
				tCopy[i][j] = t[i][j];
			}
		}
	}

	private List<Double[]> identify() {
		try {
			double[][] potentialViruses = Util.readMatrixFromFile(fileNamePotentialViruses);
			return findViruses(potentialViruses);
		} catch (IOException e) {
			LOG.error(e);
			throw new RuntimeException(e);
		}
	}

	private List<Double[]> findViruses(double[][] potentialViruses) {
		List<Double[]> viruses = new ArrayList<>();
		for (double[] line : potentialViruses) {
			boolean isVirus = executeIdentifying(line);
			if (isVirus) {
				viruses.add(ArrayUtils.toObject(line));
			}
		}
		return viruses;
	}

	private boolean executeIdentifying(double[] input) {
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
		// System.out.println("J results: " + Arrays.toString(j));
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

	private static void printResult(List<Double[]> result) {
		LOG.warn("VIRUSES: ");
		for (Double[] doubles : result) {
			String line = StringUtils.EMPTY;
			for (Double value : doubles) {
				line += value.intValue() + " "; 
			}
			LOG.warn(line);
		}
	}
	
	private static void printB(double[][] b) {
		System.out.println("B: ");
		for (double[] lineB : b) {
			for (double valueB : lineB) {
				System.out.print(valueB + " || ");
			}
			System.out.println();
		}
	}

	private static void printT(double[][] t) {
		System.out.println("T:");
		for (double[] lineT : t) {
			for (double valueT : lineT) {
				System.out.print(valueT + " || ");
			}
			System.out.println();
		}
	}

	@Override
	public void run() {
		this.educate();
		while (true) {
			List<Double[]> viruses = identify();
			printResult(viruses);
			try {
				LOG.info("Waiting for the next file.");
				Thread.sleep(ONE_MINUTE);
			} catch (InterruptedException e) {
				LOG.error(e);
			}
		}
	}

}
