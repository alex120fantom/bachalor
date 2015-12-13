package ua.kh.khpi.alex_babenko.art;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.kh.khpi.alex_babenko.services.ArrayService;
import ua.kh.khpi.alex_babenko.services.FileService;

import javax.annotation.PostConstruct;

import static java.text.MessageFormat.format;

@Component
public class Network {

	private static final Logger LOG = Logger.getLogger(Network.class);

    private double[][] knowledges;

    private double[][] b; // коефициенты весов
    private double[][] t; // значения
    private double[][] bCopy; // коефициенты весов;
	private double[][] tCopy; // значения

    @Value("${file.knowledge}")
    private String fileKnowledgeName;

    @Value("${neuron.adaptation.parameter}")
	private double L;
    @Value("${nueron.similarity.coefficient}")
	private double p;

    @Autowired
    private FileService fileService;
    @Autowired
    private ArrayService arrayService;

    @PostConstruct
    public void setUp() throws IOException {
        int lines = fileService.countLines(fileKnowledgeName);
        Integer lineSize = fileService.countLineSize(fileKnowledgeName);

        double w1 = 1 / (1 + lineSize.doubleValue()); // изначальные веса
        double w2 = 1;
        this.b = arrayService.fillArray(lineSize, lines, w1); 	// m=lineSize - макс. число кластеров
        this.t = arrayService.fillArray(lines, lineSize, w2);	// n=lines - размерность входящих векоторов
        this.bCopy = new double[lineSize][lines];
        this.tCopy = new double[lines][lineSize];
    }

	public void educate(double[][] knowledges) {
		LOG.debug("Start network education");
		while (needNewEra()) {
			LOG.trace("Need new era for education: " + needNewEra());
			startEra(knowledges);
			bCopy = arrayService.createCopy(b);
			tCopy = arrayService.createCopy(t);
		}
		LOG.debug("Finish network education");
	}

    private boolean needNewEra() {
        return !(Arrays.deepEquals(b, bCopy) || Arrays.deepEquals(t, tCopy));
    }

	private void startEra(double[][] knowledges) {
		LOG.trace("New education era was started");
        for (double[] knowledge : knowledges) {
            double[] UinputY = countUinputY(knowledge);
            executeEducation(knowledge, UinputY);
        }
		LOG.trace("New education era was finished");
	}

	private void executeEducation(double[] knowledgesLine, double[] UinputY) {
		int neuronWinner = findNeuronWinner(UinputY);
		double[] UoutZ = countUOutZ(knowledgesLine, t[neuronWinner]);
		double neuronNorma = countNorma(UoutZ);
		double inputNorma = countNorma(knowledgesLine);
		LOG.trace("neuronNorma=" + neuronNorma + " inputNorma=" + inputNorma);
		boolean newImage = isImageIdentified(inputNorma, neuronNorma);
		LOG.trace("newImage: " + newImage);

		if (newImage) {
			updateKnowledges(neuronWinner, UoutZ, neuronNorma);
		} else { // again to find new neuron winner
			UinputY[neuronWinner] = -1;
			LOG.trace("There is no needed neuron! Repeat education.");
			executeEducation(knowledgesLine, UinputY);
		}
	}

    private void updateKnowledges(int neuronWinner, double[] UoutZ, double neuronNorma) {
        for (int j = 0; j < b.length; j++) {
            b[j][neuronWinner] = (L * UoutZ[j]) / (L - 1 + neuronNorma);
        }
        for (int j = 0; j < t[neuronWinner].length; j++) {
            t[neuronWinner][j] = UoutZ[j];
        }
        LOG.trace("Knowledges were updated");

    }

	public List<Double[]> findViruses(double[][] potentialViruses) {
		List<Double[]> viruses = new ArrayList<>();
		for (double[] line : potentialViruses) {
			if (isVirus(line)) {
				viruses.add(ArrayUtils.toObject(line));
			}
		}
		return viruses;
	}

	private boolean isVirus(double[] input) {
		double[] UinputY = countUinputY(input);
		int neuronWinner = findNeuronWinner(UinputY);
		if (neuronWinner < 0) {
			return false;
		}
		double[] UoutZ = countUOutZ(input, t[neuronWinner]);
		double neuronNorma = countNorma(UoutZ);
		double inputNorma = countNorma(input);

		LOG.debug("neuronNorma=" + neuronNorma + " inputNorma=" + inputNorma);

		boolean identified = isImageIdentified(inputNorma, neuronNorma);
		if (identified) {
			updateKnowledges(neuronWinner, UoutZ, neuronNorma);
			return true;
		}
		return false;
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
        LOG.trace("winner is " + index + " with value = " + maxDoubleValue);
        return index;
    }

    private static double[] countUOutZ(double[] inputLine, double[] t) {
        double[] UoutZ = Arrays.copyOf(inputLine, inputLine.length);
        for (int i = 0; i < UoutZ.length; i++) {
            UoutZ[i] *= t[i];
        }
        return UoutZ;
    }

    private double countNorma(double[] inputVector) {
        double norma = 0;
        for (double i : inputVector) {
            norma += i;
        }
        return norma;
    }

	private boolean isImageIdentified(double inputNorma, double neuronNorma) {
		return (neuronNorma / inputNorma) > p;
	}

}
