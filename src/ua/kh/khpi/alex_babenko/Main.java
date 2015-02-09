package ua.kh.khpi.alex_babenko;

import java.util.Arrays;
import java.util.Collections;

public class Main {
	
	private static boolean somethingChanged = true;

	private static Integer m = 4; // макс. число кластеров
	private static Integer n = 5; // размерность входящих векоторов
	private static double[][] b = new double[n][m]; // коефициенты весов
	private static double[][] t = new double[m][n]; // значения
	
	private static double[][] bCopy = new double[n][m]; // коефициенты весов;
	private static double[][] tCopy = new double[m][n]; // значения
	
	private static double L = 2;
	private static double p = 0.8;
	
	private static double w1 = 1 / (1 + n.doubleValue()); // изначальные веса
	private static double w2 = 1;
	
	private static double[][] input = { 
			{ 1, 1, 0, 0, 0 }, 
			{ 0, 0, 1, 1, 0 },
			{ 1, 0, 0, 0, 0 }, 
			{ 0, 0, 0, 1, 1 } };


	/**
	 * Starting the program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		fillB(b, w1);
		fillT(t, w2);
		
		makeCopies();
		while (true) {
			startEra(input);
			System.out.println("----changesCounter---- " + (Arrays.deepEquals(b, bCopy) && Arrays.deepEquals(t, tCopy)));
			if (needNewEra()) {
				makeCopies();
				
				startEra(input);
			} else {
				break;
			} 
		}

		printB(b);
		printT(t);

	}

	private static boolean needNewEra() {
		return !(Arrays.deepEquals(b, bCopy) && Arrays.deepEquals(t, tCopy));
	}

	private static void makeCopies() {
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
	
	private static void startEra(double[][] input) {
		resetChangesCounter();
		for (int i = 0; i < input.length; i++) {

			double[] UinputY = countUinputY(input[i], b);
			executeCalculations(input[i], UinputY);
		
		}
	}

	private static void resetChangesCounter() {
		somethingChanged = false;
	}
	
	private static void executeCalculations(double[] input, double[] UinputY) {
		int neuronWinner = findNeuronWinner(UinputY);
		
		double[] UoutZ = countUOutZ(input, t[neuronWinner]);

		double neuronNorma = countNorma(UoutZ);
		double inputNorma = countNorma(input);
		
		System.out.println("neuronNorma=" + neuronNorma + " inputNorma="+inputNorma);

		boolean newImage = isNewImage(inputNorma, neuronNorma, p);
		System.out.println("newImage: " + newImage);

		if (newImage) {
			somethingChanged = true;
			for (int j = 0; j < b.length; j++) {
				b[j][neuronWinner] = (L * UoutZ[j]) / (L - 1 + neuronNorma);
			}
			for (int j = 0; j < t[neuronWinner].length; j++) {
				t[neuronWinner][j] = UoutZ[j];
			}
		} else {		// again to find new neuron winner
			UinputY[neuronWinner] = -1;
			System.out.println("!!!!!no such!!!!");
			executeCalculations(input, UinputY);
		}
	}

	private static double[] countUOutZ(double[] inputLine, double[] t) {
		double[] UoutZ = Arrays.copyOf(inputLine, inputLine.length);
		for (int i = 0; i < UoutZ.length; i++) {
			UoutZ[i] *= t[i];
		}
		return UoutZ;
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

	private static boolean isNewImage(double inputNorma, double neuronNorma,
			double p) {
		return (neuronNorma / inputNorma) > p;
	}

	private static double[] countUinputY(double[] inputLine, double[][] b) {
		double[] j = new double[b[0].length];
		for (int i = 0; i < b[0].length; i++) {
			double resultJ = 0;
			for (int k = 0; k < b.length; k++) {
				resultJ += inputLine[k] * b[k][i];
			}
			j[i] = resultJ;
		}
		System.out.println("J results: " + Arrays.toString(j));
		return j;
	}

	private static int findNeuronWinner(double[] j) {
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

	private static double countNorma(double[] inputVector) {
		double norma = 0;
		for (double i : inputVector) {
			norma += i;
		}
		return norma;
	}

	private static double[][] fillB(double[][] b, double w1) {
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b[i].length; j++) {
				b[i][j] = w1;
			}
		}
		return b;
	}

	private static double[][] fillT(double[][] t, double w2) {
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < t[i].length; j++) {
				t[i][j] = w2;
			}
		}
		return t;
	}

}
