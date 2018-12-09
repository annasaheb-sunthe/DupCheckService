package com.dupcheck.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dupcheck.model.DupcheckRuleModel;
import com.dupcheck.model.RequestData;
import com.dupcheck.model.ResponseMessage;


@Service
public interface DupcheckService {
	
	public ResponseMessage checkDuplicateRequest(RequestData requestData);
	
	public DupcheckRuleModel addDupcheckRule(DupcheckRuleModel dupcheckRuleModel);
	
	public ResponseMessage modifyDupcheckRule(DupcheckRuleModel dupcheckRuleModel);
		
	public List<DupcheckRuleModel> getDupcheckRuleByType(DupcheckRuleModel dupcheckRuleModel);

	public List<DupcheckRuleModel> getAllDupcheckRules();
	
	public DupcheckRuleModel getDupcheckRuleById(long dupcheckRuleId);
}
