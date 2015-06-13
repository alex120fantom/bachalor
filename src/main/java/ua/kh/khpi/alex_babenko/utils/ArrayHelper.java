package ua.kh.khpi.alex_babenko.utils;


public class ArrayHelper {

	public static double[][] creareCopy(double[][] source) {
		double[][] target = new double[source.length][source[0].length];
		for (int i = 0; i < source.length; i++) {
			for (int j = 0; j < source[i].length; j++) {
				target[i][j] = source[i][j];
			}
		}
		return target;
	}
	

	public static double[][] fillArray(int height, int width, double value) {
		double[][] array = new double[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				array[i][j] = value;
			}
		}
		return array;
	}

}
