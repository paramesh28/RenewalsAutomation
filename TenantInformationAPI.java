package com.salesforce.automation.commonAPI;

import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.TestDataProvider;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TenantInformationAPI {

	private static final Logger LOG = LoggerFactory.getLogger(TenantAPI.class);
	final static private String TEST_DATA = "./testdata/Account.xml";
	protected static Map<String, Map<String, Object>> dataMap = TestDataProvider.dataReader(TEST_DATA);

	/**
	 * Create Tenant Incormation - Using API call - 
	 * @param connection
	 * @param TenantId
	 * @param accountId
	 * @param FulfillmentProviderName
	 * @param TenantExternalId
	 * @return TenantInfoId
	 **/
	public String createTenantInfo(PartnerConnection connection,String TenantId, String accountId,String FulfillmentProviderName, String TenantExternalId) throws Exception{
		//FulfillmentProviderId(Force.com)
		String query= "SELECT Id FROM FulfillmentProvider__c where Name='"+FulfillmentProviderName+"'";
		SObject[] FullfilmentProvider = ApiUtilities.executeQuery(connection, query);
		String FullfilmentProvider_ID=(String)FullfilmentProvider[0].getField("Id");

		Map<String,Object> createTenantInfo=dataMap.get("GlobalTenantInfo");
		createTenantInfo.put("SfdcTenant__c", TenantId);
		createTenantInfo.put("SfdcFulfillmentProvider__c",FullfilmentProvider_ID);
		createTenantInfo.put("Apttus_Config2__AccountId__c",accountId);
		createTenantInfo.put("SfdcParameters__c", "{\"ExtTenantId\":\""+TenantExternalId+"\"}");

		String TenantInfoId = ApiUtilities.createObject(connection,"Apttus_Config2__AccountLocation__c", createTenantInfo);
		LOG.info(" : TenantInfo Id  for "+FulfillmentProviderName+": "+TenantInfoId);

		return TenantInfoId;
	}
}