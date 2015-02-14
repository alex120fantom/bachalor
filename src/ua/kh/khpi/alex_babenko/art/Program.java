package ua.kh.khpi.alex_babenko.art;

import java.io.IOException;
import java.util.Arrays;

public class Program {

	private double[][] input = null;
	private String fileName;

	private Integer m;// = 4; // макс. число кластеров
	private Integer n;// = 5; // размерность входящих векоторов

	private double[][] b;// = new double[n][m]; // коефициенты весов
	private double[][] t;// = new double[m][n]; // значения
	private double[][] bCopy;// = new double[n][m]; // коефициенты весов;
	private double[][] tCopy;// = new double[m][n]; // значения

	private double L;
	private double p;// = 0.8;

	private double w1;// = 1 / (1 + n.doubleValue()); // изначальные веса
	private double w2;// = 1;

	
	public Program(String fileName) {
		this.fileName = fileName;
		initializeSystem();
	}

	private void initializeSystem() {
		try {
			this.m = Util.countLines(fileName);
			this.n = Util.countElements(fileName);
			input = Util.readMatrixFromFile(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.b = new double[n][m];
		this.t = new double[m][n];
		this.bCopy = new double[n][m];
		this.tCopy = new double[m][n];
		this.L = 2;
		this.p = 0.75;
		this.w1 = 1 / (1 + n.doubleValue()); // изначальные веса
		this.w2 = 1;
		fillB();
		fillT();
		
	}

	public void execute() throws IOException {
		educate();
		double[] virus = {1, 1, 0, 1, 0};

		printB(b);
		printT(t);

		System.out.println("virus: " + executeIdentifying(virus));
	}

	public void educate() {
		while (true) {
			startEra(input);
//			System.out.println("----needNewEra---- " + needNewEra());
			if (needNewEra()) {
				makeCopies();
				continue;
			} else {
				break;
			}
		}
	}
	
	public void identify(String fileName) {
		try {
			double[][] potentialViruses = Util.readMatrixFromFile(fileName);
			for (double[] line : potentialViruses) {
				boolean isVirus = executeIdentifying(line);
				System.out.println(Arrays.toString(line) + "is virus: " + isVirus);
			}
		} catch (IOException e) {
			e.printStackTrace();
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

	private void startEra(double[][] input) {
		// resetChangesCounter();
		for (int i = 0; i < input.length; i++) {

			double[] UinputY = countUinputY(input[i]);
			executeEducation(input[i], UinputY);

		}
	}

	private void executeEducation(double[] input, double[] UinputY) {
		int neuronWinner = findNeuronWinner(UinputY);

		double[] UoutZ = countUOutZ(input, t[neuronWinner]);

		double neuronNorma = countNorma(UoutZ);
		double inputNorma = countNorma(input);

//		System.out.println("neuronNorma=" + neuronNorma + " inputNorma="
//				+ inputNorma);

		boolean newImage = isIdentified(inputNorma, neuronNorma);
//		System.out.println("newImage: " + newImage);

		if (newImage) {
			updateKnowledges(neuronWinner, UoutZ, neuronNorma);
		} else { // again to find new neuron winner
			UinputY[neuronWinner] = -1;
//			System.out.println("!!!!!no such!!!!");
			executeEducation(input, UinputY);
		}
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
		
		System.out.println("neuronNorma=" + neuronNorma + " inputNorma="
				+ inputNorma);
		boolean identified = isIdentified(inputNorma, neuronNorma);
		
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
	}

	private static double[] countUOutZ(double[] inputLine, double[] t) {
		double[] UoutZ = Arrays.copyOf(inputLine, inputLine.length);
		for (int i = 0; i < UoutZ.length; i++) {
			UoutZ[i] *= t[i];
		}
		return UoutZ;
	}

	private boolean isIdentified(double inputNorma, double neuronNorma) {
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
//		System.out.println("J results: " + Arrays.toString(j));
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
		System.out.println("winner is " + index + " with value = "
				+ maxDoubleValue);
		return index;
	}

	private double countNorma(double[] inputVector) {
		double norma = 0;
		for (double i : inputVector) {
			norma += i;
		}
		return norma;
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

}
