package ch.noisette.doodle.domains;

import java.util.List;

public class Poll {

	private String id;
	private String label;
	private List<String> choices;
	private String email;
	private Integer maxChoices;
	private List<Subscriber> subscribers;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getMaxChoices() {
		return maxChoices;
	}

	public void setMaxChoices(Integer maxChoices) {
		this.maxChoices = maxChoices;
	}

	public List<Subscriber> getSubscribers() {
		return subscribers;
	}

	public void setSubscribers(List<Subscriber> subscribers) {
		this.subscribers = subscribers;
	}

	@Override
	public String toString() {
		return "Poll [id=" + id + ", label="
				+ label + ", choices=" + choices + ", email="
				+ email + ", subscribers=" + subscribers + "]";
	}
}
