package ua.kh.khpi.alex_babenko;

import java.io.IOException;

import ua.kh.khpi.alex_babenko.art.Program;

public class Main {

	public static void main(String[] args) throws IOException {
		Program program = new Program("base_knowledge.txt");
		program.setFileNamePotentialViruses("viruses.txt");
		
		Thread neuralNetwork  = new Thread(program);
		neuralNetwork.start();
		
	}

}
