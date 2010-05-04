package de.dvdb.domain.model.message;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import de.dvdb.domain.model.user.User;

@Entity
@javax.persistence.Table(name = "dvdb2_textmessage")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "messagetype", discriminatorType = DiscriminatorType.INTEGER)
@DiscriminatorValue("1")
public class TextMessage implements Serializable {

	private static final long serialVersionUID = 3459628813972344925L;

	private Long id;

	private User recipient;

	private User sender;

	private Date messageDate;

	private TextMessage reference;

	private String subject;

	private String anonymousEmail;

	private String body;

	private Boolean messageRead = false;

	private Boolean highPrio = false;

	@javax.persistence.Id
	@javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@NotNull
	@Length(max = 10000)
	@Column(length = 10000)
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@NotNull
	@Length(min = 1, max = 255)
	@Column(length = 255)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getMessageDate() {
		return messageDate;
	}

	@Column(name = "messagedate")
	public void setMessageDate(Date messageDate) {
		this.messageDate = messageDate;
	}

	@javax.persistence.ManyToOne(optional = false)
	@javax.persistence.JoinColumn(name = "user_id_recipient")
	public User getRecipient() {
		return recipient;
	}

	public void setRecipient(User recipient) {
		this.recipient = recipient;
	}

	@javax.persistence.ManyToOne(optional = true)
	@javax.persistence.JoinColumn(name = "user_id_sender", nullable = true)
	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public TextMessage getReference() {
		return reference;
	}

	@javax.persistence.ManyToOne(optional = true)
	@javax.persistence.JoinColumn(name = "textmessage_id_reference", nullable = true)
	public void setReference(TextMessage reference) {
		this.reference = reference;
	}

	@Column(name = "anonymousemail", length = 255)
	public String getAnonymousEmail() {
		return anonymousEmail;
	}

	public void setAnonymousEmail(String anonymousEmail) {
		this.anonymousEmail = anonymousEmail;
	}

	@Column(name = "messageread")
	public Boolean getMessageRead() {
		return messageRead;
	}

	public void setMessageRead(Boolean messageRead) {
		this.messageRead = messageRead;
	}

	@Column(name = "highprio")
	public Boolean getHighPrio() {
		return highPrio;
	}

	public void setHighPrio(Boolean highPrio) {
		this.highPrio = highPrio;
	}

}
