package ua.kh.khpi.alex_babenko;

import java.util.Arrays;
import java.util.Collections;

public class Main {

	public static void main(String[] args) {

		// Initialize
		int m = 4; // макс. число кластеров
		Integer n = 5; // размерность входящих векоторов
		double p = 0.8; // параметр подобия
		double L = 2; // параметр для адаптации весов

		double[][] input = { 
				{ 1, 1, 0, 0, 0 }, 
				{ 0, 0, 1, 1, 0 },
				{ 1, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 1, 1 } };

		double w1 = 1 / (1 + n.doubleValue()); // изначальные веса
		double w2 = 1;

		double[][] b = new double[n][m]; // коефициенты весов
		double[][] t = new double[m][n]; // значения

		fillB(b, w1);
		fillT(t, w2);

		
		// Counting
		for (int i = 0; i < input.length; i++) {
			
			double[] UinputY = countUinputY(input[i], b);
			
			int neuronWinner = findNeuronWinner(UinputY);
			
			double[] UoutZ = countUOutZ(input[i], t[neuronWinner]);

			double neuronNorma = countNorma(UoutZ);
			double inputNorma = countNorma(input[i]);
			
			System.out.println("neuronNorma=" + neuronNorma + " inputNorma="+inputNorma);

			boolean newImage = isNewImage(inputNorma, neuronNorma, p);
			System.out.println("newImage: " + newImage);

			if (newImage) {
				for (int j = 0; j < b.length; j++) {
					b[j][neuronWinner] = (L * UoutZ[j]) / (L - 1 + neuronNorma);
				}
				for (int j = 0; j < t[neuronWinner].length; j++) {
					t[neuronWinner][j] = UoutZ[j];
				}
			} else {
				UinputY[neuronWinner] = -1;
				System.out.println("!!!!!no such!!!!");
			}
		}

		printB(b);
		printT(t);

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
