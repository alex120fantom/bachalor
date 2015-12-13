package ua.kh.khpi.alex_babenko.art;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.kh.khpi.alex_babenko.art.entity.KnowledgeBase;
import ua.kh.khpi.alex_babenko.art.entity.Line;
import ua.kh.khpi.alex_babenko.services.ArrayService;
import ua.kh.khpi.alex_babenko.services.FileService;

import javax.annotation.PostConstruct;

@Component
public class Network {

	private static final Logger LOG = Logger.getLogger(Network.class);

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

    @Autowired
    private KnowledgeBase virusesKnowledgeBase;

    @PostConstruct
    public void setUp() throws IOException {
        int lines = fileService.countLines(fileKnowledgeName);
        Integer lineSize = fileService.countLineSize(fileKnowledgeName);

        double w1 = 1 / (1 + lineSize.doubleValue()); // изначальные веса
        double w2 = 1;

        virusesKnowledgeBase.setB(arrayService.buildLineMatrix(lineSize, lines, w1));
        virusesKnowledgeBase.setT(arrayService.buildLineMatrix(lines, lineSize, w2));
    }

	public void educate(double[][] knowledges) {
		LOG.debug("Start network education");
        while (needNewEra()) {
            LOG.debug("Need new era for education: " + needNewEra());
            startEra(knowledges);

            virusesKnowledgeBase.setbCopy(arrayService.createCopy(virusesKnowledgeBase.getB()));
            virusesKnowledgeBase.settCopy(arrayService.createCopy(virusesKnowledgeBase.getT()));
		}
		LOG.debug("Finish network education");
	}

    private boolean needNewEra() {
        return !weightsWereChanged();
    }

    private boolean weightsWereChanged() {
        return checkWeighChanges(virusesKnowledgeBase.getB(), virusesKnowledgeBase.getbCopy()) ||
                checkWeighChanges(virusesKnowledgeBase.getT(), virusesKnowledgeBase.gettCopy());
    }

    private boolean checkWeighChanges(List<Line> list, List<Line> listCopy) {
        return list.containsAll(listCopy) && listCopy.containsAll(list);
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
        Line nuronWinnerLine = virusesKnowledgeBase.getT().get(neuronWinner);
        double[] UoutZ = countUOutZ(knowledgesLine, nuronWinnerLine);//[neuronWinner]);
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

    private void updateKnowledges(int neuronWinnerIndex, double[] UoutZ, double neuronNorma) {
        for (int j = 0; j < virusesKnowledgeBase.getB().size(); j++) {
            BigDecimal newNeuron = BigDecimal.valueOf((L * UoutZ[j]) / (L - 1 + neuronNorma));
            List<BigDecimal> lineValue = virusesKnowledgeBase.getB().get(j).getLineValue();
            lineValue.set(neuronWinnerIndex, newNeuron);
        }
        for (int j = 0; j < virusesKnowledgeBase.getT().get(neuronWinnerIndex).getLineValue().size(); j++) {
            Line nuerinWinnerLine = virusesKnowledgeBase.getT().get(neuronWinnerIndex);
            List<BigDecimal> lineValue = nuerinWinnerLine.getLineValue();
            lineValue.set(j, BigDecimal.valueOf(UoutZ[j])); //TODO: UoutZ rewrite
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
		double[] UoutZ = countUOutZ(input, virusesKnowledgeBase.getT().get(neuronWinner));
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
        int lineSize = virusesKnowledgeBase.getB().get(0).getLineValue().size();
        double[] j = new double[lineSize];
        for (int i = 0; i < lineSize; i++) {
            double resultJ = 0;
            for (int k = 0; k < virusesKnowledgeBase.getB().size(); k++) {
                resultJ += inputLine[k] * virusesKnowledgeBase.getB().get(k).getLineValue().get(i).doubleValue(); // TODO //b[k][i];
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

    private double[] countUOutZ(double[] inputLine, Line t) {
        double[] UoutZ = Arrays.copyOf(inputLine, inputLine.length);
        for (int i = 0; i < UoutZ.length; i++) {
            UoutZ[i] *= t.getLineValue().get(i).doubleValue(); // TODO: rewrite
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
