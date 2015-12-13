package ua.kh.khpi.alex_babenko.art.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import ua.kh.khpi.alex_babenko.art.entity.Line;

import java.util.Arrays;

@Service
public class DefaultCalculationService implements CalculationService {

    private static final Logger LOG = Logger.getLogger(DefaultCalculationService.class);

    @Override
    public double[] countUOutZ(double[] inputLine, Line t) {
        double[] UoutZ = Arrays.copyOf(inputLine, inputLine.length);
        for (int i = 0; i < UoutZ.length; i++) {
            UoutZ[i] *= t.getLineValue().get(i).doubleValue(); // TODO: rewrite
        }
        return UoutZ;
    }

    @Override
    public int findNeuronWinnerIndex(double[] j) {
        double maxDoubleValue = Double.MIN_VALUE;
        int index = -1;
        for (int i = 0; i < j.length; i++) {
            if (j[i] > maxDoubleValue) {
                maxDoubleValue = j[i];
                index = i;
            }
        }
        LOG.trace("winner is " + index + " with value = " + maxDoubleValue);
        return index;
    }

    @Override
    public double countNorma(double[] inputVector) {
        double norma = 0;
        for (double i : inputVector) {
            norma += i;
        }
        return norma;
    }

    @Override
    public boolean isImageIdentified(double inputNorma, double neuronNorma, double p) {
        return (neuronNorma / inputNorma) > p;
    }

}
