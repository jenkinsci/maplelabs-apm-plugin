package io.jenkins.plugins.Events.interfaces;

public interface IEvent {

	public static enum AlertType {
		INFO,
		ERROR,
		WARNING,
		SUCCESS;
	}

	public static enum Priority {
		LOW,
		HIGH,
		NORMAL;
	}

	public Long getDate();

	public String getText();

	public String getHost();

	public String getTitle();

	public String getNodeName();

	public String getEventType();

	public Priority getPriority();

	public AlertType getAlertType();
}
