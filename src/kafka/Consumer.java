package kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import configuration.Configuration;
import log4j.LogManager;

public class Consumer extends Thread{
	private KafkaConsumer<Integer,String> consumer;
	private Properties config;
	public static List<domain.ConsumerRecord> emailList = new ArrayList<domain.ConsumerRecord>();

	public Consumer(Properties config) {
		this.config = config;
	}

	@Override
	public void run() {

		// Initializes Kafka consumer
		// Set this consumer to listen to a specified topic
		LogManager.writeInLog(LogManager.TypeLog4j.INFO, "Consumer started.");
		consumer = new KafkaConsumer<>(config);
		consumer.subscribe(Collections.singletonList(Configuration.instance.getKAFKA_TOPIC()));

		// Endless loop to keep application listening
		while (true) {

			// Try to get unread messages
			org.apache.kafka.clients.consumer.ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofMillis(500));

			// If there is no unread messages, we do nothing until the next loop
			if (records.count() < 1) LogManager.writeInLog(LogManager.TypeLog4j.INFO, "There is no messages left to read from Topic.");

			// Iterates through unread messages
			// Adds them to our 'queue' for eventually sending them as emails
			for (org.apache.kafka.clients.consumer.ConsumerRecord<Integer, String> record : records) {
				LogManager.writeInLog(LogManager.TypeLog4j.DEBUG, "Message appended to Collection: ('"+record.key() + "', '"+record.value()+"') at offset "+record.offset());
				Consumer.emailList.add(new domain.ConsumerRecord(record));

				// Sets ACK on received messages
				consumer.commitAsync();
			}

			try {
				// Introduces some delay to prevent overflow
				Thread.sleep(TimeUnit.SECONDS.toMillis(
						Configuration.instance.getCONSUMER_DELAY_SECONDS()
						));
			} catch (Exception e) {}
		}
	}
}
