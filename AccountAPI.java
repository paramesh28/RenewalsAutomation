package com.salesforce.automation.commonAPI;

import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.TestDataProvider;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class AccountAPI {

	private static final Logger LOG = LoggerFactory.getLogger(AccountAPI.class);

	final static private String TEST_DATA = "./testdata/Account.xml";
	protected static Map<String, Map<String, Object>> dataMap = TestDataProvider.dataReader(TEST_DATA);

	CommonAPI commonAPI = new CommonAPI();
	UserAPI 		userAPI 		= new UserAPI();
	ApiUtilities 	apiUtilities 	= new ApiUtilities();

	/**
	 * Create New Account Using API call
	 * @param connection
	 * @param recordType - String, ie. "Sales".
	 * @return accountId
	 * @throws ConnectionException 
	 * @throws InterruptedException 
	 **/
	public String createAccount(PartnerConnection connection, String recordType) throws ConnectionException, InterruptedException{

		LOG.info("--------------------------------------------------------");
		LOG.info("                 Create New Account                     ");
		LOG.info("--------------------------------------------------------");
		String accountId = null;
		String recordTypeId = commonAPI.getRecordTypeId(connection, "Account", recordType);
		Map<String,Object> AccountMap = dataMap.get("GlobalAccount");
		LOG.info("                GlobalAccount                     ");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());		
		AccountMap.put("Name", "LUNAR_TA_"+timestamp.getTime());
		AccountMap.put("RecordTypeId", recordTypeId);		
		LOG.info("                AccountMap                     "+AccountMap);
		accountId = ApiUtilities.createObject(connection,"Account", AccountMap);
		LOG.info("                createObject                     ");
		LOG.info("Account Id   : "+accountId);
		System.out.println("");
		System.out.println("");

		return accountId;
	}

	public String setMPTerritory(PartnerConnection connection) throws InterruptedException{

		try{
			

			String UN = System.getProperty("AdminUsername");
			int UN_Lenth = UN.length();
			int Start	= UN.indexOf(".com.");
			String SandboxName = UN.substring(Start+5, UN_Lenth);
			LOG.info(" : SandboxName -  "+SandboxName);
			
			LOG.info("*********************************************************************");
			LOG.info("**********  Set Salesforce Team Process - Start  ********************");
			LOG.info("*********************************************************************");			
			// Adding 1st set of Users with  SP
			String SOLAR_COREAE 			= getUser(connection, "solarcoreae@salesforce.com."+SandboxName, 		"SOLAR", "CoreAE", 				"scoreae",	"Global Corporate Sales",	"STRAT", 		"201461");
			String SOLAR_MCAE	 			= getUser(connection, "solarmcae@salesforce.com."+SandboxName, 			"SOLAR", "MCAE", 				"smcae", 	"CSM", 						"CSM", 			"201462");
			String SOLAR_SaleCloudAE 		= getUser(connection, "solarSalescae@salesforce.com."+SandboxName, 		"SOLAR", "SalesCloudAE", 		"sscae", 	"Global Field Sales", 		"BDR", 			"201463");
			String SOLAR_ServiceCloudAE		= getUser(connection, "solarServicecae@salesforce.com."+SandboxName,	"SOLAR", "ServiceCloudAE", 		"ssscae", 	"Global Field Sales", 		"Agency", 		"201464");
			String SOLAR_PardotAE 			= getUser(connection, "solarpardotae@salesforce.com."+SandboxName, 		"SOLAR", "PardotAE", 			"spdae", 	"Pardot Sales", 			"PAM", 			"201465");
			String SOLAR_MCRM 				= getUser(connection, "solarmcrm@salesforce.com."+SandboxName, 			"SOLAR", "MCRM", 				"smcrm", 	"CSM", 						"MGR", 			"201466");	
			String SOLAR_MCSAE				= getUser(connection, "solarmcsae@salesforce.com."+SandboxName, 		"SOLAR", "MCServicesAE", 		"smcsae", 	"CSM", 						"MC- Services",	"201467");	
			String SOLAR_AnalyticsAE 		= getUser(connection, "solaranalyticsae@salesforce.com."+SandboxName, 	"SOLAR", "AnalyticsAE", 		"saae", 	"Value Add Sales", 			"MM", 			"201468");
			String SOLAR_HoldOutUser		= getUser(connection, "solarHoldoutUser@salesforce.com."+SandboxName, 	"SOLAR", "SolarHoldoutUser",	"shou", 	"Value Add Sales", 			"SR", 			"201469");
			LOG.info("*********************************************************************");
			// Adding 2nd set of Users with  SP
			String SOLAR_COREAE2 			= getUser(connection, "solarcoreae2@salesforce.com."+SandboxName, 		"SOLAR CoreAE", 			"WithSP", "scoreae2",	"Global Corporate Sales",	"STRAT", 		"201470");
			String SOLAR_MCAE2	 			= getUser(connection, "solarmcae2@salesforce.com."+SandboxName, 		"SOLAR MCAE", 				"WithSP", "smcae2", 	"CSM", 						"CSM", 			"201471");
			String SOLAR_SaleCloudAE2 		= getUser(connection, "solarSalescae2@salesforce.com."+SandboxName, 	"SOLAR SalesCloudAE", 		"WithSP", "sscae2", 	"Global Field Sales", 		"BDR", 			"201472");
			String SOLAR_ServiceCloudAE2	= getUser(connection, "solarServicecae2@salesforce.com."+SandboxName, 	"SOLAR ServiceCloudAE",		"WithSP", "ssscae2", 	"Global Field Sales", 		"Agency", 		"201473");
			String SOLAR_PardotAE2 			= getUser(connection, "solarpardotae2@salesforce.com."+SandboxName, 	"SOLAR PardotAE", 			"WithSP", "spdae2", 	"Pardot Sales", 			"PAM", 			"201474");
			String SOLAR_MCRM2 				= getUser(connection, "solarmcrm2@salesforce.com."+SandboxName, 		"SOLAR MCRM", 				"WithSP", "smcrm2", 	"CSM", 						"MGR", 			"201475");	
			String SOLAR_MCSAE2				= getUser(connection, "solarmcsae2@salesforce.com."+SandboxName, 		"SOLAR MCServicesAE", 		"WithSP", "smcsae2", 	"CSM", 						"MC- Services",	"201476");	
			String SOLAR_AnalyticsAE2 		= getUser(connection, "solaranalyticsae2@salesforce.com."+SandboxName, 	"SOLAR AnalyticsAE", 		"WithSP", "saae2", 		"Value Add Sales", 			"MM", 			"201477");
			String SOLAR_HoldOutUser2		= getUser(connection, "solarHoldoutUser2@salesforce.com."+SandboxName, 	"SOLAR SolarHoldoutUser", 	"WithSP", "shou2", 		"Value Add Sales", 			"SR", 			"201478");
			String SOLAR_GAM2		        = getUser(connection, "solargam2@salesforce.com."+SandboxName, 	        "SOLAR GAM", 	            "WithSP", "sgam2", 		"Global Corporate Sales", 	"ENTR", 		"201488");
			LOG.info("*********************************************************************");
			// Adding 3nd set of Users without  SP
			String SOLAR_COREAE3 			= getUser(connection, "solarcoreae3@salesforce.com."+SandboxName, 		"SOLAR CoreAE", 			"WithOutSP", "scoreae3",	"Global Corporate Sales",	"STRAT", 		"201479");
			String SOLAR_MCAE3	 			= getUser(connection, "solarmcae3@salesforce.com."+SandboxName, 		"SOLAR MCAE", 				"WithOutSP", "smcae3", 		"CSM", 						"CSM", 			"201480");
			String SOLAR_SaleCloudAE3 		= getUser(connection, "solarSalescae3@salesforce.com."+SandboxName, 	"SOLAR SalesCloudAE", 		"WithOutSP", "sscae3", 		"Global Field Sales", 		"BDR", 			"201481");
			String SOLAR_ServiceCloudAE3	= getUser(connection, "solarServicecae3@salesforce.com."+SandboxName, 	"SOLAR ServiceCloudAE",		"WithOutSP", "ssscae3", 	"Global Field Sales", 		"Agency", 		"201482");
			String SOLAR_PardotAE3 			= getUser(connection, "solarpardotae3@salesforce.com."+SandboxName, 	"SOLAR PardotAE", 			"WithOutSP", "spdae3", 		"Pardot Sales", 			"PAM", 			"201483");
			String SOLAR_MCRM3 				= getUser(connection, "solarmcrm3@salesforce.com."+SandboxName, 		"SOLAR MCRM", 				"WithOutSP", "smcrm3", 		"CSM", 						"MGR", 			"201484");	
			String SOLAR_MCSAE3				= getUser(connection, "solarmcsae3@salesforce.com."+SandboxName, 		"SOLAR MCServicesAE", 		"WithOutSP", "smcsae3", 	"CSM", 						"MC- Services",	"201485");	
			String SOLAR_AnalyticsAE3 		= getUser(connection, "solaranalyticsae3@salesforce.com."+SandboxName, 	"SOLAR AnalyticsAE", 		"WithOutSP", "saae3", 		"Value Add Sales", 			"MM", 			"201486");
			String SOLAR_HoldOutUser3		= getUser(connection, "solarHoldoutUser3@salesforce.com."+SandboxName, 	"SOLAR SolarHoldoutUser", 	"WithOutSP", "shou3", 		"Value Add Sales", 			"SR", 			"201487");
			LOG.info("*********************************************************************");
			//Sales Profile Creation
			String SOLAR_COREAE_SP2 		= createSalesProfile(connection, SOLAR_COREAE2, 		"Core AE");
			String SOLAR_MCAE_SP2 			= createSalesProfile(connection, SOLAR_MCAE2, 			"MC AE");
			String SOLAR_SaleCloudAE_SP2 	= createSalesProfile(connection, SOLAR_SaleCloudAE2, 	"Sales Cloud AE");
			String SOLAR_ServiceCloudAE_SP2	= createSalesProfile(connection, SOLAR_ServiceCloudAE2,	"Service Cloud AE");
			String SOLAR_PardotAE_SP2 		= createSalesProfile(connection, SOLAR_PardotAE2, 		"Pardot AE");
			String SOLAR_MCRM_SP2 			= createSalesProfile(connection, SOLAR_MCRM2, 			"MC RM");	
			String SOLAR_MCSAE_SP2			= createSalesProfile(connection, SOLAR_MCSAE2, 			"MC Services AE");	
			String SOLAR_AnalyticsAE_SP2 	= createSalesProfile(connection, SOLAR_AnalyticsAE2, 	"Analytics AE");
			String SOLAR_HoldOutUser_SP2	= createSalesProfile(connection, SOLAR_HoldOutUser2, 	"Steelbrick AE");
			String SOLAR_GAM_SP2	        = createSalesProfile(connection, SOLAR_GAM2, 	        "GAM");
			LOG.info("*********************************************************************");
			LOG.info("  SOLAR_COREAE "+SOLAR_COREAE);
			ArrayList allTestUsers = new ArrayList();
			allTestUsers.add(SOLAR_COREAE);
			allTestUsers.add(SOLAR_MCAE);
			allTestUsers.add(SOLAR_SaleCloudAE);
			allTestUsers.add(SOLAR_ServiceCloudAE);
			allTestUsers.add(SOLAR_PardotAE);
			allTestUsers.add(SOLAR_MCRM);
			allTestUsers.add(SOLAR_MCSAE);
			allTestUsers.add(SOLAR_AnalyticsAE);
			allTestUsers.add(SOLAR_HoldOutUser);

			allTestUsers.add(SOLAR_COREAE2);
			allTestUsers.add(SOLAR_MCAE2);
			allTestUsers.add(SOLAR_SaleCloudAE2);
			allTestUsers.add(SOLAR_ServiceCloudAE2);
			allTestUsers.add(SOLAR_PardotAE2);
			allTestUsers.add(SOLAR_MCRM2);
			allTestUsers.add(SOLAR_MCSAE2);
			allTestUsers.add(SOLAR_AnalyticsAE2);
			allTestUsers.add(SOLAR_HoldOutUser2);
			allTestUsers.add(SOLAR_GAM2);

			allTestUsers.add(SOLAR_COREAE3);
			allTestUsers.add(SOLAR_MCAE3);
			allTestUsers.add(SOLAR_SaleCloudAE3);
			allTestUsers.add(SOLAR_ServiceCloudAE3);
			allTestUsers.add(SOLAR_PardotAE3);
			allTestUsers.add(SOLAR_MCRM3);
			allTestUsers.add(SOLAR_MCSAE3);
			allTestUsers.add(SOLAR_AnalyticsAE3);
			allTestUsers.add(SOLAR_HoldOutUser3);
			
			String TestUserManagerUpdate = System.getProperty("TestUserManagerUpdate");
			System.out.println("TestUserManagerUpdate Value = "+TestUserManagerUpdate);
			if(TestUserManagerUpdate.equals("true")){
			LOG.info("-----------------------------------------------------------------");
			int noOfTestUsers = allTestUsers.size();
			
			String SFDC_Manager_ProfileId = commonAPI.getIdValueBasedOnField(connection, "Profile", "Name", "SFDC Manager");
			
			String ManagersList = "SELECT Id "
											+ "FROM User "
											+ "WHERE (Not Name LIKE 'SOLAR%') "
											+ "AND profileId = '"+SFDC_Manager_ProfileId+"' "
													+ "AND sf62user__manager__c != NULL "
													+ "AND sf62user__manager__r.sf62user__manager__c != NULL "
													+ "AND IsActive = true "
													+ " LIMIT "+noOfTestUsers;
			SObject[] ManagersList_SObject = ApiUtilities.executeQuery(connection, ManagersList);
			String ManagerId = null;
			String TestUserId = null;
			String ManagerName = null;
			String TestUserName = null;
			String ApexScript = null;
			
			for(int i = 0; i < noOfTestUsers; i++ ){
				ManagerId = null;
				TestUserId = null;
				ManagerId = (String)ManagersList_SObject[i].getField("Id");
				TestUserId = (String)allTestUsers.get(i);
				ManagerName	= 
				TestUserName = 
				//commonAPI.updateField(connection, "User", TestUserId, "ManagerId", ManagerId);
				ApexScript = "List<User> users = [SELECT Id, sf62user__manager__c FROM User  WHERE Id = '"+TestUserId+"'];"
						+ "for(User user : users) {"
						+ "user.sf62user__manager__c = '"+ManagerId+"'; ​}"
								+ " update users;";
				
				ApexScript = "User user = [SELECT Id, sf62user__manager__c FROM User  WHERE Id = '"+TestUserId+"']; "
						+ "user.sf62user__manager__c = '"+ManagerId+"';"
						+ "user.ManagerId = '"+ManagerId+"';"
						+ " update user;";
				
				LOG.info("Updating user "+ commonAPI.getFieldValue(connection, "User", TestUserId, "Name")+"("+TestUserId+") Manager as "+ commonAPI.getFieldValue(connection, "User", ManagerId, "Name")+"("+ManagerId+")");
				LOG.info(" Update Action :"+apiUtilities.runApexWithReturn(ApexScript));
				LOG.info("-----------------------------------------------------------------");
			}
			}
			LOG.info("*********************************************************************");
			//Team User - SetUp
			String TU_SOLAR_COREAE 			= null;
			String TU_SOLAR_MCAE 			= null;
			String TU_SOLAR_SaleCloudAE 	= null;
			String TU_SOLAR_ServiceCloudAE	= null;
			String TU_SOLAR_PardotAE 		= null;
			String TU_SOLAR_MCRM 			= null;	
			String TU_SOLAR_MCSAE			= null;	
			String TU_SOLAR_AnalyticsAE 	= null;
			String TU_SOLAR_HoldOutUser		= null;

			String TerritoryUsageTypeId	= commonAPI.getIdByName(connection, "Territory_Usage_Type__c ", "Sales");
			String TeamRoleId	= null;


			//Core AE
			TeamRoleId	= null;
			TeamRoleId 	= commonAPI.getIdByName(connection, "Team_Role__c ", "Core AE");
			String TU_SOLAR_COREAE_Query = "SELECT Id FROM Team_User__c WHERE Team_User_id__c = '"+SOLAR_COREAE+"' AND Team_Role_Id__c = '"+TeamRoleId+"'AND Territory_Usage_Type__c = '"+TerritoryUsageTypeId+"'";
			SObject[] TU_SOLAR_COREAE_SObject = ApiUtilities.executeQuery(connection, TU_SOLAR_COREAE_Query);
			if(TU_SOLAR_COREAE_SObject.length >= 1){
				TU_SOLAR_COREAE = (String)TU_SOLAR_COREAE_SObject[0].getField("Id");
				LOG.info(" TU_SOLAR_COREAE	Already Present   : "+TU_SOLAR_COREAE);
			}else{
				TU_SOLAR_COREAE = userAPI.createTeamUser(connection, SOLAR_COREAE, TeamRoleId, TerritoryUsageTypeId);
				LOG.info(" TU_SOLAR_COREAE   : "+TU_SOLAR_COREAE);
			}


			//MC AE
			TeamRoleId	= null;
			TeamRoleId 	= commonAPI.getIdByName(connection, "Team_Role__c ", "MC AE");
			String TU_SOLAR_MCAE_Query = "SELECT Id FROM Team_User__c WHERE Team_User_id__c = '"+SOLAR_MCAE+"' AND Team_Role_Id__c = '"+TeamRoleId+"'AND Territory_Usage_Type__c = '"+TerritoryUsageTypeId+"'";
			SObject[] TU_SOLAR_MCAE_SObject = ApiUtilities.executeQuery(connection, TU_SOLAR_MCAE_Query);
			if(TU_SOLAR_MCAE_SObject.length >= 1){
				TU_SOLAR_MCAE = (String)TU_SOLAR_MCAE_SObject[0].getField("Id");
				LOG.info(" TU_SOLAR_MCAE	Already Present   : "+TU_SOLAR_MCAE);
			}else{
				TU_SOLAR_MCAE = userAPI.createTeamUser(connection, SOLAR_MCAE, TeamRoleId, TerritoryUsageTypeId);
				LOG.info(" TU_SOLAR_MCAE   : "+TU_SOLAR_MCAE);
			}	


			//Sales Cloud AE
			TeamRoleId	= null;
			TeamRoleId 	= commonAPI.getIdByName(connection, "Team_Role__c ", "Sales Cloud AE");
			String TU_SOLAR_SaleCloudAE_Query = "SELECT Id FROM Team_User__c WHERE Team_User_id__c = '"+SOLAR_SaleCloudAE+"' AND Team_Role_Id__c = '"+TeamRoleId+"'AND Territory_Usage_Type__c = '"+TerritoryUsageTypeId+"'";
			SObject[] TU_SOLAR_SaleCloudAE_SObject = ApiUtilities.executeQuery(connection, TU_SOLAR_SaleCloudAE_Query);
			if(TU_SOLAR_SaleCloudAE_SObject.length >= 1){
				TU_SOLAR_SaleCloudAE = (String)TU_SOLAR_SaleCloudAE_SObject[0].getField("Id");
				LOG.info(" TU_SOLAR_SaleCloudAE	Already Present   : "+TU_SOLAR_SaleCloudAE);
			}else{
				TU_SOLAR_SaleCloudAE = userAPI.createTeamUser(connection, SOLAR_SaleCloudAE, TeamRoleId, TerritoryUsageTypeId);
				LOG.info(" TU_SOLAR_SaleCloudAE   : "+TU_SOLAR_SaleCloudAE);
			}

			//Services Cloud AE
			TeamRoleId	= null;
			TeamRoleId 	= commonAPI.getIdByName(connection, "Team_Role__c ", "Service Cloud AE");
			String TU_SOLAR_ServiceCloudAE_Query = "SELECT Id FROM Team_User__c WHERE Team_User_id__c = '"+SOLAR_ServiceCloudAE+"' AND Team_Role_Id__c = '"+TeamRoleId+"'AND Territory_Usage_Type__c = '"+TerritoryUsageTypeId+"'";
			SObject[] TU_SOLAR_ServiceCloudAE_SObject = ApiUtilities.executeQuery(connection, TU_SOLAR_ServiceCloudAE_Query);
			if(TU_SOLAR_ServiceCloudAE_SObject.length >= 1){
				TU_SOLAR_ServiceCloudAE = (String)TU_SOLAR_ServiceCloudAE_SObject[0].getField("Id");
				LOG.info(" TU_SOLAR_ServiceCloudAE	Already Present   : "+TU_SOLAR_ServiceCloudAE);
			}else{
				TU_SOLAR_ServiceCloudAE = userAPI.createTeamUser(connection, SOLAR_ServiceCloudAE, TeamRoleId, TerritoryUsageTypeId);
				LOG.info(" TU_SOLAR_ServiceCloudAE   : "+TU_SOLAR_ServiceCloudAE);
			}

			//Pardot AE
			TeamRoleId	= null;
			TeamRoleId 	= commonAPI.getIdByName(connection, "Team_Role__c ", "Pardot AE");
			String TU_SOLAR_PardotAE_Query = "SELECT Id FROM Team_User__c WHERE Team_User_id__c = '"+SOLAR_PardotAE+"' AND Team_Role_Id__c = '"+TeamRoleId+"'AND Territory_Usage_Type__c = '"+TerritoryUsageTypeId+"'";
			SObject[] TU_SOLAR_PardotAE_SObject = ApiUtilities.executeQuery(connection, TU_SOLAR_PardotAE_Query);
			if(TU_SOLAR_PardotAE_SObject.length >= 1){
				TU_SOLAR_PardotAE = (String)TU_SOLAR_PardotAE_SObject[0].getField("Id");
				LOG.info(" TU_SOLAR_PardotAE	Already Present   : "+TU_SOLAR_PardotAE);
			}else{
				TU_SOLAR_PardotAE = userAPI.createTeamUser(connection, SOLAR_PardotAE, TeamRoleId, TerritoryUsageTypeId);
				LOG.info(" TU_SOLAR_PardotAE   : "+TU_SOLAR_PardotAE);
			}

			//MC RM
			TeamRoleId	= null;
			TeamRoleId 	= commonAPI.getIdByName(connection, "Team_Role__c ", "MC RM");
			String TU_SOLAR_MCRM_Query = "SELECT Id FROM Team_User__c WHERE Team_User_id__c = '"+SOLAR_MCRM+"' AND Team_Role_Id__c = '"+TeamRoleId+"'AND Territory_Usage_Type__c = '"+TerritoryUsageTypeId+"'";
			SObject[] TU_SOLAR_MCRM_SObject = ApiUtilities.executeQuery(connection, TU_SOLAR_MCRM_Query);
			if(TU_SOLAR_MCRM_SObject.length >= 1){
				TU_SOLAR_MCRM = (String)TU_SOLAR_MCRM_SObject[0].getField("Id");
				LOG.info(" TU_SOLAR_MCRM	Already Present   : "+TU_SOLAR_MCRM);
			}else{
				TU_SOLAR_MCRM = userAPI.createTeamUser(connection, SOLAR_MCRM, TeamRoleId, TerritoryUsageTypeId);
				LOG.info(" TU_SOLAR_MCRM   : "+TU_SOLAR_MCRM);
			}			

			//MC Services AE
			TeamRoleId	= null;
			TeamRoleId 	= commonAPI.getIdByName(connection, "Team_Role__c ", "MC Services AE");
			String TU_SOLAR_MCSAE_Query = "SELECT Id FROM Team_User__c WHERE Team_User_id__c = '"+SOLAR_MCSAE+"' AND Team_Role_Id__c = '"+TeamRoleId+"'AND Territory_Usage_Type__c = '"+TerritoryUsageTypeId+"'";
			SObject[] TU_SOLAR_MCSAE_SObject = ApiUtilities.executeQuery(connection, TU_SOLAR_MCSAE_Query);
			if(TU_SOLAR_MCSAE_SObject.length >= 1){
				TU_SOLAR_MCSAE = (String)TU_SOLAR_MCSAE_SObject[0].getField("Id");
				LOG.info(" TU_SOLAR_MCSAE	Already Present   : "+TU_SOLAR_MCSAE);
			}else{
				TU_SOLAR_MCSAE = userAPI.createTeamUser(connection, SOLAR_MCSAE, TeamRoleId, TerritoryUsageTypeId);
				LOG.info(" TU_SOLAR_MCSAE   : "+TU_SOLAR_MCSAE);
			}

			//Analytics AE
			TeamRoleId	= null;
			TeamRoleId 	= commonAPI.getIdByName(connection, "Team_Role__c ", "Analytics AE");
			String TU_SOLAR_AnalyticsAE_Query = "SELECT Id FROM Team_User__c WHERE Team_User_id__c = '"+SOLAR_AnalyticsAE+"' AND Team_Role_Id__c = '"+TeamRoleId+"'AND Territory_Usage_Type__c = '"+TerritoryUsageTypeId+"'";
			SObject[] TU_SOLAR_AnalyticsAE_SObject = ApiUtilities.executeQuery(connection, TU_SOLAR_AnalyticsAE_Query);
			if(TU_SOLAR_AnalyticsAE_SObject.length >= 1){
				TU_SOLAR_AnalyticsAE = (String)TU_SOLAR_AnalyticsAE_SObject[0].getField("Id");
				LOG.info(" TU_SOLAR_AnalyticsAE	Already Present   : "+TU_SOLAR_AnalyticsAE);
			}else{
				TU_SOLAR_AnalyticsAE = userAPI.createTeamUser(connection, SOLAR_AnalyticsAE, TeamRoleId, TerritoryUsageTypeId);
				LOG.info(" TU_SOLAR_AnalyticsAE   : "+TU_SOLAR_AnalyticsAE);
			}

			//Analytics AE
			TeamRoleId	= null;
			TeamRoleId 	= commonAPI.getIdByName(connection, "Team_Role__c ", "Steelbrick AE");
			String TU_SOLAR_HoldOutUser_Query = "SELECT Id FROM Team_User__c WHERE Team_User_id__c = '"+SOLAR_HoldOutUser+"' AND Team_Role_Id__c = '"+TeamRoleId+"'AND Territory_Usage_Type__c = '"+TerritoryUsageTypeId+"'";
			SObject[] TU_SOLAR_HoldOutUser_SObject = ApiUtilities.executeQuery(connection, TU_SOLAR_HoldOutUser_Query);
			if(TU_SOLAR_HoldOutUser_SObject.length >= 1){
				TU_SOLAR_HoldOutUser = (String)TU_SOLAR_HoldOutUser_SObject[0].getField("Id");
				LOG.info(" TU_SOLAR_HoldOutUser	Already Present   : "+TU_SOLAR_HoldOutUser);
			}else{
				TU_SOLAR_HoldOutUser = userAPI.createTeamUser(connection, SOLAR_HoldOutUser, TeamRoleId, TerritoryUsageTypeId);
				LOG.info(" TU_SOLAR_HoldOutUser   : "+TU_SOLAR_HoldOutUser);
			}
			LOG.info("*********************************************************************");
			
			//Get Prefix value from Team Role.
			String Prefix_CoreAE 			= getPrefixValue(connection, "Core AE");
			String Prefix_MCAE 				= getPrefixValue(connection, "MC AE");
			String Prefix_SalesCloudAE 		= getPrefixValue(connection, "Sales Cloud AE");
			String Prefix_ServiceCloudAE	= getPrefixValue(connection, "Service Cloud AE");
			String Prefix_PardotAE 			= getPrefixValue(connection, "Pardot AE");
			String Prefix_MCRM 				= getPrefixValue(connection, "MC RM");
			String Prefix_McServicesAE 		= getPrefixValue(connection, "MC Services AE");
			String Prefix_AnalyticsAE 		= getPrefixValue(connection, "Analytics AE");
			String Prefix_SteelbrickAE 		= getPrefixValue(connection, "Steelbrick AE");
			
			LOG.info("*********************************************************************");

			//Create Territories
			String SOLAR_Terr_Core1 				= createTerritory(connection, "Core AE", Prefix_CoreAE+"SOLAR-Core1", "Sales");
			String SOLAR_Terr_MCAE1 				= createTerritory(connection, "MC AE", Prefix_MCAE+"SOLAR-MCAE1", "Sales");
			String SOLAR_Terr_SalesCloudAE1 		= createTerritory(connection, "Sales Cloud AE", Prefix_SalesCloudAE+"SOLAR-SalesCloudAE1", "Sales");
			String SOLAR_Terr_ServiceCloudAE1 		= createTerritory(connection, "Service Cloud AE", Prefix_ServiceCloudAE+"SOLAR-ServiceCloudAE1", "Sales");			
			String SOLAR_Terr_PardotAE1 			= createTerritory(connection, "Pardot AE", Prefix_PardotAE+"SOLAR-PardotAE1", "Sales");
			String SOLAR_Terr_MCRM1 				= createTerritory(connection, "MC RM", Prefix_MCRM+"SOLAR-MCRM1", "Sales");
			String SOLAR_Terr_MCServicesAE1 		= createTerritory(connection, "MC Services AE", Prefix_McServicesAE+"SOLAR-MCServicesAE1", "Sales");
			String SOLAR_Terr_AnalyticsAE1 			= createTerritory(connection, "Analytics AE", Prefix_AnalyticsAE+"SOLAR-AnalyticsAE1", "Sales");
			String SOLAR_Terr_SteelbrickAE1 		= createTerritory(connection, "Steelbrick AE", Prefix_SteelbrickAE+"SOLAR-SteelbrickAE1", "Sales");
			
			LOG.info("*********************************************************************");
			
			//Sales Profile Creation
			String SOLAR_COREAE_SP 			= createSalesProfile(connection, SOLAR_COREAE, "Core AE");
			String SOLAR_MCAE_SP 			= createSalesProfile(connection, SOLAR_MCAE, "MC AE");
			String SOLAR_SaleCloudAE_SP 	= createSalesProfile(connection, SOLAR_SaleCloudAE, "Sales Cloud AE");
			String SOLAR_ServiceCloudAE_SP	= createSalesProfile(connection, SOLAR_ServiceCloudAE, "Service Cloud AE");
			String SOLAR_PardotAE_SP 		= createSalesProfile(connection, SOLAR_PardotAE, "Pardot AE");
			String SOLAR_MCRM_SP 			= createSalesProfile(connection, SOLAR_MCRM, "MC RM");	
			String SOLAR_MCSAE_SP			= createSalesProfile(connection, SOLAR_MCSAE, "MC Services AE");	
			String SOLAR_AnalyticsAE_SP 	= createSalesProfile(connection, SOLAR_AnalyticsAE, "Analytics AE");
			String SOLAR_HoldOutUser_SP		= createSalesProfile(connection, SOLAR_HoldOutUser, "Steelbrick AE");			

			LOG.info("*********************************************************************");
			
			//Territory Resource Creation
			String SOLAR_TR_COREAE 			= createTerritoryResources(connection, "SOLAR-TR-CoreAE", SOLAR_Terr_Core1, TU_SOLAR_COREAE, SOLAR_COREAE_SP);
			String SOLAR_TR_MCAE 			= createTerritoryResources(connection, "SOLAR-TR-MCAE", SOLAR_Terr_MCAE1, TU_SOLAR_MCAE, SOLAR_MCAE_SP);
			String SOLAR_TR_SaleCloudAE 	= createTerritoryResources(connection, "SOLAR-TR-SalesCloudAE", SOLAR_Terr_SalesCloudAE1, TU_SOLAR_SaleCloudAE, SOLAR_SaleCloudAE_SP);
			String SOLAR_TR_ServiceCloudAE	= createTerritoryResources(connection, "SOLAR-TR-ServiceCloudAE", SOLAR_Terr_ServiceCloudAE1, TU_SOLAR_ServiceCloudAE, SOLAR_ServiceCloudAE_SP);
			String SOLAR_TR_PardotAE 		= createTerritoryResources(connection, "SOLAR-TR-PardotAE", SOLAR_Terr_PardotAE1, TU_SOLAR_PardotAE, SOLAR_PardotAE_SP);
			String SOLAR_TR_MCRM 			= createTerritoryResources(connection, "SOLAR-TR-MCRM", SOLAR_Terr_MCRM1, TU_SOLAR_MCRM, SOLAR_MCRM_SP);	
			String SOLAR_TR_MCSAE			= createTerritoryResources(connection, "SOLAR-TR-MCServicesAE", SOLAR_Terr_MCServicesAE1, TU_SOLAR_MCSAE, SOLAR_MCSAE_SP);	
			String SOLAR_TR_AnalyticsAE 	= createTerritoryResources(connection, "SOLAR-TR-AnalyticsAE", SOLAR_Terr_AnalyticsAE1, TU_SOLAR_AnalyticsAE, SOLAR_AnalyticsAE_SP);
			String SOLAR_TR_HoldOutUser		= createTerritoryResources(connection, "SOLAR-TR-SteelbrickAE", SOLAR_Terr_SteelbrickAE1, TU_SOLAR_HoldOutUser, SOLAR_HoldOutUser_SP);

			LOG.info("*********************************************************************");
			
			//Update SOLAR_Terr_Core1
			String Core_FollowerRole = null;
			
			//Core_FollowerRole = "SOLAR-SalesCloudAE1;SOLAR-ServiceCloudAE1;SOLAR-PardotAE1;SOLAR-AnalyticsAE1;SOLAR-SteelbrickAE1";
			Core_FollowerRole = commonAPI.getFieldValue(connection, "Territory__c", SOLAR_Terr_SalesCloudAE1, "Name")+";";
			Core_FollowerRole = Core_FollowerRole+ commonAPI.getFieldValue(connection, "Territory__c", SOLAR_Terr_ServiceCloudAE1, "Name")+";";
			Core_FollowerRole = Core_FollowerRole+ commonAPI.getFieldValue(connection, "Territory__c", SOLAR_Terr_PardotAE1, "Name")+";";
			Core_FollowerRole = Core_FollowerRole+ commonAPI.getFieldValue(connection, "Territory__c", SOLAR_Terr_AnalyticsAE1, "Name")+";";
			Core_FollowerRole = Core_FollowerRole+ commonAPI.getFieldValue(connection, "Territory__c", SOLAR_Terr_SteelbrickAE1, "Name")+";";
			commonAPI.updateField(connection, "Territory__c", SOLAR_Terr_Core1, "Follower_Role__c", Core_FollowerRole);

			LOG.info("*********************************************************************");
			
			//SOLAR_Terr_MCAE1
			String MC_FollowerRole = null;
			//MC_FollowerRole = "SOLAR-MCRM1;SOLAR-MCServicesAE1";
			MC_FollowerRole = commonAPI.getFieldValue(connection, "Territory__c", SOLAR_Terr_MCRM1, "Name")+";";
			MC_FollowerRole = MC_FollowerRole+ commonAPI.getFieldValue(connection, "Territory__c", SOLAR_Terr_MCServicesAE1, "Name")+";";
			commonAPI.updateField(connection, "Territory__c", SOLAR_Terr_MCAE1, "Follower_Role__c", MC_FollowerRole);

			LOG.info("*********************************************************************");
			String SRRateConfigUpdate = "false";
			SRRateConfigUpdate = System.getProperty("SRRateConfigUpdate");
			System.out.println("SRRateConfigUpdate Value = "+SRRateConfigUpdate);
			if(SRRateConfigUpdate.equals("true")){
			//SR Rate Config
			String SOLAR_SRRC_COREAE 		= setSRRateConfig(connection, "Core AE");
			String SOLAR_SRRC_MCAE 			= setSRRateConfig(connection, "MC AE");
			String SOLAR_SRRC_SaleCloudAE 		= setSRRateConfig(connection, "Sales Cloud AE");
			String SOLAR_SRRC_ServiceCloudAE	= setSRRateConfig(connection, "Service Cloud AE");
			String SOLAR_SRRC_PardotAE 		= setSRRateConfig(connection, "Pardot AE");
			String SOLAR_SRRC_MCRM 			= setSRRateConfig(connection, "MC RM");	
			String SOLAR_SRRC_MCSAE			= setSRRateConfig(connection, "MC Services AE");	
			String SOLAR_SRRC_AnalyticsAE 		= setSRRateConfig(connection, "Analytics AE");
			String SOLAR_SRRC_HoldOutUser		= setSRRateConfig(connection, "Steelbrick AE");
			String SOLAR_SRRC_SalesPerson		= setSRRateConfig(connection, "Salesperson");
			}
			LOG.info("*********************************************************************");
			LOG.info("**********  Set Salesforce Team Process - End   *********************");
			LOG.info("*********************************************************************");
			
			
		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return null;
	}


	public String createTerritory(PartnerConnection connection, String TerritoryClassification, String TerritoryName, String UsageType) throws InterruptedException{

		try{
			String TerritoryId = null;
			//Account Info
			Map<String,Object> AccountInfoMap = dataMap.get("GlobalAccount");
			Object NumberOfEmployees	= AccountInfoMap.get("NumberOfEmployees");
			Object ShippingCountry		= AccountInfoMap.get("ShippingCountry");
			Object ShippingState		= AccountInfoMap.get("ShippingState");
			Object ShippingCity			= AccountInfoMap.get("ShippingCity");

			//Get Territory Usage Type Id
			String TerritoryUsageTypeId	= commonAPI.getIdByName(connection, "Territory_Usage_Type__c ", UsageType);

			//CORE AE Setup
			//Check, Is SOLAR-Core1 Territory Present
			String Territory_Query = "SELECT Id FROM Territory__c WHERE Name = '"+TerritoryName+"'";
			SObject[] Territory_SObject = ApiUtilities.executeQuery(connection, Territory_Query);

			if(Territory_SObject.length >= 1){
				TerritoryId = (String)Territory_SObject[0].getField("Id");
				LOG.info(TerritoryName+" Territory already Present -   : "+TerritoryId);
				return TerritoryId;
			}else{
				String SOLAR_MasterCarve_Id = null;

				if(TerritoryClassification=="Core AE" || TerritoryClassification=="MC AE"){
					//Check 'SOLAR-MasterCarve' is present
					String SOLAR_MC_Query = "SELECT Id FROM Master_Carve__c WHERE Name = 'SOLAR-MasterCarve'";
					SObject[] SOLAR_MC_SObject = ApiUtilities.executeQuery(connection, SOLAR_MC_Query);	
					if(SOLAR_MC_SObject.length >= 1){
						//Get Master Carve Key Id
						SOLAR_MasterCarve_Id = (String)SOLAR_MC_SObject[0].getField("Id");
						LOG.info("SOLAR_MasterCarve_Id Already Present   : "+SOLAR_MasterCarve_Id);
					}else{	
						//Create Master Carve
						Map<String,Object> MasterCarveMap = dataMap.get("Master_Carve__c");				
						MasterCarveMap.put("Name", "SOLAR-MasterCarve");
						MasterCarveMap.put("Countries__c", "US");	
						MasterCarveMap.put("CurrencyIsoCode", "USD");
						MasterCarveMap.put("Order__c", "1");	
						MasterCarveMap.put("Account_Rule_Exists__c", true);
						if(TerritoryClassification.equals("Core AE") || TerritoryClassification.equals("MC AE")){
							MasterCarveMap.put("Master_Carve_Rule_Field__c", "( NumberOfEmployees == "+NumberOfEmployees+" ) AND ( ShippingCountry == "+ShippingCountry+" ) AND ( ShippingState == "+ShippingState+" ) AND ( ShippingCity == "+ShippingCity+" )");
							MasterCarveMap.put("Master_Carve_Filter_Logic__c", "1 AND 2 AND 3 AND 4");
						}
						SOLAR_MasterCarve_Id = ApiUtilities.createObject(connection,"Master_Carve__c", MasterCarveMap);
						LOG.info("SOLAR_MasterCarve_Id   : "+SOLAR_MasterCarve_Id);					
					}
				}

				//Create  Territory						
				Map<String,Object> TerritoryMap = dataMap.get("Territory__c");	
				TerritoryMap.put("Name", TerritoryName);				
				TerritoryMap.put("Territory_Usage_Type__c", TerritoryUsageTypeId);
				TerritoryMap.put("Territory_Classification__c", TerritoryClassification);				
				TerritoryMap.put("Status__c", "Active");				
				GregorianCalendar TodaytDate = new GregorianCalendar();
				TerritoryMap.put("Start_Date__c", TodaytDate.getTime()); //2017-07-17

				if(TerritoryClassification.equals("Core AE") || TerritoryClassification.equals("MC AE")){
					TerritoryMap.put("Master_Carve__c", SOLAR_MasterCarve_Id);
				}else{
					TerritoryMap.put("Follower__c", true);
				}

				TerritoryId = ApiUtilities.createObject(connection,"Territory__c", TerritoryMap);
				LOG.info("Territory Id for "+TerritoryName+" : "+TerritoryId);	

				if(TerritoryClassification.equals("Core AE") || TerritoryClassification.equals("MC AE")){
					//Create Territory Definitions-1					
					Map<String,Object> TerritoryDefinitionMap = dataMap.get("Territory_Definition__c");	
					TerritoryDefinitionMap.put("Territory__c", TerritoryId);
					TerritoryDefinitionMap.put("Type__c", "City__c");				
					TerritoryDefinitionMap.put("Type_Value__c", ShippingCity);		
					TerritoryDefinitionMap.put("CurrencyIsoCode ", "USD");	
					String SOLAR_Core1_TerritoryDefinition_Id = ApiUtilities.createObject(connection,"Territory_Definition__c", TerritoryDefinitionMap);
					LOG.info("Territory Definition-1 -Id for SOLAR_Core1 : "+SOLAR_Core1_TerritoryDefinition_Id);	

					//Create Territory Definitions-2
					TerritoryDefinitionMap.clear();	
					TerritoryDefinitionMap.put("Territory__c", TerritoryId);	
					TerritoryDefinitionMap.put("Type__c", "State__c");				
					TerritoryDefinitionMap.put("Type_Value__c", ShippingState);	
					TerritoryDefinitionMap.put("CurrencyIsoCode ", "USD");	
					SOLAR_Core1_TerritoryDefinition_Id = ApiUtilities.createObject(connection,"Territory_Definition__c", TerritoryDefinitionMap);
					LOG.info("Territory Definition-2 -Id for SOLAR_Core1 : "+SOLAR_Core1_TerritoryDefinition_Id);

				}
				return TerritoryId;
			}
		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return null;
	}

	public String createSalesProfile(PartnerConnection connection, String UserId, String TeamRole) throws InterruptedException{

		try{
			String SalesProfileId = null;
			String EmployeeId = null;

			//String SalesProfile_Query = "SELECT Id FROM SalesProfile__c WHERE User__c = '"+UserId+"' AND TeamRole__c = '"+TeamRole+"'";
			String SalesProfile_Query = "SELECT Id FROM SalesProfile__c WHERE User__c = '"+UserId+"'";
			SObject[] SalesProfile_SObject = ApiUtilities.executeQuery(connection, SalesProfile_Query);	
			if(SalesProfile_SObject.length >= 1){
				//Get Master Carve Key Id
				SalesProfileId = (String)SalesProfile_SObject[0].getField("Id");
				LOG.info("User "+UserId+" SalesProfileId Already Present   : "+SalesProfileId);
			}else{

				EmployeeId = commonAPI.getFieldValue(connection, "User", UserId, "sf62user__Employee_Number__c");
				Map<String,Object> SalesProfileMap = dataMap.get("SalesProfile__c");
				SalesProfileMap.clear();	
				SalesProfileMap.put("EmployeeId__c", EmployeeId);	
				SalesProfileMap.put("TeamRole__c", TeamRole);				
				SalesProfileMap.put("TerritoryAnalysisStatus__c", "Completed");	

				GregorianCalendar TodaytDate = new GregorianCalendar();
				SalesProfileMap.put("StartDate__c ", TodaytDate.getTime());
				SalesProfileMap.put("WorkdayEffectiveDate__c ", TodaytDate.getTime());

				SalesProfileMap.put("IsCommissionable__c ", true);
				SalesProfileMap.put("User__c ", UserId);
				SalesProfileMap.put("AttributesChangedInWorkday__c ", "Approved");			

				SalesProfileId = ApiUtilities.createObject(connection,"SalesProfile__c", SalesProfileMap);
				LOG.info("User "+UserId+" SalesProfileId : "+SalesProfileId);
			}

			return SalesProfileId;			
		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return null;
	}

	public String createTerritoryResources(PartnerConnection connection, String TR_Name, String Territory, String TerritoryUser, String SalesProfile) throws InterruptedException{

		try{
			String TerritoryResourcesId = null;
			String TerritoryResources_Query = "SELECT "
					+ "Id "
					+ "FROM Territory_Resources__c "
					+ "WHERE Name = '"+TR_Name+"' "
					+ "AND Territory__c = '"+Territory+"' "
					+ "AND SalesProfile__c = '"+SalesProfile+"' "
					+ "AND Status__c = 'Active'";

			SObject[] TerritoryResources_SObject = ApiUtilities.executeQuery(connection, TerritoryResources_Query);	
			if(TerritoryResources_SObject.length >= 1){
				TerritoryResourcesId = (String)TerritoryResources_SObject[0].getField("Id");
				LOG.info(" : "+TR_Name+" Territory Resources Already Present   : "+TerritoryResourcesId);
			}else{

				String AssignmentRuleId = commonAPI.getIdByName(connection, "Team_Assignment_Rule__c", "Always Assign Rule");
				Map<String,Object> TerritoryResourcesMap = dataMap.get("Territory_Resources__c");
				TerritoryResourcesMap.clear();	
				TerritoryResourcesMap.put("Name", TR_Name);	
				TerritoryResourcesMap.put("Territory__c", Territory);				
				TerritoryResourcesMap.put("Territory_User__c", TerritoryUser);	
				TerritoryResourcesMap.put("Assignment_Rule__c", AssignmentRuleId);
				TerritoryResourcesMap.put("Status__c ", "Active");
				//TerritoryResourcesMap.put("SalesProfile__c ", SalesProfile); //Blocked this line, Confirmed by Jeyanthi from ADM Team
				//TerritoryResourcesMap.put("cascade_to_child__c ", true);  //Blocked this line, Confirmed by Jeyanthi from ADM Team
				GregorianCalendar TodaytDate = new GregorianCalendar();
				TerritoryResourcesMap.put("Start_Date__c ", TodaytDate.getTime());				

				if(TR_Name.equals("SOLAR-TR-CoreAE") || TR_Name.equals("SOLAR-TR-MCAE")){ 
					TerritoryResourcesMap.put("Seq__c ", "1");
				}else{
					TerritoryResourcesMap.put("Seq__c ", "2");
				}		

				TerritoryResourcesId = ApiUtilities.createObject(connection,"Territory_Resources__c", TerritoryResourcesMap);
				LOG.info(" : "+TR_Name+" Territory Resources Id : "+TerritoryResourcesId);
			}

			return TerritoryResourcesId;			
		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return null;
	}

	public String setSRRateConfig(PartnerConnection connection, String SellingRole) throws InterruptedException{

		try{
			String TSRRateConfigId 	= null;

			String SellingRoleId   	= commonAPI.getIdByName(connection, "Team_Role__c", SellingRole);
			String StartDate		= null;
			String EndDate			= null;
			String Min				= null;
			String Max				= null;

			String TSRRateConfigWithTrue_Query = "SELECT Id, "
					+ "Active__c, "
					+ "SellingRole__c,"
					+ "EffectiveStartDate__c,"
					+ "EffectiveEndDate__c,"
					+ "TSRMinValue__c,"
					+ "TSRMaxValue__c"
					+ " FROM TSRRateConfig__c  WHERE SellingRole__c = '"+SellingRoleId+"' AND Active__c = "+true+"";

			SObject[] TSRRateConfigWithTrue_SObject = ApiUtilities.executeQuery(connection, TSRRateConfigWithTrue_Query);	

			if(TSRRateConfigWithTrue_SObject.length >= 1){
				TSRRateConfigId	= (String) TSRRateConfigWithTrue_SObject[0].getField("Id");

				Map<String,Object> SRRateConfigMap = dataMap.get("TSRRateConfig__c");
				SRRateConfigMap.clear();	
				SRRateConfigMap.put("TSRMinValue__c ", "100.00");
				SRRateConfigMap.put("TSRMaxValue__c ", "100.00");
				Calendar S_Date = new GregorianCalendar();
				Calendar E_Date = new GregorianCalendar();
				S_Date.add(Calendar.MONTH, -1);
				E_Date.add(Calendar.YEAR, 1);
				SRRateConfigMap.put("EffectiveStartDate__c ", S_Date.getTime());	
				SRRateConfigMap.put("EffectiveEndDate__c ", E_Date.getTime());
				boolean UpdateStatus = ApiUtilities.updateObjectAndReturnValue(connection, "TSRRateConfig__c", TSRRateConfigId, SRRateConfigMap);
				LOG.info(" TSRRateConfig__c Update : "+UpdateStatus);
			}else{
				Map<String,Object> SRRateConfigMap = dataMap.get("TSRRateConfig__c");
				SRRateConfigMap.clear();
				SRRateConfigMap.put("SellingRole__c", SellingRoleId);
				SRRateConfigMap.put("TSRMinValue__c", "100.00");
				SRRateConfigMap.put("TSRMaxValue__c", "100.00");
				Calendar S_Date = new GregorianCalendar();
				Calendar E_Date = new GregorianCalendar();
				S_Date.add(Calendar.MONTH, -1);
				E_Date.add(Calendar.YEAR, 1);
				SRRateConfigMap.put("EffectiveStartDate__c", S_Date.getTime());	
				SRRateConfigMap.put("EffectiveEndDate__c", E_Date.getTime());
				TSRRateConfigId = ApiUtilities.createObject(connection,"TSRRateConfig__c", SRRateConfigMap);
				LOG.info(" TSR Rate Config Id : "+TSRRateConfigId);
			}
			return TSRRateConfigId;			
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
			SOLAR_User = userAPI.createUser(connection, Fname, Lname, EmailId, Alias, ProfileName, MarketSegment, EmpId);
			LOG.info(ProfileName+" SOLAR_User : "+SOLAR_User);
		}
		return SOLAR_User;
	}
	
	public String getPrefixValue(PartnerConnection custom_connection,String TeamRoleName) throws InterruptedException{

		try{
			String FieldValueRecordId = null;
			String FieldValueId_Query = "SELECT Prefix__c FROM Team_Role__c WHERE Name = '"+TeamRoleName+"'";
			SObject[] FieldValueId_SObject = ApiUtilities.executeQuery(custom_connection, FieldValueId_Query);
			FieldValueRecordId = (String)FieldValueId_SObject[0].getField("Prefix__c");
						
			if(FieldValueRecordId == null){
				FieldValueRecordId = "";
			}else{
				FieldValueRecordId = FieldValueRecordId+"_";
			}
			
			LOG.info(TeamRoleName+" Prefix__c : "+FieldValueRecordId);
			return FieldValueRecordId;

		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return null;
	}
}
