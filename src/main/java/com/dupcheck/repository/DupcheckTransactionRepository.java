package com.dupcheck.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dupcheck.model.DupcheckTransactionModel;

public interface DupcheckTransactionRepository extends JpaRepository<DupcheckTransactionModel, DupcheckTransactionModel> {
	
	@Query(value="SELECT * FROM DupcheckTransactionModel sd WHERE sd.transactionId = ?1", nativeQuery=true)
	public DupcheckTransactionModel findById(long transactionId);
	
	@Query(value="SELECT * FROM DupcheckTransactionModel sd WHERE sd.transactionType = ?1 AND sd.transactionSubType = ?2 AND sd.fieldValue = ?3", nativeQuery=true)
	public List<DupcheckTransactionModel> findByFieldValue(String transactionType, String transactionSubType, String  fieldValue);
}
