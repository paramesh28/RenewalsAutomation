package com.salesforce.automation.commonAPI;

import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.TestDataProvider;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TenantAPI {
	
	private static final Logger LOG = LoggerFactory.getLogger(TenantAPI.class);
	
	final static private String TEST_DATA = "./testdata/Account.xml";
	protected static Map<String, Map<String, Object>> dataMap = TestDataProvider.dataReader(TEST_DATA);
	
	
	
	/**
	 * Create Tenant - Using API call - 
	 * @param connection
	 * @param accountId
	 * @param TenantName - String Try to pass the account name
	 * @param orgId
	 * @param CloudServiceProvider
	 * @return TenantId
	 **/
	public String createTenant(PartnerConnection connection, String accountId, String TenantName,String orgId, String CloudServiceProvider) throws Exception{

		String query= "SELECT Id FROM CloudServiceProvider where Name = '"+CloudServiceProvider+"'";
		SObject[] TenantId1 = ApiUtilities.executeQuery(connection, query);
		String CloudServiceProviderID=(String)TenantId1[0].getField("Id");
		LOG.info(" : Tenant ID for "+CloudServiceProvider);
		Map<String,Object> createTenant = dataMap.get("GlobalTenant");
		
		createTenant.put("AccountId", accountId);
		createTenant.put("Name",TenantName);        	
		createTenant.put("CloudServiceProviderId",CloudServiceProviderID);
		createTenant.put("ExternalId", orgId);
		String TenantId = ApiUtilities.createObject(connection,"Tenant", createTenant);
		LOG.info(" : TenantId for "+CloudServiceProvider+" : "+TenantId);

		return TenantId;
	}
}