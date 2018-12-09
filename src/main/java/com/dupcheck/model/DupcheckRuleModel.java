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
@Table(name = "DupcheckRuleModel")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@XmlRootElement
public class DupcheckRuleModel {
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private long dupcheckRuleId;

	@Column
	private String transactionType;

	@Column
	private String transactionSubType;

	@Column
	private Integer dupcheckType;

	@Column
	private String fieldName;

	@Column
	private String createdOn;

	@Column
	private String updatedOn;
}
