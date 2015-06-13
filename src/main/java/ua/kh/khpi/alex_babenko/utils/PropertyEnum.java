package ua.kh.khpi.alex_babenko.utils;

public enum PropertyEnum {
	
	FILE_KNOWLEDGE ("file.knowledge"), 
	FILE_VIRUSES ("file.viruses"),
	FILE_READING_TIMEOUT("file.reading.timeout"),
	NEURON_SIMILARITY_COFFICIENT("nueron.similarity.coefficient"),
	NEURON_ADAPTATION_PARAMETER("neuron.adaptation.parameter");
	
	private final String name;
	
	private PropertyEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
