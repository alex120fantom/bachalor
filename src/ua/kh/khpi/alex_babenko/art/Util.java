package ua.kh.khpi.alex_babenko.art;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Util {
	public static double[][] readMatrixFromFile(String filename)
			throws IOException {
		int m = countLines(filename);
		int n = countElements(filename);
		double[][] matrix = new double[m][n];
		try {
			Scanner input = new Scanner(new File(filename));
			while (input.hasNextLine()) {
				for (int i = 0; i < m; i++) {
					for (int j = 0; j < n; j++) {
						try {
							matrix[i][j] = input.nextDouble();
						} catch (java.util.NoSuchElementException e) {
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return matrix;
	}

	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count + 1;
		} finally {
			is.close();
		}
	}

	public static int countElements(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		try {
			String arr[] = new String[0];
			if ((line = br.readLine()) != null) {
				arr = line.split(" ");
			}
			return arr.length;
		} finally {
			br.close();
		}
	}
}
