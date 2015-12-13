package ua.kh.khpi.alex_babenko.art.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.kh.khpi.alex_babenko.art.entity.Knowledge;
import ua.kh.khpi.alex_babenko.art.entity.Line;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
public class DefaultCalculationService implements CalculationService {

    private static final Logger LOG = Logger.getLogger(DefaultCalculationService.class);

    @Value("${nueron.similarity.coefficient}")
    private double p;
    @Value("${neuron.adaptation.parameter}")
    private double L;

    @Override
    public double[] countUinputY(double[] inputLine, Knowledge knowledge) {
        int lineSize = knowledge.getB().get(0).getLineValue().size();
        double[] j = new double[lineSize];
        for (int i = 0; i < lineSize; i++) {
            double resultJ = 0;
            for (int k = 0; k < knowledge.getB().size(); k++) {
                resultJ += inputLine[k] * knowledge.getB().get(k).getLineValue().get(i).doubleValue(); // TODO //b[k][i];
            }
            j[i] = resultJ;
        }
        return j;
    }

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
    public boolean isImageIdentified(double inputNorma, double neuronNorma) {
        return (neuronNorma / inputNorma) > p;
    }

    @Override
    public BigDecimal calculateNewNeuronValue(double UoutZ, double neuronNorma) {
        return BigDecimal.valueOf((L * UoutZ) / (L - 1 + neuronNorma));
    }

}
