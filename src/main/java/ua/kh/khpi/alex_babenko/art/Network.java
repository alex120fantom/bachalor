package ua.kh.khpi.alex_babenko.art;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.kh.khpi.alex_babenko.art.entity.Knowledge;
import ua.kh.khpi.alex_babenko.art.service.ImageDetectionService;
import ua.kh.khpi.alex_babenko.art.service.KnowledgeService;
import ua.kh.khpi.alex_babenko.services.ArrayService;
import ua.kh.khpi.alex_babenko.services.FileService;

import javax.annotation.PostConstruct;

@Component
public class Network {

	private static final Logger LOG = Logger.getLogger(Network.class);

    @Value("${file.knowledge}")
    private String fileKnowledgeName;

    @Autowired
    private Knowledge virusesKnowledge;
    @Autowired
    private Knowledge cleanFileKnowledge;
    @Autowired
    private FileService fileService;
    @Autowired
    private ArrayService arrayService;
    @Autowired
    private KnowledgeService knowledgeService;
    @Autowired
    private ImageDetectionService imageDetectionService;

    @PostConstruct
    public void setUp() throws IOException {
        int lines = fileService.countLines(fileKnowledgeName);
        Integer lineSize = fileService.countLineSize(fileKnowledgeName);

        double w1 = 1 / (1 + lineSize.doubleValue()); // изначальные веса
        double w2 = 1;

        virusesKnowledge.setB(arrayService.buildLineMatrix(lineSize, lines, w1));
        virusesKnowledge.setT(arrayService.buildLineMatrix(lines, lineSize, w2));
    }

	public void educateWithViruses(double[][] knowledges) {
        knowledgeService.educateKnowledge(knowledges, virusesKnowledge);
    }

	public void educateWithCleanFiles(double[][] knowledges) {
        knowledgeService.educateKnowledge(knowledges, cleanFileKnowledge);
    }

	public List<Double[]> findViruses(double[][] potentialViruses) {
        return imageDetectionService.findViruses(potentialViruses, virusesKnowledge);
    }

    public boolean doesVirusDetected(double[] virus) {
        return imageDetectionService.doesImageDetected(virus, virusesKnowledge);
    }

    public boolean doesCleanFileDetected(double[] cleanFile) {
        return imageDetectionService.doesImageDetected(cleanFile, cleanFileKnowledge);
    }


}
