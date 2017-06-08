package ua.kh.khpi.alex_babenko.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class Printer {
	
	private static final Logger LOG = Logger.getLogger(Printer.class);
	
	public static void printResult(List<Double[]> result) {
        result.stream()
                .map(Printer::createLine)
                .forEach(LOG::warn);
	}

	private static String createLine(Double[] doubles) {
        String line = StringUtils.EMPTY;
		for (Double value : doubles) {
			line += value.intValue() + " "; 
		}
		return line;
	}

}
