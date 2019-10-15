package main;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import configuration.Configuration;
import kafka.Consumer;
import log4j.LogManager;
import smtp.EmailManager;

public class Main {

	public static void main(String[] args) {
		LogManager.writeInLog(LogManager.TypeLog4j.INFO, "KREB-J instance started.");
		
		// Initializes global variables from application properties file
		new Configuration();
		
		// Sets Kafka configuration properties
		Properties config = new Properties();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Configuration.instance.getKAFKA_BOOTSTRAP_SERVERS_CONFIG());
		config.put(ConsumerConfig.CLIENT_ID_CONFIG, Configuration.instance.getKAFKA_CLIENT_ID_CONFIG());
		config.put(ConsumerConfig.GROUP_ID_CONFIG, Configuration.instance.getKAFKA_GROUP_ID_CONFIG());
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		
		// Starts consumer instance to look for Kafka messages
		Consumer consumer = new Consumer(config);
		consumer.start();
		
		// Starts email manager instance to process received Kafka messages
		EmailManager emailManager = new EmailManager();
		emailManager.start();
	}
}
