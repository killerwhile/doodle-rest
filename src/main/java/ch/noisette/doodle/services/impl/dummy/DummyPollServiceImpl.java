package ch.noisette.doodle.services.impl.dummy;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.stereotype.Service;

import ch.noisette.doodle.domains.Poll;
import ch.noisette.doodle.domains.Subscriber;
import ch.noisette.doodle.services.PollService;

@Service
public class DummyPollServiceImpl implements PollService {

	private static final Logger logger_c = Logger
			.getLogger(DummyPollServiceImpl.class);

	private final int DB_PORT = 9160;
	private final String DB_HOST = "localhost";
	private final String DB_KEYSPACE = "doodle";
	private final String UTF8 = "UTF-8";
	private TTransport tr;
	private Cassandra.Client client;

	private void openDBconnection() {

		tr = new TFramedTransport(new TSocket(DB_HOST, DB_PORT));
		TProtocol protocol = new TBinaryProtocol(tr);
		client = new Cassandra.Client(protocol);
		try {
			tr.open();
		} catch (TTransportException e) {
			e.printStackTrace();
		}

	}

	private void setKeySpace(String keyspace) {

		try {
			client.set_keyspace(keyspace);
		} catch (InvalidRequestException e) {
			System.out.println("Keyspace does not exist!");
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}

	}

	private void closeDBconnection() {
		tr.close();
	}

	@Override
	public Poll getPollById(String pollId) {

		return new Poll();
	}

	@Override
	public List<Poll> getAllPolls() {
		// TODO Auto-generated method stub
		return Collections.<Poll> emptyList();
	}

	@Override
	public Poll createPoll(Poll poll) {

		String pollID = UUID.randomUUID().toString();

		System.out.println(pollID);

		poll.setId(pollID);

		openDBconnection();
		setKeySpace(DB_KEYSPACE);

		try {

			ColumnParent cp = new ColumnParent("poll_attributes");
			long timestamp = System.currentTimeMillis();

			Column cLabel = new Column();
			cLabel.setName("label".getBytes(UTF8));
			cLabel.setValue(poll.getLabel().getBytes(UTF8));
			cLabel.setTimestamp(timestamp);
			client.insert(ByteBuffer.wrap(pollID.getBytes(UTF8)), cp, cLabel,
					ConsistencyLevel.QUORUM);

			Column cEmail = new Column();
			cEmail.setName("email".getBytes(UTF8));
			cEmail.setValue(poll.getEmail().getBytes(UTF8));
			cEmail.setTimestamp(timestamp);
			client.insert(ByteBuffer.wrap(pollID.getBytes(UTF8)), cp, cEmail,
					ConsistencyLevel.QUORUM);

			Column cMaxChoices = new Column();
			cMaxChoices.setName("max-choices".getBytes(UTF8));
			cMaxChoices
					.setValue(poll.getMaxChoices().toString().getBytes(UTF8));
			cMaxChoices.setTimestamp(timestamp);
			client.insert(ByteBuffer.wrap(pollID.getBytes(UTF8)), cp,
					cMaxChoices, ConsistencyLevel.QUORUM);

			Column cChoices = new Column();
			cChoices.setName("choices".getBytes(UTF8));
			cChoices.setValue(poll.getChoices().toString().getBytes(UTF8));
			cChoices.setTimestamp(timestamp);
			client.insert(ByteBuffer.wrap(pollID.getBytes(UTF8)), cp, cChoices,
					ConsistencyLevel.QUORUM);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (InvalidRequestException e) {
			e.printStackTrace();
		} catch (UnavailableException e) {
			e.printStackTrace();
		} catch (TimedOutException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}

		closeDBconnection();

		return poll;
	}

	@Override
	public Poll addSubscriber(String pollId, Subscriber subscriber) {

		openDBconnection();
		setKeySpace(DB_KEYSPACE);

		try {

			ColumnParent cp = new ColumnParent("poll_attributes");
			long timestamp = System.currentTimeMillis();

			Column cLabel = new Column();
			cLabel.setName("label".getBytes(UTF8));
			cLabel.setValue(subscriber.getLabel().getBytes(UTF8));
			cLabel.setTimestamp(timestamp);
			client.insert(ByteBuffer.wrap(pollId.getBytes(UTF8)), cp, cLabel,
					ConsistencyLevel.QUORUM);

			Column cChoices = new Column();
			cChoices.setName("choices".getBytes(UTF8));
			cChoices.setValue(subscriber.getChoices().toString().getBytes(UTF8));
			cChoices.setTimestamp(timestamp);
			client.insert(ByteBuffer.wrap(pollId.getBytes(UTF8)), cp, cChoices,
					ConsistencyLevel.QUORUM);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (InvalidRequestException e) {
			e.printStackTrace();
		} catch (UnavailableException e) {
			e.printStackTrace();
		} catch (TimedOutException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}

		closeDBconnection();

		Poll poll = new Poll();
		poll.setChoices(subscriber.getChoices());
		poll.setLabel(subscriber.getLabel());
		poll.setId(pollId);

		return poll;
	}

	@Override
	public void deletePoll(String pollId) {
		// TODO Auto-generated method stub

	}

}
