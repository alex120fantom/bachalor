package ua.kh.khpi.alex_babenko.art;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.kh.khpi.alex_babenko.art.entity.Knowledge;
import ua.kh.khpi.alex_babenko.art.entity.Line;
import ua.kh.khpi.alex_babenko.art.service.CalculationService;
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
    private Knowledge virusesKnowledge;
    @Autowired
    private CalculationService calculationService;

    @PostConstruct
    public void setUp() throws IOException {
        int lines = fileService.countLines(fileKnowledgeName);
        Integer lineSize = fileService.countLineSize(fileKnowledgeName);

        double w1 = 1 / (1 + lineSize.doubleValue()); // изначальные веса
        double w2 = 1;

        virusesKnowledge.setB(arrayService.buildLineMatrix(lineSize, lines, w1));
        virusesKnowledge.setT(arrayService.buildLineMatrix(lines, lineSize, w2));
    }

	public void educate(double[][] knowledges) {
		LOG.debug("Start network education");
        while (needNewEra()) {
            LOG.debug("Need new era for education: " + needNewEra());
            startEra(knowledges);

            virusesKnowledge.setbCopy(arrayService.createCopy(virusesKnowledge.getB()));
            virusesKnowledge.settCopy(arrayService.createCopy(virusesKnowledge.getT()));
		}
		LOG.debug("Finish network education");
	}

    private boolean needNewEra() {
        return !weightsWereChanged();
    }

    private boolean weightsWereChanged() {
        return checkWeighChanges(virusesKnowledge.getB(), virusesKnowledge.getbCopy()) ||
                checkWeighChanges(virusesKnowledge.getT(), virusesKnowledge.gettCopy());
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
		int neuronWinner = calculationService.findNeuronWinnerIndex(UinputY);
        Line nuronWinnerLineT = virusesKnowledge.getT().get(neuronWinner);
        double[] UoutZ = calculationService.countUOutZ(knowledgesLine, nuronWinnerLineT);//[neuronWinner]);
		double neuronNorma = calculationService.countNorma(UoutZ);
		double inputNorma = calculationService.countNorma(knowledgesLine);
		LOG.trace("neuronNorma=" + neuronNorma + " inputNorma=" + inputNorma);
		boolean newImage = calculationService.isImageIdentified(inputNorma, neuronNorma, p);
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
        for (int j = 0; j < virusesKnowledge.getB().size(); j++) {
            BigDecimal newNeuron = BigDecimal.valueOf((L * UoutZ[j]) / (L - 1 + neuronNorma));
            List<BigDecimal> lineValue = virusesKnowledge.getB().get(j).getLineValue();
            lineValue.set(neuronWinnerIndex, newNeuron);
        }
        for (int j = 0; j < virusesKnowledge.getT().get(neuronWinnerIndex).getLineValue().size(); j++) {
            Line nuerinWinnerLine = virusesKnowledge.getT().get(neuronWinnerIndex);
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
		int neuronWinner = calculationService.findNeuronWinnerIndex(UinputY);
		if (neuronWinner < 0) {
			return false;
		}
        Line nueronWinnerLineT = virusesKnowledge.getT().get(neuronWinner);
        double[] UoutZ = calculationService.countUOutZ(input, nueronWinnerLineT);
		double neuronNorma = calculationService.countNorma(UoutZ);
		double inputNorma = calculationService.countNorma(input);

		LOG.debug("neuronNorma=" + neuronNorma + " inputNorma=" + inputNorma);

		boolean identified = calculationService.isImageIdentified(inputNorma, neuronNorma, p);
		if (identified) {
			updateKnowledges(neuronWinner, UoutZ, neuronNorma);
			return true;
		}
		return false;
	}

    private double[] countUinputY(double[] inputLine) {
        int lineSize = virusesKnowledge.getB().get(0).getLineValue().size();
        double[] j = new double[lineSize];
        for (int i = 0; i < lineSize; i++) {
            double resultJ = 0;
            for (int k = 0; k < virusesKnowledge.getB().size(); k++) {
                resultJ += inputLine[k] * virusesKnowledge.getB().get(k).getLineValue().get(i).doubleValue(); // TODO //b[k][i];
            }
            j[i] = resultJ;
        }
        return j;
    }

}
