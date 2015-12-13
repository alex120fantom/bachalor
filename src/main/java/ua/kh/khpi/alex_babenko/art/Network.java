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
import ua.kh.khpi.alex_babenko.art.service.KnowledgeService;
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
    @Autowired
    private KnowledgeService knowledgeService;

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
        knowledgeService.educateKnowledge(knowledges, virusesKnowledge);
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
		double[] UinputY = calculationService.countUinputY(input, virusesKnowledge);
		int neuronWinner = calculationService.findNeuronWinnerIndex(UinputY);
		if (neuronWinner < 0) {
			return false;
		}
        Line nueronWinnerLineT = virusesKnowledge.getT().get(neuronWinner);
        double[] UoutZ = calculationService.countUOutZ(input, nueronWinnerLineT);
		double neuronNorma = calculationService.countNorma(UoutZ);
		double inputNorma = calculationService.countNorma(input);

		LOG.debug("neuronNorma=" + neuronNorma + " inputNorma=" + inputNorma);

		boolean identified = calculationService.isImageIdentified(inputNorma, neuronNorma);
		if (identified) {
			knowledgeService.updateKnowledges(neuronWinner, UoutZ, neuronNorma, virusesKnowledge);
			return true;
		}
		return false;
	}

}
