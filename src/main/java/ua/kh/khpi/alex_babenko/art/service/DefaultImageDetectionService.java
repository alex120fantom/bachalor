package ua.kh.khpi.alex_babenko.art.service;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kh.khpi.alex_babenko.art.entity.Knowledge;
import ua.kh.khpi.alex_babenko.art.entity.Line;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultImageDetectionService implements ImageDetectionService {

    private static final Logger LOG = Logger.getLogger(DefaultImageDetectionService.class);

    @Autowired
    private KnowledgeService knowledgeService;
    @Autowired
    private CalculationService calculationService;

    @Override
    public List<Double[]> findViruses(double[][] potentialViruses, Knowledge knowledge) {
        List<Double[]> viruses = new ArrayList<>();
        for (double[] line : potentialViruses) {
            if (doesImageDetected(line, knowledge)) {
                viruses.add(ArrayUtils.toObject(line));
            }
        }
        return viruses;
    }


    @Override
    public boolean doesImageDetected(double[] input, Knowledge knowledge) {
        double[] UinputY = calculationService.countUinputY(input, knowledge);
        int neuronWinner = calculationService.findNeuronWinnerIndex(UinputY);
        if (neuronWinner < 0) {
            return false;
        }
        Line nueronWinnerLineT = knowledge.getT().get(neuronWinner);
        double[] UoutZ = calculationService.countUOutZ(input, nueronWinnerLineT);
        double neuronNorma = calculationService.countNorma(UoutZ);
        double inputNorma = calculationService.countNorma(input);

        LOG.debug("neuronNorma=" + neuronNorma + " inputNorma=" + inputNorma);

        boolean identified = calculationService.isImageIdentified(inputNorma, neuronNorma);
        if (identified) {
            LOG.debug("identified=" + identified + ". Updating knowledge" );
            knowledgeService.updateKnowledges(neuronWinner, UoutZ, neuronNorma, knowledge);
            return true;
        }
        return false;
    }

}
