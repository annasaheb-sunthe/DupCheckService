package com.dupcheck.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DupckeckRuleDto {
	
	private Integer dupcheckRuleId;
	
	private String transactionType;
	
	private String transactionSubType;
	
	private String dupcheckType;
	
	private String fieldName;
	
	private Date createOn;
	
	private Date updatedOn;

}
