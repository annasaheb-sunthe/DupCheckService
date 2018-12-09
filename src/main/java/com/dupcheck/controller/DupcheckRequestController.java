package com.dupcheck.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dupcheck.model.DupcheckRuleModel;
import com.dupcheck.model.RequestData;
import com.dupcheck.model.ResponseMessage;
import com.dupcheck.service.DupcheckService;
import com.dupcheck.utils.ReceiverConstants;

import lombok.extern.log4j.Log4j2;

@Component
@RestController
@Log4j2
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(ReceiverConstants.DUPCHECK_URL)
public class DupcheckRequestController {
	@Autowired
	private DupcheckService dupcheckService;

	@RequestMapping(value = ReceiverConstants.DUPCHECK_REQUEST_URL, method = RequestMethod.POST, produces = { "application/json", "application/xml" })
	public ResponseEntity<ResponseMessage> checkDuplicateRequest(@RequestBody RequestData requestData) {
		log.info("RequestData - Transaction type : " + requestData.getTransactionType() 
		+ ", Transaction sub type : " + requestData.getTransactionSubType() + ", payload format: " + requestData.getPayloadFormat());

		ResponseMessage responseMessage = dupcheckService.checkDuplicateRequest(requestData);
		
//		ResponseEntity<ResponseMessage> re = null; 
//		if (responseMessage.getResponseCode() == 200) {
//			re = new ResponseEntity<ResponseMessage>(responseMessage, HttpStatus.OK);
//		} else {
//			re = new ResponseEntity<ResponseMessage>(responseMessage, HttpStatus.CONFLICT);
//		}
		
		return new ResponseEntity<ResponseMessage>(responseMessage, HttpStatus.OK);
	}
	
	@RequestMapping(value = ReceiverConstants.ADD_DUPCHECK_RULE_URL, method = RequestMethod.POST, produces = {"application/xml", "application/json" })
	public ResponseEntity<DupcheckRuleModel> addDupcheckRule(@RequestHeader Map<String, String> requestMap,
			@RequestBody DupcheckRuleModel dupcheckRuleModel) {
		log.info("Received RequestHeader : " + requestMap);
		log.info("Received DupcheckRuleModel : " + dupcheckRuleModel);

		DupcheckRuleModel dupcheckRuleResult = dupcheckService.addDupcheckRule(dupcheckRuleModel);

		if (dupcheckRuleResult == null) {
			log.info("Data not able to add into DB - DupcheckRuleModel : " + dupcheckRuleResult);
			return new ResponseEntity<DupcheckRuleModel>(dupcheckRuleResult, HttpStatus.CONFLICT);
		}

		log.info("Data added into DB - DupcheckRuleModel : " + dupcheckRuleResult);
		return new ResponseEntity<DupcheckRuleModel>(dupcheckRuleResult, HttpStatus.OK);
	}

	// @RequestMapping(value = ReceiverConstants.MODIFY_DUPCHECK_RULE_URL, method =
	// RequestMethod.POST, produces = {"application/xml", "application/json" })
	@PutMapping("/modifyDupcheckRule")
	public ResponseEntity<ResponseMessage> modifyDupcheckRule(@RequestHeader Map<String, String> requestMap,
			@RequestBody DupcheckRuleModel dupcheckRuleModel) {
		log.info("Received RequestHeader : " + requestMap);
		log.info("Received DupcheckRuleModel : " + dupcheckRuleModel);

		ResponseMessage rm = dupcheckService.modifyDupcheckRule(dupcheckRuleModel);

		if (rm.getResponseCode() != 200) {
			log.info("Data not able to udpate into DB - ResponseMessage : " + rm);
			return new ResponseEntity<ResponseMessage>(rm, HttpStatus.CONFLICT);
		}

		log.info("Data updated into DB - ResponseMessage : " + rm);
		return new ResponseEntity<ResponseMessage>(rm, HttpStatus.OK);
	}

	@RequestMapping(value = ReceiverConstants.GET_DUPCHECK_RULE_BY_TYPE_URL, method = RequestMethod.GET, produces = {
			"application/xml", "application/json" })
	public ResponseEntity<List<DupcheckRuleModel>> getDupcheckRuleByType(@RequestHeader Map<String, String> requestMap,
			@RequestBody DupcheckRuleModel dupcheckRuleModel) {
		log.info("Received RequestHeader : " + requestMap);
		log.info("Request dupcheckRuleModel: " + dupcheckRuleModel);
		List<DupcheckRuleModel> dupcheckRuleList = dupcheckService.getDupcheckRuleByType(dupcheckRuleModel);
		log.info("DupcheckRules recieved from db : " + dupcheckRuleList);
		return new ResponseEntity<List<DupcheckRuleModel>>(dupcheckRuleList, HttpStatus.OK);
	}

	@RequestMapping(value = ReceiverConstants.GET_ALL_DUPCHECK_RULES_URL, method = RequestMethod.GET, produces = {
			"application/xml", "application/json" })
	public ResponseEntity<List<DupcheckRuleModel>> getAllDupcheckRules(@RequestHeader Map<String, String> requestMap) {
		log.info("Received RequestHeader : " + requestMap);
		List<DupcheckRuleModel> list = dupcheckService.getAllDupcheckRules();
		log.info("Dup check rules received from DB : " + list);
		return new ResponseEntity<List<DupcheckRuleModel>>(list, HttpStatus.OK);
	}

	@RequestMapping(value = ReceiverConstants.GET_DUPCHECK_RULE_BY_ID_URL, method = RequestMethod.GET, produces = {
			"application/xml", "application/json" })
	public ResponseEntity<DupcheckRuleModel> getMetadataById(@RequestHeader Map<String, String> requestMap,
			@PathVariable("dupcheckRuleId") long dupcheckRuleId) {
		log.info("RequestHeader received " + requestMap);
		log.info("Request Body : " + dupcheckRuleId);
		DupcheckRuleModel dupcheckRule = dupcheckService.getDupcheckRuleById(dupcheckRuleId);
		log.info("Dup check rules received from DB : " + dupcheckRule);
		return new ResponseEntity<DupcheckRuleModel>(dupcheckRule, HttpStatus.OK);
	}
}