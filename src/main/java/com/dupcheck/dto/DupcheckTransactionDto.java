package com.dupcheck.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DupcheckTransactionDto {
	
	private Integer transactionId;
	
	private String transactionType;
	
	private String transactionSubType;
	
	private String fieldValue;
	
	private Date createOn;
	
	private Date updatedOn;

}
