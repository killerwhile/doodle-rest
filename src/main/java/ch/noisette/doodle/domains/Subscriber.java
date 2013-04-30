package ch.noisette.doodle.domains;

import java.util.List;

public class Subscriber {

	private String label;
	private List<String> choices;
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public List<String> getChoices() {
		return choices;
	}
	
	public void setChoices(List<String> choices) {
		this.choices = choices;
	}
	
	@Override
	public String toString() {
		return "Subscriber [label=" + label + ", choices=" + choices + "]";
	}
}
