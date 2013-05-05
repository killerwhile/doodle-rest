package ch.noisette.doodle.services.impl.dummy;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
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

	private final static int DB_PORT = 9160;
	private final static String DB_HOST = "localhost";
	private final static String DB_KEYSPACE = "doodle";
	private final static String UTF8 = "UTF-8";

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

		openDBconnection();
		setKeySpace(DB_KEYSPACE);

		Poll foundPoll = new Poll();
		foundPoll.setId(pollId);

		try {

			ColumnParent cp1 = new ColumnParent("poll_attributes");
			SlicePredicate predicate1 = new SlicePredicate();
			SliceRange sr1 = new SliceRange(toByteBuffer(""), toByteBuffer(""),
					false, 10);
			predicate1.setSlice_range(sr1);
			List<ColumnOrSuperColumn> attributes = client.get_slice(
					toByteBuffer(pollId), cp1, predicate1,
					ConsistencyLevel.QUORUM);

			for (ColumnOrSuperColumn c : attributes) {

				String name = toString(c.column.name);
				String value = toString(c.column.value);

				if (name.equals("label")) {
					foundPoll.setLabel(value);
				} else if (name.equals("email")) {
					foundPoll.setEmail(value);
				} else if (name.equals("max-choices")) {
					foundPoll.setMaxChoices(Integer.parseInt(value));
				} else if (name.equals("choices")) {
					foundPoll.setChoices(stringToStringList(value));
				}

			}

			// Set Subscribers
			List<Subscriber> subsList = new ArrayList<Subscriber>();
			ColumnParent cp = new ColumnParent("poll_subscribers");
			SlicePredicate predicate = new SlicePredicate();
			SliceRange sr = new SliceRange(toByteBuffer(""), toByteBuffer(""),
					false, Integer.MAX_VALUE);
			predicate.setSlice_range(sr);
			List<ColumnOrSuperColumn> subscribers = client.get_slice(
					toByteBuffer(pollId), cp, predicate,
					ConsistencyLevel.QUORUM);
			for (ColumnOrSuperColumn c : subscribers) {

				Subscriber s = new Subscriber();
				s.setLabel(toString(c.column.name));
				s.setChoices(stringToStringList(toString(c.column.value)));
				subsList.add(s);

			}
			foundPoll.setSubscribers(subsList);

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

		return foundPoll;

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
			client.insert(toByteBuffer(pollID), cp, cLabel,

			ConsistencyLevel.QUORUM);

			Column cEmail = new Column();
			cEmail.setName("email".getBytes(UTF8));
			cEmail.setValue(poll.getEmail().getBytes(UTF8));
			cEmail.setTimestamp(timestamp);
			client.insert(toByteBuffer(pollID), cp, cEmail,

			ConsistencyLevel.QUORUM);

			Column cMaxChoices = new Column();
			cMaxChoices.setName("max-choices".getBytes(UTF8));
			cMaxChoices
					.setValue(poll.getMaxChoices().toString().getBytes(UTF8));
			cMaxChoices.setTimestamp(timestamp);
			client.insert(toByteBuffer(pollID), cp, cMaxChoices,
					ConsistencyLevel.QUORUM);

			Column cChoices = new Column();
			cChoices.setName("choices".getBytes(UTF8));
			cChoices.setValue(poll.getChoices().toString().getBytes(UTF8));
			cChoices.setTimestamp(timestamp);
			client.insert(toByteBuffer(pollID), cp, cChoices,

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

			ColumnParent cp = new ColumnParent("poll_subscribers");
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

	public static ByteBuffer toByteBuffer(String value)
			throws UnsupportedEncodingException {
		return ByteBuffer.wrap(value.getBytes(UTF8));
	}

	public static String toString(ByteBuffer buffer)
			throws UnsupportedEncodingException {
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		return new String(bytes, UTF8);
	}

	private List<String> stringToStringList(String s) {

		List<String> list = new ArrayList<String>();

		StringTokenizer st = new StringTokenizer(s, "[ ],");
		while (st.hasMoreTokens())
			list.add(st.nextToken());

		return list;
	}

}
