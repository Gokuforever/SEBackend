package com.sorted.commons.helper;

import com.sorted.commons.enums.MailTemplate;

import java.util.List;

public class MailBuilder {

	private List<String> to;
	private List<String> cc;
	private List<String> bcc;
	private String subject;
	private String content;
	private MailTemplate template;

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public void setTo(String... to) {
		this.to = List.of(to);
	}

	public List<String> getCc() {
		return cc;
	}

	public void setCc(List<String> cc) {
		this.cc = cc;
	}

	public void setCc(String... cc) {
		this.cc = List.of(cc);
	}

	public List<String> getBcc() {
		return bcc;
	}

	public void setBcc(List<String> bcc) {
		this.bcc = bcc;
	}

	public void setBcc(String... bcc) {
		this.bcc = List.of(bcc);
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setTemplate(MailTemplate template) {
		this.template = template;
	}

	public MailTemplate getTemplate() {
		return this.template;
	}

}
