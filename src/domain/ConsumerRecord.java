package domain;

// Domain Class
// Defines our concept of a ConsumerRecord
public class ConsumerRecord {
	private org.apache.kafka.clients.consumer.ConsumerRecord<Integer, String> record;
	
	public ConsumerRecord(org.apache.kafka.clients.consumer.ConsumerRecord<Integer, String> record) {
		this.record = record;
	}
	
	public org.apache.kafka.clients.consumer.ConsumerRecord<Integer, String> getRecord() {
		return record;
	}
	public void setRecord(org.apache.kafka.clients.consumer.ConsumerRecord<Integer, String> record) {
		this.record = record;
	}
}
