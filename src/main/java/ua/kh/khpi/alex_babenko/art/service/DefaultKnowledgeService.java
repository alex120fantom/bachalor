package ua.kh.khpi.alex_babenko.art.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kh.khpi.alex_babenko.art.entity.Knowledge;
import ua.kh.khpi.alex_babenko.art.entity.Line;
import ua.kh.khpi.alex_babenko.services.ArrayService;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DefaultKnowledgeService implements KnowledgeService {

    private static final Logger LOG = Logger.getLogger(DefaultKnowledgeService.class);

    @Autowired
    private ArrayService arrayService;
    @Autowired
    private CalculationService calculationService;

    @Override
    public void educateKnowledge(double[][] knowledges, Knowledge knowledge) {
        LOG.debug("Start network education");
        while (needNewEra(knowledge)) {
            LOG.debug("Need new era: " + needNewEra(knowledge));
            startEraForKnowledge(knowledges, knowledge);

            knowledge.setbCopy(arrayService.createCopy(knowledge.getB()));
            knowledge.settCopy(arrayService.createCopy(knowledge.getT()));
        }
        LOG.debug("Finish network education");
    }

    private boolean needNewEra(Knowledge knowledge) {
        return !weightsWereChanged(knowledge);
    }

    private boolean weightsWereChanged(Knowledge knowledge) {
        return checkWeighChanges(knowledge.getB(), knowledge.getbCopy()) ||
                checkWeighChanges(knowledge.getT(), knowledge.gettCopy());
    }

    private boolean checkWeighChanges(List<Line> list, List<Line> listCopy) {
        return list.containsAll(listCopy) && listCopy.containsAll(list);
    }

    private void startEraForKnowledge(double[][] knowledges, Knowledge knowledge) {
        LOG.trace("New education era was started");
        for (double[] newKnowledge : knowledges) {
            double[] UinputY = calculationService.countUinputY(newKnowledge, knowledge);
            executeEducation(newKnowledge, UinputY, knowledge);
        }
        LOG.trace("New education era was finished");
    }


    private void executeEducation(double[] knowledgesLine, double[] UinputY, Knowledge knowledge) {
        int neuronWinner = calculationService.findNeuronWinnerIndex(UinputY);
        Line nuronWinnerLineT = knowledge.getT().get(neuronWinner);
        double[] UoutZ = calculationService.countUOutZ(knowledgesLine, nuronWinnerLineT);//[neuronWinner]);
        double neuronNorma = calculationService.countNorma(UoutZ);
        double inputNorma = calculationService.countNorma(knowledgesLine);
        LOG.trace("neuronNorma=" + neuronNorma + " inputNorma=" + inputNorma);
        boolean newImage = calculationService.isImageIdentified(inputNorma, neuronNorma);
        LOG.trace("newImage: " + newImage);

        if (newImage) {
            updateKnowledges(neuronWinner, UoutZ, neuronNorma, knowledge);
        } else { // again to find new neuron winner
            UinputY[neuronWinner] = -1;
            LOG.trace("There is no needed neuron! Repeat education.");
            executeEducation(knowledgesLine, UinputY, knowledge);
        }
    }


    @Override
    public void updateKnowledges(int neuronWinnerIndex, double[] UoutZ, double neuronNorma, Knowledge knowledge) {
        for (int j = 0; j < knowledge.getB().size(); j++) {
            BigDecimal newNeuron = calculationService.calculateNewNeuronValue(UoutZ[j], neuronNorma);
            List<BigDecimal> lineValue = knowledge.getB().get(j).getLineValue();
            lineValue.set(neuronWinnerIndex, newNeuron);
        }
        for (int j = 0; j < knowledge.getT().get(neuronWinnerIndex).getLineValue().size(); j++) {
            Line nuerinWinnerLine = knowledge.getT().get(neuronWinnerIndex);
            List<BigDecimal> lineValue = nuerinWinnerLine.getLineValue();
            lineValue.set(j, BigDecimal.valueOf(UoutZ[j])); //TODO: UoutZ rewrite
        }
        LOG.trace("Knowledges were updated");
    }

}
