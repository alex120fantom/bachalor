package ua.kh.khpi.alex_babenko.art.service;

import ua.kh.khpi.alex_babenko.art.entity.Knowledge;

public interface KnowledgeService {
    void educateKnowledge(double[][] knowledges, Knowledge knowledge);

    void updateKnowledges(int neuronWinnerIndex, double[] UoutZ, double neuronNorma, Knowledge knowledge);
}
