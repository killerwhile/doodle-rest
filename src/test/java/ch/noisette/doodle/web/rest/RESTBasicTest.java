package ch.noisette.doodle.web.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import ch.noisette.doodle.domains.Poll;

public class RESTBasicTest {

	@Test
	public void createPollTest() throws IOException {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://localhost:8080/doodle-rest/rest/polls/");
		post.setHeader("Content-type", "application/json;charset=UTF8");

		Poll poll = new Poll();
		// poll.setId( null ); id is returned by the REST API, not set manually.
		poll.setEmail("email@address.com");

		poll.setLabel("Afterwork");
		poll.setMaxChoices(1);

		@SuppressWarnings("serial")
		List<String> choices = new ArrayList<String>() {
			{
				add("Monday");
				add("Tuesday");
				add("Friday");
			}
		};
		poll.setChoices(choices);

		// JSON serialization.
		ObjectMapper mapper = new ObjectMapper();
		String jsonPoll = mapper.writeValueAsString(poll);

		// POST Request payload it only text.
		post.setEntity(new StringEntity(jsonPoll, "UTF8"));

		HttpResponse response = client.execute(post);
		String pollId = response.getAllHeaders()[1].toString().substring(10);
		Assert.assertEquals(HttpStatus.CREATED.value(), response
				.getStatusLine().getStatusCode());
		Assert.assertTrue(response.containsHeader("Location"));

	}

	@Test
	public void deletePollTest() throws IOException {

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://localhost:8080/doodle-rest/rest/polls/");
		post.setHeader("Content-type", "application/json;charset=UTF8");

		Poll poll = new Poll();
		// poll.setId( null ); id is returned by the REST API, not set manually.
		poll.setEmail("email@address.com");

		poll.setLabel("Afterwork");
		poll.setMaxChoices(1);

		@SuppressWarnings("serial")
		List<String> choices = new ArrayList<String>() {
			{
				add("Monday");
				add("Tuesday");
				add("Friday");
			}
		};
		poll.setChoices(choices);

		// JSON serialization.
		ObjectMapper mapper = new ObjectMapper();
		String jsonPoll = mapper.writeValueAsString(poll);

		// POST Request payload it only text.
		post.setEntity(new StringEntity(jsonPoll, "UTF8"));

		HttpResponse response = client.execute(post);
		String pollId = response.getAllHeaders()[1].toString().substring(34);

		HttpClient client1 = new DefaultHttpClient();
		HttpDelete delete = new HttpDelete(
				"http://localhost:8080/doodle-rest/rest/polls/" + pollId);

		HttpResponse response1 = client1.execute(delete);

		Assert.assertEquals(HttpStatus.OK.value(), response1.getStatusLine()
				.getStatusCode());

	}

}
