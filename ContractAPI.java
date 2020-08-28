package com.salesforce.automation.commonAPI;

import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.TestDataProvider;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.GregorianCalendar;
import java.util.Map;

public class ContractAPI {
	
	private static final Logger LOG = LoggerFactory.getLogger(ContractAPI.class);

	public String ContractId;
	final static private String TEST_DATA = "./testdata/Contract.xml";
	protected static Map<String, Map<String, Object>> dataMap = TestDataProvider.dataReader(TEST_DATA);
	
	PageObjectApi 	pageObjectApi 	= new PageObjectApi();
	CommonAPI commonAPI = new CommonAPI();
	ApiUtilities 	apiUtilities	= new ApiUtilities();

	/**
	 * Create AssertLine on Contract Using API call
	 * @param connection
	 * @param ContactId
	 **/
	public void createAssertLine(PartnerConnection connection, String ContractId) throws InterruptedException{

		try{
			int AssertLinelength = 0;
			String AssertLineScript = "AssetBatchProcess.runAssetBatchProcessOnce('"+ContractId+"','pmarina@salesforce.com');";
			apiUtilities.runApex(AssertLineScript);
			String AssertLineQuery = "SELECT Name FROM Apttus_Config2__AssetLineItem__c WHERE SfdcAssetContract__c = '"+ContractId+"'";
			SObject[] AssertLineSObject = ApiUtilities.executeQuery(connection, AssertLineQuery);
			AssertLinelength = AssertLineSObject.length;
			if(AssertLinelength == 0){
				LOG.info("AssertLine not created");
				System.exit(0);
			}					
		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
	}
	
	/**
	 * Create COSO on Contract Using API call
	 * @param connection
	 * @param ContactId
	 **/
	public void createCOSO(PartnerConnection connection, String ContractId) throws InterruptedException{

		try{
			
			//update sfbase__COSOUpdateRequired__c field as true in contract
			commonAPI.updateField(connection, "Contract", ContractId, "sfbase__COSOUpdateRequired__c", true);
			
			//Create COSO
			String COCOScript = "RenewalsCosoGeneration cosoProcess = new RenewalsCosoGeneration();";
			COCOScript += "cosoProcess.runCosoProcess('pmarina@salesforce.com','20',null,'"+ContractId+"');";
			apiUtilities.runApex(COCOScript);
			
			String COSOQuery = "SELECT Id, sfbase__Quantity__c,	sfbase__TotalOrderValue__c FROM sfbase__ContractOrderSummary__c  WHERE sfbase__Contract__c  = '"+ContractId+"' ORDER BY sfbase__Quantity__c Desc";
			SObject[] COSOSObject = ApiUtilities.executeQuery(connection, COSOQuery);
			
			int COSOlength = 0;
			COSOlength = COSOSObject.length;
			if(COSOlength == 0){
				LOG.info("COSO not created");
				System.exit(0);
			}else{
				LOG.info("COSO created");
				LOG.info("COSO Amount validation - Starts");
				String COSO_Quantity = (String)COSOSObject[0].getField("sfbase__Quantity__c");
				String COSO_Amount = (String)COSOSObject[0].getField("sfbase__TotalOrderValue__c");
				
				float COSO_Quantity_Float = Float.parseFloat(COSO_Quantity);
				float COSO_Amount_Float = Float.parseFloat(COSO_Amount);
				
				int COSO_Quantity_int = (int)COSO_Quantity_Float;
				int COSO_Amount_int   = (int)COSO_Amount_Float;

				LOG.info("COSO Amount validation - Done");
			}
		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
	}
	
	
	/**
	 * Create Contract on Contract Using API call - @pdatrak
	 * @param connection
	 * @param accountId
	 * @param priceBookId
	 * @param contactId
	 * @param StartDate
	 * @param Term
	 * @return ContractId
	 **/	
	public String createContract(PartnerConnection connection,String accountId,String priceBookId,String contactId, GregorianCalendar startDate, int term) throws Exception{

		//Get Account Name
		String AccountName = commonAPI.getFieldValue(connection, "Account", accountId, "Name");
		LOG.info(" AccountName : "+AccountName);

		dataMap = TestDataProvider.dataReader(TEST_DATA);
		Map<String,Object> contract=dataMap.get("GlobalContract");

		contract.put("AccountId", accountId);
		contract.put("sfbase__ContractType__c", "New");
		contract.put("Pricebook2Id",priceBookId);
		contract.put("StartDate", startDate.getTime());
		contract.put("ContractTerm",term);
		contract.put("BillingEmail", "test@test.com");
		contract.put("OrderPrebillDays",30);
		contract.put("BillingLanguage", "en_US");
		contract.put("BillingCompany",AccountName);
		contract.put("Status", "Draft");
		contract.put("sfbase__BillingContact__c", contactId);
		contract.put("BillingEmail", "qetest@test.com");
	
		String ContractId = ApiUtilities.createObject(connection,"Contract", contract);
		return ContractId;
	}
	/**
	 * Create Contract on Contract with  JPY currency Using API call - @pdatrak
	 * @param connection
	 * @param accountId
	 * @param priceBookId
	 * @param contactId
	 * @param StartDate
	 * @param Term
	 * @return ContractId
	 **/
	public String createContract_JPY(PartnerConnection connection,String accountId,String priceBookId,String contactId, GregorianCalendar startDate, int term) throws Exception{

		//Get Account Name
		String AccountName = commonAPI.getFieldValue(connection, "Account", accountId, "Name");
		LOG.info(" AccountName : "+AccountName);

		dataMap = TestDataProvider.dataReader(TEST_DATA);
		Map<String,Object> contract=dataMap.get("GlobalContract_JPY");

		contract.put("AccountId", accountId);
		contract.put("sfbase__ContractType__c", "New");
		contract.put("Pricebook2Id",priceBookId);
		contract.put("StartDate", startDate.getTime());
		contract.put("ContractTerm",term);
		contract.put("BillingEmail", "test@test.com");
		contract.put("OrderPrebillDays",30);
		contract.put("BillingLanguage", "en_US");
		contract.put("BillingCompany",AccountName);
		contract.put("Status", "Draft");
		contract.put("sfbase__BillingContact__c", contactId);
		contract.put("BillingEmail", "qetest@test.com");
	
		String ContractId = ApiUtilities.createObject(connection,"Contract", contract);
		return ContractId;
	}
}