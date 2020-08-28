package com.salesforce.automation.commonAPI;

import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.TestDataProvider;
import com.sforce.soap.partner.PartnerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ContactAPI {

	private static final Logger LOG = LoggerFactory.getLogger(ContactAPI.class);

	public String ContactId;
	final static private String TEST_DATA = "./testdata/Account.xml";
	protected static Map<String, Map<String, Object>> dataMap = TestDataProvider.dataReader(TEST_DATA);
	
	CommonAPI commonAPI = new CommonAPI();

	/**
	 * Create New Contact on Account Using API call
	 * @param connection
	 * @param AccountId
	 * @return ContactId
	 **/
	public String creatContact(PartnerConnection connection, String AccountId) throws InterruptedException{

		try{
			LOG.info("--------------------------------------------------------");
			LOG.info("              Create New Contact on Account             ");
			LOG.info("--------------------------------------------------------");
			
			//Get Country Record Type Id
			String RecordTypeId = commonAPI.getRecordTypeId(connection, "Contact", "Contact");
			String AccountName 	= commonAPI.getFieldValue(connection, "Account", AccountId, "Name");

			//Create new Company
			LOG.info(" : Create New Contact -  Started.......");
			dataMap = TestDataProvider.dataReader(TEST_DATA);
			Map<String,Object> contactMap = dataMap.get("GlobalContact");
			contactMap.put("FirstName", AccountName);
			contactMap.put("RecordTypeId", RecordTypeId);
			contactMap.put("AccountId", AccountId);
			String ContactId = ApiUtilities.createObject(connection,"Contact", contactMap);
			LOG.info("Contact Id   : "+ContactId);
			System.out.println("");
			System.out.println("");
			return ContactId;

		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return ContactId;
	}
}