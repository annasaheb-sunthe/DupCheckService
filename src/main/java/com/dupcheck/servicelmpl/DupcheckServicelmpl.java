package com.dupcheck.servicelmpl;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.dupcheck.model.DupcheckRuleModel;
import com.dupcheck.model.DupcheckTransactionModel;
import com.dupcheck.model.RequestData;
import com.dupcheck.model.ResponseMessage;
import com.dupcheck.repository.DupcheckRuleRepository;
import com.dupcheck.repository.DupcheckTransactionRepository;
import com.dupcheck.service.DupcheckService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class DupcheckServicelmpl implements DupcheckService {
	@Autowired
	public DupcheckRuleRepository dupcheckRuleRepository;
	
	@Autowired
	public DupcheckTransactionRepository dupcheckTransactionRepository;
	
	@Override
	public ResponseMessage checkDuplicateRequest(RequestData requestData) {
		ResponseMessage responseMessage = new ResponseMessage();
		List<DupcheckRuleModel> rulesList = dupcheckRuleRepository.findByType(requestData.getTransactionType(),
				requestData.getTransactionSubType());

		// String correlationIdDupTable =
		// dupcheckTransactionRepository.findOne(correlationId);
		if (rulesList == null || rulesList.isEmpty()) {
			log.info("NO any Duplicate details available in database with given transaction details");
			responseMessage.setResponseCode(510);
			responseMessage.setResponseMessage(
					"No duplicate check rule available for transactionType :" + requestData.getTransactionType()
							+ " transactionSubType : " + requestData.getTransactionSubType());

		} else {
			log.info("rulesList size : " + rulesList.size());

			// log.info("XSD : \n" + metadataModel.getValidationSchema());
			// log.info("XSD file size: " + metadataModel.getValidationSchema().length());

			try {
				String payload = requestData.getPayload();

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(new InputSource(new StringReader(payload)));
				doc.getDocumentElement().normalize();
				log.info("No. of Nodes in the doc: " + doc.getChildNodes().getLength());
				XPath xPath = XPathFactory.newInstance().newXPath();
				
				DupcheckRuleModel dupcheckRuleModel = null;
				
				StringBuffer ruleValue = new StringBuffer();
				for (int i = 0; i < rulesList.size(); i++) {
					dupcheckRuleModel = rulesList.get(i);
					String ruleExp = dupcheckRuleModel.getFieldName();
					log.info("FieldValue [" + i + "] :" + dupcheckRuleModel.getFieldName());
					// extract message type
					NodeList ruleNodeList = (NodeList) xPath.compile(ruleExp).evaluate(doc, XPathConstants.NODESET);
					log.info("Rule Nodes: " + ruleNodeList.getLength());
					String value = null;

					if (ruleNodeList.getLength() > 0) {
						Node nNode = ruleNodeList.item(0);
						log.info("ruleExp - Current Element :" + nNode.getNodeName());

						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							Element eElement = (Element) nNode;
							log.info("RuleExp [" + i + "] TextContext : " + nNode.getTextContent());
							log.info("RuleExp [" + i + "] NodeValue: " + nNode.getNodeValue());
							value = nNode.getTextContent();
						}
					}
					
					ruleValue.append(value);
					
					if (i < (rulesList.size() - 1)) {
						ruleValue.append("^~");
					}
				}
				
				log.info("Request Data Dup Check Value : " + ruleValue.toString());
				
				List<DupcheckTransactionModel> transactionList = dupcheckTransactionRepository.findByFieldValue(requestData.getTransactionType(),
						requestData.getTransactionSubType(), ruleValue.toString());
				
				log.info("Is duplicate transaction exist? : " + (transactionList != null && transactionList.size() > 0));
				//check transaction exists 
				if(transactionList == null || transactionList.isEmpty()) {
					
					DupcheckTransactionModel transactionModel = dupcheckTransactionRepository.save(getDupCheckTransaction(requestData, ruleValue.toString()));
					responseMessage.setResponseCode(200);
					responseMessage.setResponseMessage("Transction ID : " + transactionModel.getTransactionId() + " is not duplicate.");
				} else {
					responseMessage.setResponseCode(400);
					responseMessage.setResponseMessage("Duplicate Check failed for transction ID : " + requestData.getTransactionID());
				}

			} catch (Exception e) {
				log.info("Exception: " + e.getMessage());
				responseMessage.setResponseCode(400);
				responseMessage.setResponseMessage("Duplicate Check failed!");
				e.printStackTrace();
			}
		}

		return responseMessage;
	}

	@Override
	public DupcheckRuleModel addDupcheckRule(DupcheckRuleModel dupcheckRuleModel) {
		log.info("DupcheckRule received: " + dupcheckRuleModel);
		dupcheckRuleModel.setDupcheckRuleId(getMetadataId());
		dupcheckRuleModel.setCreatedOn(getCurrentDateTime());
		dupcheckRuleModel.setUpdatedOn(getCurrentDateTime());

		DupcheckRuleModel dupcheckRuleVar = null;
		try {
			dupcheckRuleVar = (DupcheckRuleModel) dupcheckRuleRepository
					.findById(dupcheckRuleModel.getDupcheckRuleId());
		} catch (NoSuchElementException ex) {
			log.info("Error in finding DupcheckRule" + ex.getMessage());
		}

		if (dupcheckRuleVar != null) {
			// return false;
		} else {
			log.info("DupcheckRule deatils being saved in db");
			dupcheckRuleVar = dupcheckRuleRepository.save(dupcheckRuleModel);
			log.info("DupcheckRule saved in db");
		}

		return dupcheckRuleVar;
	}

	@Override
	public ResponseMessage modifyDupcheckRule(DupcheckRuleModel dupcheckRuleModel) {
		log.info("DupcheckRule received: " + dupcheckRuleModel);

		dupcheckRuleModel.setUpdatedOn(getCurrentDateTime());
		ResponseMessage rm = new ResponseMessage();
		int updateCount = 0;

		try {
			updateCount = dupcheckRuleRepository.updateById(dupcheckRuleModel.getTransactionType(),
					dupcheckRuleModel.getTransactionSubType(), dupcheckRuleModel.getDupcheckType(),
					dupcheckRuleModel.getFieldName(), dupcheckRuleModel.getUpdatedOn(),
					dupcheckRuleModel.getDupcheckRuleId());
			if (updateCount > 0) {
				rm.setResponseCode(200);
				rm.setResponseMessage("Record updated successfully.");
				log.info("DupcheckRule updated in db");
			} else {
				rm.setResponseCode(700);
				rm.setResponseMessage("Update failed. Record id did not match");
			}

		} catch (NoSuchElementException ex) {
			log.info("Error in finding DupcheckRule" + ex.getMessage());
			rm.setResponseCode(900);
			rm.setResponseMessage("Update failed. No Such Element Exception: " + ex.getMessage());
		}

		return rm;
	}

	@Override
	public List<DupcheckRuleModel> getDupcheckRuleByType(DupcheckRuleModel dupcheckRuleModel) {
		List<DupcheckRuleModel> obj = dupcheckRuleRepository.findByType(dupcheckRuleModel.getTransactionType(),
				dupcheckRuleModel.getTransactionSubType());
		return obj;
	}

	@Override
	public List<DupcheckRuleModel> getAllDupcheckRules() {
		List<DupcheckRuleModel> list = new ArrayList<>();
		dupcheckRuleRepository.findAll().forEach(e -> list.add(e));
		return list;
	}

	@Override
	public DupcheckRuleModel getDupcheckRuleById(long dupcheckRuleId) {
		DupcheckRuleModel dupcheckRuleModel = dupcheckRuleRepository.findById(dupcheckRuleId);
		return dupcheckRuleModel;
	}

	public long getMetadataId() {
		Random random = new Random(System.nanoTime() % 100000);
		long uniqueMetadataId = random.nextInt(1000000000);
		return uniqueMetadataId;
	}

	public String getCurrentDateTime() {
		LocalDateTime localDateTime = LocalDateTime.now();
		return localDateTime.toString();
	}
	
	private DupcheckTransactionModel getDupCheckTransaction(RequestData reqeustData, String fieldValue) {
		DupcheckTransactionModel transactionData = DupcheckTransactionModel.builder().transactionId(reqeustData.getTransactionID())
				.transactionType(reqeustData.getTransactionType())
				.transactionSubType(reqeustData.getTransactionSubType())
				.fieldValue(fieldValue)
				.createdOn(reqeustData.getCreatedOn())
				.updatedOn(reqeustData.getUpdatedOn()).build();
		return transactionData;	
	}
}
