package smtp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;

import configuration.Configuration;
import kafka.Consumer;
import log4j.LogManager;
import log4j.LogManager.TypeLog4j;

public class EmailManager extends Thread{
	
	// Configuration variables
	private int MAX_EMAILS_PER_MINUTE;
	private String HOSTNAME;
	private int PORT;
	private String FROM;
	private String PSSWD;

	private domain.ConsumerRecord item;
	private JSONObject record;

	public EmailManager() {doConfig();}
	public EmailManager(domain.ConsumerRecord item) {
		doConfig();
		this.item = item;
		this.setRecord(this.item.getRecord());
	}
	
	private void doConfig() {
		this.MAX_EMAILS_PER_MINUTE = Configuration.instance.getSMTP_MAX_EMAILS_PER_MINUTE();
		this.HOSTNAME = Configuration.instance.getSMTP_HOSTNAME();
		this.PORT = Configuration.instance.getSMTP_PORT();
		this.FROM = Configuration.instance.getSMTP_FROM();
		this.PSSWD = Configuration.instance.getSMTP_PSSWD();
	}

	// Main loop 
	// Looks for unread Kafka messages at our dedicated 'queue'
	public void start() {
		LogManager.writeInLog(LogManager.TypeLog4j.INFO, "Email Manager started.");
		while(true) {
			for (int i = 0; i < MAX_EMAILS_PER_MINUTE;) {
				domain.ConsumerRecord item = null;
				try {
					item = Consumer.emailList.remove(0);
				} catch (IndexOutOfBoundsException e) {}

				if (item != null) {
					// Found an unread messages waiting to be sent
					// Starts sending process as parallel thread
					i++;
					EmailManager thread = new EmailManager(item);
					thread.run();
				}
			}

			try {
				// Introduces some delay to prevent reaching technical limits on number of emails sent per minute
				// This fixes losting emails due to this techinal reason
				LogManager.writeInLog(LogManager.TypeLog4j.INFO, "Waiting a minute to get more emails from Kafka-topic ...");
				Thread.sleep(TimeUnit.MINUTES.toMillis(1));
			} catch (InterruptedException e) {}
		}
	}

	// Method called at start of a Thread instance
	@Override
	public void run() {
		LogManager.writeInLog(LogManager.TypeLog4j.INFO, "Processing email...");
		this.send();
	}

	// Handles email sending
	private void send() {
		HtmlEmail email = new HtmlEmail();
		email.setDebug(false);

		email.setHostName(HOSTNAME);
		email.setSmtpPort(PORT);
		email.setAuthenticator(new DefaultAuthenticator(FROM, PSSWD));
		email.setStartTLSEnabled(true);
		email.setSubject(record.getString("subject"));
		try {
			email.setFrom(FROM);
			this.setToEmailAddresses(email);
			this.setBccEmailAddresses(email);
			this.setHtmlBody(email);

			try {
				email.send();
				LogManager.writeInLog(LogManager.TypeLog4j.INFO, "Email sent.");
			} catch (Exception e) {
				Consumer.emailList.add(item);
				LogManager.writeInLog(TypeLog4j.ERROR, "Message could not be sent to recipients. Appending it again to current queue.");
			}
		} catch (EmailException e) {
			LogManager.writeInLog(TypeLog4j.ERROR, "Error handling email addresses. Check for the following information: { \n"
					+ " email.from ==> "+FROM+" \n"
					+ " email.to ==> "+record.getString("to")+" \n"
					+ " email.bcc ==> "+record.getString("bcc")+" \n"
					+ " email.htmlBody ==> "+record.getString("body")+" \n"
					+ "}");
			LogManager.writeInLog(TypeLog4j.FATAL, e.getMessage());
		}
	}

	// Used by the constructor
	// Parse Kafka record to JSON object
	// Sets this as instance variable 
	private void setRecord(ConsumerRecord<Integer, String> record) {
		this.record = new JSONObject(record.value());
	}

	// Encapsulates setting of to addresses
	private void setToEmailAddresses(HtmlEmail email) throws EmailException {
		String to = record.getString("to");
		if (!to.isEmpty()) {
			for (String currentTo : parseEmailAddresses(to)) {
				email.addTo(currentTo);
			}				
		}
	}

	// Encapsulates setting of bcc addresses
	private void setBccEmailAddresses(HtmlEmail email) throws EmailException {
		String bcc = record.getString("bcc");
		if (!bcc.isEmpty()) {
			for (String currentBcc : parseEmailAddresses(bcc)) {
				email.addBcc(currentBcc);
			}
		}
	}

	// Parses email addresses
	// Replaces quotes and handles if there is more than 1 email address
	// Returns parsed addresses as String Collection
	private static List<String> parseEmailAddresses(String addresses) {
		List<String> result = new ArrayList<String>();
		if (addresses.contains("["))	addresses = addresses.substring(1,addresses.length()-1);
		addresses = addresses.replaceAll("\\\'", "").replaceAll("\\\"", "");

		for (String email : addresses.split(",")) {
			result.add(email);
		}
		return result;
	}

	// Encapsulates setting of Html body
	private void setHtmlBody(HtmlEmail email) throws EmailException {
		String body = record.getString("body");
		email.setHtmlMsg(parseHTMLBody(body));
	}

	// Parses HTML body
	// Look for body out of redmine's generated html email
	// Returns final html body as String 
	private static String parseHTMLBody(String body) {
		body = body.substring(1, body.length()-1);
		int initPosition = body.indexOf("<html>");
		int endPosition = body.indexOf("</html>")+7;

		LogManager.writeInLog(LogManager.TypeLog4j.DEBUG, body.substring(initPosition, endPosition));
		return body.substring(initPosition, endPosition);
	}
}
