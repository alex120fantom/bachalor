package ua.kh.khpi.alex_babenko;

import java.io.IOException;

import org.apache.log4j.Logger;

import ua.kh.khpi.alex_babenko.art.Program;

public class Main {
	
	private static final Logger LOG = Logger.getLogger(Main.class);
	
	public static void main(String[] args) throws IOException {
		
		LOG.info("Main started");
		
		Program program = new Program("base_knowledge.txt");
		program.setFileNamePotentialViruses("viruses.txt");
		
		Thread neuralNetwork  = new Thread(program);
		neuralNetwork.start();
		
	}

}
