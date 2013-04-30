package ch.noisette.doodle.services;

import java.util.List;

import ch.noisette.doodle.domains.Poll;
import ch.noisette.doodle.domains.Subscriber;

public interface PollService {

	public Poll getPollById(String pollId);

	public List<Poll> getAllPolls();

	public Poll createPoll(Poll poll);

	public Poll addSubscriber(String pollId, Subscriber subscriber);

	public void deletePoll(String pollId);
	
}
