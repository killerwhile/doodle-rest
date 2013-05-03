package ch.noisette.doodle.services.impl.dummy;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import ch.noisette.doodle.domains.Poll;
import ch.noisette.doodle.domains.Subscriber;
import ch.noisette.doodle.services.PollService;

@Service
public class PollServiceImpl implements PollService {

	private static final Logger logger_c = Logger.getLogger(PollServiceImpl.class);

	@Override
	public Poll getPollById(String pollId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Poll> getAllPolls() {
		// TODO Auto-generated method stub
		return Collections.<Poll>emptyList();
	}

	@Override
	public Poll createPoll(Poll poll) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Poll addSubscriber(String pollId, Subscriber subscriber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deletePoll(String pollId) {
		// TODO Auto-generated method stub

	}



}
