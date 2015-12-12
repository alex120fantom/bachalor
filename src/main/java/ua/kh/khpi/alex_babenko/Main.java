package ua.kh.khpi.alex_babenko;


import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.kh.khpi.alex_babenko.art.Network;
import ua.kh.khpi.alex_babenko.config.Config;
import ua.kh.khpi.alex_babenko.exceptions.EmptyDataException;
import ua.kh.khpi.alex_babenko.services.FileService;
import ua.kh.khpi.alex_babenko.utils.Printer;

@Component
public class Main {
	
	private static final Logger LOG = Logger.getLogger(Main.class);

    @Value("${file.viruses}")
	private String fileVirusesName;
    @Autowired
    private FileService fileService;

    private static Network network;

	public static void main(String[] args) {
		LOG.info("Main started");
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        network = context.getBean(Network.class);
        network.educate();

    }

    @Scheduled(fixedDelayString = "${file.reading.timeout}", initialDelayString = "${file.reading.timeout}" )
    private void execute() {
        LOG.info("fileVirusesName + " + fileVirusesName);
        try {
            List<Double[]> viruses = network.findViruses();
            Printer.printResult(viruses);
            LOG.info("Waiting for the next file.");
            network.setPotentialViruses(readFile());
        } catch (IOException e) {
            LOG.error(e);
        } catch (EmptyDataException e) {
            LOG.info(e.getMessage());
            return;
        }
    }

    private double[][] readFile() throws IOException {
		double[][] result = fileService.readMatrixFromFile(fileVirusesName);
		if (ArrayUtils.isNotEmpty(result) && ArrayUtils.isNotEmpty(result[0])) {
			return result;
		}
		throw new EmptyDataException("No info in the file");
	}

}
