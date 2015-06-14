package ua.kh.khpi.alex_babenko;

import static ua.kh.khpi.alex_babenko.utils.PropertyEnum.FILE_KNOWLEDGE;
import static ua.kh.khpi.alex_babenko.utils.PropertyEnum.FILE_VIRUSES;
import static ua.kh.khpi.alex_babenko.utils.PropertyEnum.NEURON_ADAPTATION_PARAMETER;
import static ua.kh.khpi.alex_babenko.utils.PropertyEnum.NEURON_SIMILARITY_COFFICIENT;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import ua.kh.khpi.alex_babenko.art.Network;
import ua.kh.khpi.alex_babenko.exceptions.EmptyDataException;
import ua.kh.khpi.alex_babenko.utils.FileHelper;
import ua.kh.khpi.alex_babenko.utils.Printer;
import ua.kh.khpi.alex_babenko.utils.PropertiesHelper;
import ua.kh.khpi.alex_babenko.utils.PropertyEnum;

public class Main {
	
	private static final Logger LOG = Logger.getLogger(Main.class);
	private final static long READING_TIMEOUT = Long.parseLong(PropertiesHelper.getPropertyByCode(PropertyEnum.FILE_READING_TIMEOUT));
	private static final String FILE_KNOWLEDGE_NAME = PropertiesHelper.getPropertyByCode(FILE_KNOWLEDGE);
	private static final String FILE_VIRUSES_NAME = PropertiesHelper.getPropertyByCode(FILE_VIRUSES);
	private static final String P_STRING = PropertiesHelper.getPropertyByCode(NEURON_SIMILARITY_COFFICIENT);
	private static final String L_STRING = PropertiesHelper.getPropertyByCode(NEURON_ADAPTATION_PARAMETER);

	public static void main(String[] args) {
		LOG.info("Main started");
		Network network = prepareNetwork();
		executeNetwork(network);
	}

	private static Network prepareNetwork() {
		Network network = null;
		try {
			int lines = FileHelper.countLines(FILE_KNOWLEDGE_NAME);
			int lineSize = FileHelper.countLineSize(FILE_KNOWLEDGE_NAME);
			double[][] knowleges = FileHelper.readMatrixFromFile(FILE_KNOWLEDGE_NAME);
			double p = Double.parseDouble(P_STRING);
			double L = Double.parseDouble(L_STRING);
			network = new Network(lines, lineSize, knowleges, p, L);
			network.setPotentialViruses(readFile());
		} catch (IOException e) {
			LOG.error(e);
			throw new RuntimeException();
		}
		LOG.debug("Network preparation finished");
		return network;
	}

	private static void executeNetwork(Network network) {
		network.educate();
		while (true) {
			try {
				List<Double[]> viruses = network.findViruses();
				Printer.printResult(viruses);
				LOG.info("Waiting for the next file.");
				Thread.sleep(READING_TIMEOUT);
				network.setPotentialViruses(readFile());
			} catch (InterruptedException | IOException e) {
				LOG.error(e);
			} catch (EmptyDataException e) {
				LOG.info(e.getMessage());
				continue;
			}
		}
	}

	private static double[][] readFile() throws IOException {
		double[][] result = FileHelper.readMatrixFromFile(FILE_VIRUSES_NAME);
		if (ArrayUtils.isNotEmpty(result) && ArrayUtils.isNotEmpty(result[0])) {
			return result;
		}
		throw new EmptyDataException("No info in the file");
	}

}
