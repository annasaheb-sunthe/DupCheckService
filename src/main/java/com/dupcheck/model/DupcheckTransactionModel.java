package com.dupcheck.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "DupcheckTransactionModel")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@XmlRootElement
public class DupcheckTransactionModel {
	@Column
	@Id
	private long transactionId;

	@Column
	private String transactionType;

	@Column
	private String transactionSubType;

	@Column(length=10000)
	private String fieldValue;

	@Column
	private String createdOn;

	@Column
	private String updatedOn;
}
