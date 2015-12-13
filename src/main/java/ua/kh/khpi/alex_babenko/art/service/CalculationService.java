package ua.kh.khpi.alex_babenko.art.service;

import ua.kh.khpi.alex_babenko.art.entity.Knowledge;
import ua.kh.khpi.alex_babenko.art.entity.Line;

import java.math.BigDecimal;

public interface CalculationService {
    double[] countUinputY(double[] inputLine, Knowledge knowledge);

    double[] countUOutZ(double[] inputLine, Line t);

    int findNeuronWinnerIndex(double[] j);

    double countNorma(double[] inputVector);

    boolean isImageIdentified(double inputNorma, double neuronNorma);

    BigDecimal calculateNewNeuronValue(double UoutZ, double neuronNorma);
}
