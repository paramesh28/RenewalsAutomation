package com.salesforce.automation.commonAPI;

import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.TestDataProvider;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MockOrgAPI {
private static final Logger LOG = LoggerFactory.getLogger(MockOrgAPI.class);
	
	final static private String TEST_DATA = "./testdata/Account.xml";
	protected static Map<String, Map<String, Object>> dataMap = TestDataProvider.dataReader(TEST_DATA);
	
	CommonAPI commonAPI = new CommonAPI();
	
	
	/**
	 * Create Mock Org Id Using API call
	 * @param connection
	 * @param accountId
	 * @return mockOrgId
	 **/
	public String createMockOrgId(PartnerConnection connection, String accountId) throws ConnectionException{
		
		LOG.info("--------------------------------------------------------");
		LOG.info("                 Create Mock Org Id                     ");
		LOG.info("--------------------------------------------------------");
		String mockOrgId = null;
		Map<String,Object> orgId = dataMap.get("GlobalOrgId");
		orgId.put("Account__c", accountId);
		mockOrgId = ApiUtilities.createObject(connection,"MockOrg__c", orgId);
		LOG.info("OrgId : "+mockOrgId);
		System.out.println("");
		System.out.println("");

		return mockOrgId;
	}
	
	
	/**
	 * Get Mock Name - Using API call - @jjayapal
	 * @param connection
	 * @param MockOrgId
	 * @return MockOrgName
	 **/
	public String getMockOrgNameById(PartnerConnection connection, String MockOrgId) throws InterruptedException{

		try{
			String MockOrgName_Query = "SELECT Name FROM MockOrg__c WHERE Id = '"+MockOrgId+"'";
			SObject[] MockOrgName_SObject = ApiUtilities.executeQuery(connection, MockOrgName_Query);
			String MockOrgName = (String)MockOrgName_SObject[0].getField("Name");
			LOG.info("MockOrgName : "+MockOrgName);
			System.out.println("");
			System.out.println("");
			return MockOrgName;

		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return null;
	}

}
