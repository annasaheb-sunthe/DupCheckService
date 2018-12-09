package com.dupcheck.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dupcheck.model.DupcheckRuleModel;

@Repository
public interface DupcheckRuleRepository extends JpaRepository<DupcheckRuleModel, DupcheckRuleModel>{
	
	@Query(value="SELECT * FROM DupcheckRuleModel sd WHERE sd.transactionType=:transactionType AND sd.transactionSubType=:transactionSubType", nativeQuery=true)
	public List<DupcheckRuleModel> findByType(@Param("transactionType") String transactionType, @Param("transactionSubType") String transactionSubType);
	
	@Query(value="SELECT * FROM DupcheckRuleModel sd WHERE sd.dupcheckRuleId = ?1", nativeQuery=true)
	public DupcheckRuleModel findById(long dupcheckRuleId);
	
	@Modifying
	@Transactional
	@Query("UPDATE DupcheckRuleModel d SET d.transactionType=:transactionType, d.transactionSubType=:transactionSubType, d.dupcheckType=:dupcheckType, d.fieldName=:fieldName, d.updatedOn=:updatedOn WHERE d.dupcheckRuleId=:dupcheckRuleId")
	public int updateById(@Param("transactionType") String transactionType, @Param("transactionSubType") String transactionSubType, @Param("dupcheckType") Integer dupcheckType, @Param("fieldName") String fieldName, @Param("updatedOn") String updatedOn, @Param("dupcheckRuleId") long dupcheckRuleId);
}