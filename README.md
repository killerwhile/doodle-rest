Doodle-like REST API backed by Cassandra, used for Fribourg's University Advanced Databases System Course
----

# Homework

The goal of the exercice is to build a REST API that will be able to create a poll, subscribe to the poll and retrieve the subsciptions.

In REST notation:

## Create a new poll

    POST /rest/polls
    { "label": "Afterwork", "choices": [ "Monday", "Tuesday", "Friday" ], "email": "benoit@noisette.ch" }

Returns ``pollId`` (in Location header)

## Subscribe to the poll

	PUT /rest/polls/<pollId>
	{ "name": "Benoit", "choices": [ "Monday", "Friday" ] }

Returns the updated poll, JSON encoded (see below)

	GET /rest/polls/<pollId>

Returns the poll, JSON encoded

	{ "poll": { "label": "Afterwork", "choices": [ "Monday", "Tuesday", "Friday" ], "email": "benoit@noisette.ch", "subscribers": [ { "name": "Benoit", "choices": [ "Monday", "Friday" ] }, ... ] } }

# Proposed Data Model

## Keyspace

	create keyspace doodle 
		with placement_strategy = 'org.apache.cassandra.locator.SimpleStrategy' 
		and  strategy_options = {replication_factor:1};

## Colum Families

### polls
	create column family polls 
		with key_validation_class = UUIDType 
		and comparator = AsciiType
		and default_validation_class = UTF8Type
		and column_metadata = [
			{column_name: label, validation_class: UTF8Type}
			{column_name: email, validation_class: UTF8Type}
			{column_name: choices, validation_class: UTF8Type}
		];

	set polls[timeuuid()]['choices'] = '[ "Monday", "Tuesday", "Friday" ]';
	set polls['0488b290-c153-11e2-b652-c56eefd2b5e3']['label'] = 'Afterwork';
	set polls['0488b290-c153-11e2-b652-c56eefd2b5e3']['email'] = 'benoit@noisette.ch';

- ``choices`` is a JSON encoded list of strings.
- Polls are not intended to change often so they are put in a dedicated CF where for instance row caching could be turned on.

### subscribers

	create column family subscribers
		with key_validation_class = UUIDType
		and comparator = 'CompositeType(TimeUUIDType,UTF8Type)'
		and default_validation_class = UTF8Type;

	set subscribers['0488b290-c153-11e2-b652-c56eefd2b5e3']['709f7180-c153-11e2-b652-c56eefd2b5e3:Benoit'] = '[ "Monday", "Friday" ]';

- The row key is the same UUID than the poll's row key.
- The column name is a TimeUUID to order the subscription, concatenated with a UTF8 string which is the name of the subscriber
- The value is a JSON encoded list of strings representing the subscribed ``choices``. Note that if the poll's administateur change the ``choices``, either all the values need to be updated, or (btter) the removed choices are skipped at runtime.

# Implementation

The current implementation use Hector client as high level connection to Cassandra.

