package ua.kh.khpi.alex_babenko;

import static ua.kh.khpi.alex_babenko.utils.PropertyEnum.FILE_KNOWLEDGE;
import static ua.kh.khpi.alex_babenko.utils.PropertyEnum.FILE_VIRUSES;

import java.io.IOException;

import org.apache.log4j.Logger;

import ua.kh.khpi.alex_babenko.art.Program;
import ua.kh.khpi.alex_babenko.utils.PropertiesHelper;

public class Main {

	private static final Logger LOG = Logger.getLogger(Main.class);

	public static void main(String[] args) throws IOException {

		LOG.info("Main started");

		Program program = new Program(
				PropertiesHelper.getPropertyByCode(FILE_KNOWLEDGE));
		program.setFileNamePotentialViruses(PropertiesHelper
				.getPropertyByCode(FILE_VIRUSES));

		Thread neuralNetwork = new Thread(program);
		neuralNetwork.start();

	}

}
