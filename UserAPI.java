package com.salesforce.automation.commonAPI;

import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.TestDataProvider;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UserAPI {

	private static final Logger LOG = LoggerFactory.getLogger(UserAPI.class);
	
	final static private String TEST_DATA = "./testdata/User.xml";
	protected static Map<String, Map<String, Object>> dataMap = TestDataProvider.dataReader(TEST_DATA);
	
	CommonAPI commonAPI = new CommonAPI();


	/**
	 *  This method is to get SalesForce team Market segment roles for created account.
	 *  Method will return marketSegment all roles for account with out comma at the end to use in other SQL queries.
	 *  @params Account_ID
	 *  @return MarketSegment_RecordsFormattedString
	 */
	public String getSFTeamMarketSegmentString(PartnerConnection connection, String AccountId) throws ConnectionException{
		
		LOG.info(" : Collecting SF Team Market Segment Details");
		String SF_MarketSegmentQuery = "SELECT sfbase__Market_Segment__c FROM User WHERE Id IN (SELECT sfbase__User__c FROM sfbase__SalesforceTeam__c WHERE sfbase__Account__c = '"+AccountId+"')";
		SObject[] SF_MarketSegmentObject = ApiUtilities.executeQuery(connection, SF_MarketSegmentQuery);
		int MarketSegmentQuery_Length = SF_MarketSegmentObject.length;

		String MarketSegment_AllrecordsString = "";
		for(int i =0; i<MarketSegmentQuery_Length; i++)
		{
			MarketSegment_AllrecordsString += "'"+(String)SF_MarketSegmentObject[i].getField("sfbase__Market_Segment__c")+"',";
		}
		String MarketSegment_RecordsFormattedString = MarketSegment_AllrecordsString.substring(0, MarketSegment_AllrecordsString.length() -1);

		return MarketSegment_RecordsFormattedString;
	}
	
	//Create New User
	public String createUser(PartnerConnection connection, String FName, String LName, String UserName, String Alias, String ProfileName, String MarketSegment, String E_Number) throws ConnectionException, Exception{
		
		String UserId = null;
		LOG.info(" ");
		LOG.info(" : Create New user ");
		LOG.info(" : First Name   : "+FName);
		LOG.info(" : Last Name    : "+LName);
		LOG.info(" : UserName     : "+UserName);
		LOG.info(" : Profile Name : "+ProfileName);
		LOG.info(" ");
		LOG.info(" : Create New user ");
		
		String ProfileId	= commonAPI.getIdByName(connection, "Profile", ProfileName);
		String RoleId		= commonAPI.getIdByName(connection, "UserRole", "QA and Development");
		
		Map<String,Object> UserMap = dataMap.get("User");		
		UserMap.put("FirstName", FName);	
		UserMap.put("LastName", LName);
		UserMap.put("Username", UserName);
		UserMap.put("Alias", Alias);
		UserMap.put("ProfileId", ProfileId);
		UserMap.put("UserRoleId", RoleId);
		UserMap.put("sf62user__Employee_Number__c", E_Number);
		UserMap.put("sfbase__Market_Segment__c", MarketSegment);		
		UserMap.put("Email", "jjayapal@salesforce.com");
		UserMap.put("IsActive", true);
		UserMap.put("CurrencyIsoCode", "USD");
		UserMap.put("LanguageLocaleKey", "en_US");
		UserMap.put("LocaleSidKey", "en_US");
		UserMap.put("sfbase__Classification__c", "CS");		
		UserMap.put("sfbase__Region__c", "AMER");
		UserMap.put("TimeZoneSidKey", "America/Los_Angeles");
		UserMap.put("EmailEncodingKey", "ISO-8859-1");
		
		UserId = ApiUtilities.createObject(connection,"User", UserMap);
		LOG.info("User Id   : "+UserId);
		ApiUtilities.runApex("System.resetPassword('"+UserId+"', True);");
		return UserId;
	}
	
	
	//Create Team User
	public String createTeamUser(PartnerConnection connection, String UserId, String TeamRoleId, String TerritoryUsageTypeId) throws ConnectionException, Exception{
		
		String TeamUserId = null;
		LOG.info(" ");
		LOG.info(" : Create Team user ");		
		Map<String,Object> TeamUserMap = dataMap.get("Team_User__c");		
		TeamUserMap.put("Team_User_id__c", UserId);	
		TeamUserMap.put("Team_Role_Id__c", TeamRoleId);
		TeamUserMap.put("Territory_Usage_Type__c", TerritoryUsageTypeId);
		TeamUserMap.put("Active__c", true);
		
		TeamUserId = ApiUtilities.createObject(connection,"Team_User__c", TeamUserMap);
		LOG.info("Team User Id   : "+TeamUserId);
		return TeamUserId;
	}
	
	
	//Get Profile Id based on Name
	public static String getProfileIdByName(PartnerConnection connection, String ProfileName) throws ConnectionException{
		
		String ProfileId = null;		
		String ProfileNameQuery = "SELECT Id FROM Profile WHERE Name = '"+ProfileName+"'";
		SObject[] ProfileNameObject = ApiUtilities.executeQuery(connection, ProfileNameQuery);
		ProfileId = (String)ProfileNameObject[0].getField("Id");
		
		return ProfileId;
	}
	
	public String setSolarSandboxTestUser(PartnerConnection connection) throws InterruptedException{

		try{
			

			String UN = System.getProperty("AdminUsername");
			int UN_Lenth = UN.length();
			int Start	= UN.indexOf(".com.");
			String SandboxName = UN.substring(Start+5, UN_Lenth);
			LOG.info(" : SandboxName -  "+SandboxName);
			
			LOG.info("*********************************************************************");
			LOG.info("**********  Set Salesforce Team Process - Start  ********************");
			LOG.info("*********************************************************************");			
			getUser(connection, "leaduser@salesforce.com."+SandboxName,					"Lead",						"User",			"luser",	"SR/EBR/CSDR - Appstore",								"STRAT", 		"202461");
			getUser(connection, "mc_agency_user@salesforce.com."+SandboxName,			"MC Agency",				"Test User",	"mtest",	"MC Agency",											"MC- Services", "202462");
			getUser(connection, "mc_direct_user@salesforce.com."+SandboxName,			"MC Direct",				"Test USer",	"mtest",	"MC Direct",											"Agency", 		"202463");
			getUser(connection, "mcaetestuser@salesforce.com."+SandboxName,				"Test", 					"User",			"mtest",	"Services - Enterprise Consulting Temp",				"PAM", 			"202464");
			getUser(connection, "pardotsales@salesforce.com."+SandboxName,				"Pardot",					"Sales",		"psale",	"Pardot Sales",											"MGR", 			"202465");
			getUser(connection, "platformsales@salesforce.com."+SandboxName,			"Platform",					"Sales",		"psale",	"Value Add Sales",										"MM", 			"202466");
			getUser(connection, "qafs0@salesforce.com."+SandboxName,					"QAFS0",					"QAFS0", 		"qafs",		"Global Field Sales",									"SR", 			"202467");
			getUser(connection, "qasalesops@salesforce.com."+SandboxName,				"QA",						"Salesops",		"qsale",	"Sales Operations Manager and Contract Specialists",	"PAM", 			"202468");
			getUser(connection, "rmmanager@salesforce.com."+SandboxName,				"RM",						"Manager",		"rmana",	"CSM Manager",											"MGR", 			"202469");
			getUser(connection, "rmuser@salesforce.com."+SandboxName,					"RM",						"User",			"ruser",	"CSM",													"CSM", 			"202470");
			//getUser(connection, "salesoperationsmanager@salesforce.com."+SandboxName,	"SalesOperationsManager",	"sale",			"sales", 	"Operations Manager and Contract Specialists",			"Agency", 		"202471");
			getUser(connection, "salesoperationsuser@salesforce.com."+SandboxName,		"Sales Operations",			"User",			"suser",	"Sales Operations",										"PAM", 			"202472");
			//getUser(connection, "seutilityapiuser@salesforce.com."+SandboxName,			"SE Impact",				"Doctor",		"seutil",	"API Internal - SE Utility",							"MGR", 			"202473");
			getUser(connection, "sfdcmanager@salesforce.com."+SandboxName,				"SFDC",						"Manager",		"sfdcmana",	"SFDC Manager",											"MM", 			"202474");
			getUser(connection, "sruser@salesforce.com."+SandboxName,					"SR",						"User",			"suser",	"SR/EBR/CSDR - Appstore",								"SR", 			"202475");
			LOG.info("*********************************************************************");
			
		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return null;
	}
	
	public String getUser(PartnerConnection connection,
			String EmailId,
			String Fname,
			String Lname,
			String Alias,
			String ProfileName,
			String MarketSegment,
			String EmpId) throws ConnectionException, Exception{
		
		String SOLAR_User_Query 		= null;
		SObject[] SOLAR_User_SObject 	= null;
		String SOLAR_User 				= null;
		
		SOLAR_User_Query = "SELECT Id FROM User WHERE Username = '"+EmailId+"'";
		SOLAR_User_SObject = ApiUtilities.executeQuery(connection, SOLAR_User_Query);
		if(SOLAR_User_SObject.length >= 1){
			SOLAR_User = (String)SOLAR_User_SObject[0].getField("Id");
			LOG.info(ProfileName+" SOLAR_User Already Present   : "+SOLAR_User);
		}else{
			SOLAR_User = createUser(connection, Fname, Lname, EmailId, Alias, ProfileName, MarketSegment, EmpId);
			LOG.info(ProfileName+" SOLAR_User : "+SOLAR_User);
		}
		return SOLAR_User;
	}
}
