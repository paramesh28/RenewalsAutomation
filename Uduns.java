package com.salesforce.automation.commonAPI;

import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.TestDataProvider;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

public class Uduns {

	private static final Logger LOG = LoggerFactory.getLogger(Uduns.class);

	public String CompanyId;

	Date date;
	int hrs;
	int min;
	int sec;
	protected static Map<String, Map<String, Object>> dataMap;
	final static private String TEST_DATA = "./testdata/ROR_TestData.xml";

	//Create Company with Uniqe UDUN - API call - @jjayapal
	public String creatCompany(PartnerConnection custom_connection, String UdunNumber) throws InterruptedException{

		try{
			//Get Country Record Type Id
			String CompanyRT_Id_Query = "SELECT Id FROM RecordType WHERE SobjectType = 'Company__c' and Name = 'Country'";
			SObject[] CompanyRT_Id_SObject = ApiUtilities.executeQuery(custom_connection, CompanyRT_Id_Query);
			String RecordTypeId = (String)CompanyRT_Id_SObject[0].getField("Id");

			//Create new Company
			LOG.info(" : Create New Company with New UDUN -  Started.......");
			String CompanyName = "ROR Company "+UdunNumber;

			dataMap = TestDataProvider.dataReader(TEST_DATA);
			Map<String,Object> companyMap = dataMap.get("GlobalCompany");

			companyMap.put("RecordTypeId", RecordTypeId);
			companyMap.put("Name", CompanyName);
			companyMap.put("Ultimate_Parent_DUNS__c",UdunNumber);
			companyMap.put("CurrencyIsoCode", "USD");
			companyMap.put("Country__c","US");
			String CompanyId = ApiUtilities.createObject(custom_connection,"Company__c", companyMap);

			return CompanyId;

		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return CompanyId;
	}


	//Create UDUNS 9 Digit number
	public String genarateUdunsNumber() throws ConnectionException {

		
		//Class Object Creations using API Connections
		ApiUtilities apiUtilities = new ApiUtilities();
		
		//User Details from POM Config
		String AdminUN = System.getProperty("testUsernameParam");
		String AdminPWD = System.getProperty("testPasswordParam");	
		
		//API Conection Creation
		PartnerConnection 	connection_admin = null;
		connection_admin = apiUtilities.customLogin(AdminUN, AdminPWD);

		String Company_Uduns_Query = "SELECT Ultimate_Parent_DUNS__c FROM Company__c Order by Ultimate_Parent_DUNS__c limit 1";
		SObject[] Company_Uduns_SObject = ApiUtilities.executeQuery(connection_admin, Company_Uduns_Query);
		String FirstUdun = (String)Company_Uduns_SObject[0].getField("Ultimate_Parent_DUNS__c");
		LOG.info(" :     smallest UDUNS is : "+FirstUdun);
		
		int FirstUdun_int = Integer.parseInt(FirstUdun);
		int FirstUdun_temp = FirstUdun_int-1;
		
		String FirstUdun_str = Integer.toString(FirstUdun_temp);
		int FirstUdun_len = FirstUdun_str.length();
		int Diff = 9-FirstUdun_len;
		
		if(Diff > 0){			
			for(int i = 0; i < Diff; i++){
				FirstUdun_str = "0"+FirstUdun_str;
			}			
		}
		LOG.info(" : New smallest UDUNS is : "+FirstUdun_str);
		
		return FirstUdun_str;
	}


	//Generate 9 digit UDUNS based random number using Partner Connection
	public String genarateUdunsNumber(PartnerConnection connection_admin) throws ConnectionException, InterruptedException {

		String UDUNS = null;
		int isUDUNSPresent = 0;

		for(int i = 0; i <  10; i++){

			Thread.sleep(3000);
			long timeSeed = System.nanoTime();
			double randSeed = Math.random() * 1000;
			long midSeed = (long) (timeSeed * randSeed);        
			String s = midSeed + "";
			String subStr = s.substring(0, 9);
			int RandomUDUNS = Integer.parseInt(subStr);

			String Uduns_Query = "SELECT Ultimate_Parent_DUNS__c FROM Company__c Where Ultimate_Parent_DUNS__c = '"+RandomUDUNS+"'";
			SObject[] Uduns_SObject = ApiUtilities.executeQuery(connection_admin, Uduns_Query);
			isUDUNSPresent = Uduns_SObject.length;
			UDUNS = Integer.toString(RandomUDUNS);

			if(isUDUNSPresent == 0){
				i = 200;
			}
		}
		return UDUNS;
	}
}