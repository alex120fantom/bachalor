package ua.kh.khpi.alex_babenko.services;

import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class DefaultArrayService implements ArrayService {

	@Override
    public double[][] createCopy(double[][] source) {
        return Arrays.stream(source)
                .map(double[]::clone)
                .toArray(double[][]::new);
    }


    @Override
    public double[][] fillArray(int height, int width, double value) {
		double[][] array = new double[height][width];
        for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				array[i][j] = value;
			}
		}
		return array;
	}

}
