package configuration;

import java.io.InputStream;
import java.util.Properties;

import log4j.LogManager;
import log4j.LogManager.TypeLog4j;

public class Configuration {
	private String KAFKA_BOOTSTRAP_SERVERS_CONFIG;
	private String KAFKA_CLIENT_ID_CONFIG;
	private String KAFKA_GROUP_ID_CONFIG;
	private String KAFKA_TOPIC;
	
	private String SMTP_HOSTNAME;
	private int SMTP_PORT;
	private String SMTP_FROM;
	private String SMTP_PSSWD;
	
	private int SMTP_MAX_EMAILS_PER_MINUTE;
	private int CONSUMER_DELAY_SECONDS;
	
	public static Configuration instance;
	
	public Configuration() {
		
		// Singleton pattern
		if (instance != null) return; 
		else Configuration.instance = this;
		
		setConfigurationProperties();
	}
	
	private void setConfigurationProperties() {
		LogManager.writeInLog(TypeLog4j.INFO , "Reading values from application.properties ...");
		try (InputStream input = main.Main.class.getClassLoader().getResourceAsStream("application.properties")) {
			Properties props = new Properties();
			props.load(input);
			
			setProperties(props, new String[] {
					"KAFKA.BOOTSTRAP_SERVERS_CONFIG",
					"KAFKA.CLIENT_ID_CONFIG",
					"KAFKA.GROUP_ID_CONFIG",
					"KAFKA.TOPIC",
					
					"SMTP.HOSTNAME",
					"SMTP.PORT",
					"SMTP.FROM",
					"SMTP.PSSWD",
					
					"SMTP.MAX_EMAILS_PER_MINUTE",
					"CONSUMER.DELAY_SECONDS"
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setProperties(Properties props, String[] key) {
		this.KAFKA_BOOTSTRAP_SERVERS_CONFIG = getProperty(props, key[0]);
		this.KAFKA_CLIENT_ID_CONFIG = getProperty(props, key[1]);
		this.KAFKA_GROUP_ID_CONFIG = getProperty(props, key[2]);
		this.KAFKA_TOPIC = getProperty(props, key[3]);
		
		this.SMTP_HOSTNAME = getProperty(props, key[4]);
		this.SMTP_PORT = Integer.parseInt(getProperty(props, key[5]));
		this.SMTP_FROM = getProperty(props, key[6]);
		this.SMTP_PSSWD = getProperty(props, key[7]);
		
		this.SMTP_MAX_EMAILS_PER_MINUTE = Integer.parseInt(getProperty(props, key[8]));
		this.CONSUMER_DELAY_SECONDS = Integer.parseInt(getProperty(props, key[9]));
	}
	
	private String getProperty(Properties props, String key) {
		return props.getProperty(key);
	}

	public String getKAFKA_BOOTSTRAP_SERVERS_CONFIG() {
		return KAFKA_BOOTSTRAP_SERVERS_CONFIG;
	}

	public String getKAFKA_CLIENT_ID_CONFIG() {
		return KAFKA_CLIENT_ID_CONFIG;
	}

	public String getKAFKA_GROUP_ID_CONFIG() {
		return KAFKA_GROUP_ID_CONFIG;
	}

	public String getKAFKA_TOPIC() {
		return KAFKA_TOPIC;
	}

	public String getSMTP_HOSTNAME() {
		return SMTP_HOSTNAME;
	}

	public int getSMTP_PORT() {
		return SMTP_PORT;
	}

	public String getSMTP_FROM() {
		return SMTP_FROM;
	}

	public String getSMTP_PSSWD() {
		return SMTP_PSSWD;
	}

	public int getSMTP_MAX_EMAILS_PER_MINUTE() {
		return SMTP_MAX_EMAILS_PER_MINUTE;
	}

	public int getCONSUMER_DELAY_SECONDS() {
		return CONSUMER_DELAY_SECONDS;
	}
	
	
}
