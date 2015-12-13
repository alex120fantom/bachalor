package ua.kh.khpi.alex_babenko.art.service;

import ua.kh.khpi.alex_babenko.art.entity.Line;

public interface CalculationService {
    double[] countUOutZ(double[] inputLine, Line t);

    int findNeuronWinnerIndex(double[] j);

    double countNorma(double[] inputVector);

    boolean isImageIdentified(double inputNorma, double neuronNorma, double p);
}
