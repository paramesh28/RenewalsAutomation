package com.salesforce.automation.commonAPI;

import com.salesforce.automation.beans.OpportunityProductSummaryBean;
import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.TestDataProvider;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.bind.XmlObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;


public class OpportunityAPI {
	
	private static final Logger LOG = LoggerFactory.getLogger(OpportunityAPI.class);
	protected static Map<String, Map<String, Object>> dataMap;
	final static private String TEST_DATA = "./testdata/Opportunity.xml";
	
	PageObjectApi 	pageObjectApi 	= new PageObjectApi();
	CommonAPI commonAPI = new CommonAPI();
	ApiUtilities 	apiUtilities	= new ApiUtilities();
	OpportunityProductSummaryBean opportunityProductSummaryBean = new OpportunityProductSummaryBean();
	
	String currentUrl = null;
	String baseUrl = null;
	
	static String username = System.getProperty("testUsernameParam");
	static String password = System.getProperty("testPasswordParam");
		
	public PartnerConnection connection = apiUtilities.customLogin(username, password);
	
	
	//Create NBAO Opportunity
	public String createNBAOOpportunity(PartnerConnection custom_connection, 
										String AccountId, 
										String OpptyName, 
										String recordType,
										String Type, 
										String Currency,
										GregorianCalendar NBAO_CloseDate) throws ConnectionException, InterruptedException{
		
		String OpportunityId = null;
		String recordTypeId = commonAPI.getRecordTypeId(custom_connection, "Opportunity", recordType);
		
		
		dataMap = TestDataProvider.dataReader(TEST_DATA);
		Map<String,Object> NBAOOpportunityMap = dataMap.get("GlobalOpportunity");
		
		NBAOOpportunityMap.put("Name", OpptyName);
		NBAOOpportunityMap.put("AccountId", AccountId);
		NBAOOpportunityMap.put("RecordTypeId", recordTypeId);
		NBAOOpportunityMap.put("Type", Type);
		NBAOOpportunityMap.put("CloseDate", NBAO_CloseDate);
		NBAOOpportunityMap.put("CurrencyIsoCode", Currency);
		NBAOOpportunityMap.put("StageName", "01 - Identifying an Opportunity");
		NBAOOpportunityMap.put("CompetitiveStatus__c", "1 - Behind Competition");
		NBAOOpportunityMap.put("PrimaryCompetitor__c", "Cheetah/Experian");

		OpportunityId = ApiUtilities.createObject(custom_connection,"Opportunity", NBAOOpportunityMap);
		LOG.info("NBAO Opportunity Id   : "+OpportunityId);
		return OpportunityId;
	}
	
	/**
	 * Create Renewal Oppty from Contract Using API call
	 * @param connection
	 * @return RenewalOpptyId
	 **/
	public String CreateRenewalOppty(PartnerConnection connection, String ContractId) throws Exception{

		String RenewalOpptyId = null;
		commonAPI.updateField(connection, "Contract", ContractId, "sfbase__RenewalOpportunityIndicator__c", "Evaluate");
		commonAPI.updateField(connection, "Contract", ContractId, "sfbase__RenewalOptyCreationDays__c", "9999");
		
		//Create Renewal Oppty Apex Script
		String RenewalScript = "CreateRenewalOpptyProcess CreateProcess =new CreateRenewalOpptyProcess(); ";
		RenewalScript += "CreateProcess.createRenewalOppty(null, null, '5','"+ContractId+"');";
		
		ApiUtilities.runApex(RenewalScript);
		LOG.info("There Renewal Oppty on Contract has been created");
		
		RenewalOpptyId = GetRenewalOpptyId(connection, ContractId);
		LOG.info("There Renewal Oppty"+RenewalOpptyId);
		return RenewalOpptyId;
	}
	
	/**
	 * Get Renewal Oppty Id from Contract Using API call
	 * @param connection
	 * @return RenewalOpptyId
	 **/
	public String GetRenewalOpptyId(PartnerConnection connection, String ContractID) throws Exception{

		String RenewalOpptyId = null;
		String RenewalOpptyIdquery= "SELECT OpportunityId__c FROM OpportunityContract__c WHERE ContractId__c = '"+ContractID+"'";
		SObject[] RenewalOpptyIdSObject = ApiUtilities.executeQuery(connection, RenewalOpptyIdquery);
		RenewalOpptyId = (String)RenewalOpptyIdSObject[0].getField("OpportunityId__c");
		LOG.info("Renewal Oppty Id  is : "+RenewalOpptyId);
		Assert.assertNotNull("There is no Renewal Oppty on Contract, got null", RenewalOpptyId);
		return RenewalOpptyId;
	}
	
		/**
	 * Create OPSO on Renewal Oppty Using API call
	 * @param connection
	 * @param RenewalOpptyId
	 **/
	public void CreateOPSO(PartnerConnection connection, String RenewalOpptyId) throws Exception{

		//Create OPSO Apex Script
		int OPSOLinelength = 0;
		String RenewalOpptyScript="IRenewalService renewService = RenewalServiceImpl.getInstance('pmarina@salesforce.com');" +
				"renewService.createOpsos(new set<Id>{'"+RenewalOpptyId+"'});";
		apiUtilities.runApex(RenewalOpptyScript);
		String OPSO_ListQuery = "SELECT sfbase__PriorAnnualOrderValue__c FROM sfbase__OpportunityProductSummary__c WHERE sfbase__Opportunity__c = '"+RenewalOpptyId+"' and sfbase__PriorAnnualOrderValue__c != 0.0";
		SObject[] OPSO_ListQuerySObject = ApiUtilities.executeQuery(connection, OPSO_ListQuery);
		OPSOLinelength = OPSO_ListQuerySObject.length;
		if(OPSOLinelength == 0){
			LOG.info("OPSO not created");
			System.exit(0);
		}	
	}


	/**
	 * Verify Quote to Oppty Sync Using API Call
	 * @param connection
	 * @param opptyId
	 * @param quoteId
	 * @return opptyId
	 **/
	public String verifyQuoteToOpptySync(PartnerConnection connection, String opptyId, String quoteId) throws Exception{

		ArrayList<String> opptyLineItemID = new ArrayList();
		ArrayList<String> quoteLineID = new ArrayList();

		String opptyLineItemIdQuery="SELECT Id FROM OpportunityLineItem where OpportunityId='"+opptyId+"'";
		SObject[] opptyLineItemIdObject = ApiUtilities.executeQuery(connection, opptyLineItemIdQuery);
		for(int i=0;i<=opptyLineItemIdObject.length-1;i++){
			int opptyProductCount = (int)opptyLineItemIdObject.length;			
			String opptyLineItemId =(String)opptyLineItemIdObject[i].getField("Id");		

			String opptyProductIdQuery = "SELECT PriceBookEntry.Product2Id FROM OpportunityLineItem WHERE Id = '"+opptyLineItemId+"'";	
			SObject[] opptyProductIdObject = ApiUtilities.executeQuery(connection, opptyProductIdQuery);
			XmlObject test1 = opptyProductIdObject[0].getChild("PricebookEntry");
			XmlObject test2 = test1.getChild("Product2Id");
			String opptyProductId = (String)test2.getValue();

			String quoteLineOnOpptyLineQuery = "SELECT QuoteLine__c FROM OpportunityLineItem WHERE Id = '"+opptyLineItemId+"'";
			SObject[] quoteLineOnOpptyLineIdObject = ApiUtilities.executeQuery(connection, quoteLineOnOpptyLineQuery);
			String quoteLineOnOpptyLineId=(String)quoteLineOnOpptyLineIdObject[0].getField("QuoteLine__c");

			opptyLineItemID.add(quoteLineOnOpptyLineId);

			String quoteLineIdQuery = "SELECT Id FROM Apttus_Proposal__Proposal_Line_Item__c where Apttus_Proposal__Proposal__c = '"+quoteId+"' and Apttus_Proposal__Product__c = '"+opptyProductId+"' and SfdcSuppressBilling__c = 'No'";
			SObject[] quoteLineIdObject = ApiUtilities.executeQuery(connection, quoteLineIdQuery);
			
			for(int j=0;j<=quoteLineIdObject.length-1;j++){
				String quoteLineId=(String)quoteLineIdObject[j].getField("Id");
					
					if(!quoteLineID.contains(quoteLineId)){
						quoteLineID.add(quoteLineId);
						}
			}
		}

		//Validate Oppty Line with Quote Line
		LOG.info("Quote to Oppty Sync Validation Starts");
		for (String ExpectedProduct : quoteLineID) {
			if(opptyLineItemID.contains(ExpectedProduct)){
				LOG.info("QuotelineId ("+ExpectedProduct+") populated on corresponding OpptyLine ");
			}else{
				LOG.info("QuoteLineId ("+ExpectedProduct+") did not get populated on OpptyLine. ");
				System.exit(0);
			}
		}
		LOG.info("Quote to Oppty Sync Validation End");
		return opptyId;
	}
	
	/**
	 * Call Run Opportuniry process
	 * @param accountId
	 * @param OpptyId
	 **/
	public boolean runOpptyTeamProces(String OpptyId, String accountId) throws Exception {

		String markForOpptyTeamQuery = "SELECT sfbase__MarkForOpptyTeamCreate__c FROM Opportunity WHERE Id = '"+OpptyId+"'";
		SObject[] markForOpptyTeamObject = ApiUtilities.executeQuery(connection, markForOpptyTeamQuery);
		String markForOpptyTeam = (String)markForOpptyTeamObject[0].getField("sfbase__MarkForOpptyTeamCreate__c");
		LOG.info(" : Mark for Oppty Team status : "+markForOpptyTeam);

		if(markForOpptyTeam.equalsIgnoreCase("false"))
		{
			Assert.fail("Mark for Oppty Team status is false");
		}

		String OpptyTeamProcesScript = "OpptyTeamProcess.processOpptyTeamForSingleAccount('"+OpptyId+"');";
		boolean OpptyTeamProcess = ApiUtilities.runApexWithReturn(OpptyTeamProcesScript);
		LOG.info(" : Oppty Team Process : "+OpptyTeamProcess);

		Thread.sleep(60000);
		int OpptyTeamProcesStatus=0;
		String OpptyTeamProcesStatusquery= "SELECT Id FROM sfbase__OpportunityTeam__c WHERE sfbase__Opportunity__c  = '"+OpptyId+"' AND sfbase__EndDate__c = null";
		SObject[] OpptyTeamProcesStatusObject = ApiUtilities.executeQuery(connection, OpptyTeamProcesStatusquery);
		OpptyTeamProcesStatus = (int)OpptyTeamProcesStatusObject.length;		

		LOG.info(" : Oppty Team Process Count : "+OpptyTeamProcesStatus);
		if (OpptyTeamProcesStatus > 0){
			LOG.info(" : Oppty Team Created based on Oppty Team Process");		
		}else{
			LOG.info(" : Oppty Team Not Created");	
			System.exit(0);
		}

		ArrayList<String> sfTeamUserID = new ArrayList();
		ArrayList<String> opptyTeamUserID = new ArrayList();
		ArrayList<String> sfTeamID = new ArrayList();

		//Get Oppty Owner ID
		String opptyOwnerquery = "SELECT OwnerId FROM Opportunity WHERE Id = '"+OpptyId+"'";
		SObject[] opptyOwnerObject = ApiUtilities.executeQuery(connection, opptyOwnerquery);
		String opptyOwnerId =(String)opptyOwnerObject[0].getField("OwnerId");


		String opptyOwnerCVRquery = "SELECT sfbase__CompValidationRole__c,AccountFunction__c  FROM sfbase__OpportunityTeam__c WHERE sfbase__User__c = '"+opptyOwnerId+"' AND sfbase__Opportunity__c = '"+OpptyId+"'";
		SObject[] opptyOwnerCVRObject = ApiUtilities.executeQuery(connection, opptyOwnerCVRquery);
		String opptyOwnerCVR =(String)opptyOwnerCVRObject[0].getField("sfbase__CompValidationRole__c");
		String opptyOwnerAF =(String)opptyOwnerCVRObject[0].getField("AccountFunction__c");

		//Get all the SF Team
		String sfTeamQuery = "SELECT sfbase__User__c,Id  FROM sfbase__SalesforceTeam__c WHERE sfbase__Account__c = '"+accountId+"' and sfbase__TeamProcessExclude__c = false AND sfbase__EndDate__c  = null";
		SObject[]sfTeamObject = ApiUtilities.executeQuery(connection, sfTeamQuery);
		for(int i=0;i<=sfTeamObject.length-1;i++){
			String sfTeamUserId = (String)sfTeamObject[i].getField("sfbase__User__c");
			sfTeamUserID.add(sfTeamUserId);

			String sfTeamId = (String)sfTeamObject[i].getField("Id");

			sfTeamID.add(sfTeamId);
		}

		for(String ExpectedSFTeamId : sfTeamID){
			//CVR and AF SFTeam
			String sfTeamCVRAFQuery = "SELECT sfbase__CompValidationRole__c,Account_Function__c,sfbase__User__c  FROM sfbase__SalesforceTeam__c WHERE Id = '"+ExpectedSFTeamId+"' and sfbase__TeamProcessExclude__c = false";
			SObject[]sfTeamCVRAFObject = ApiUtilities.executeQuery(connection, sfTeamCVRAFQuery);

			String sfTeamCVR = (String)sfTeamCVRAFObject[0].getField("sfbase__CompValidationRole__c");			
			String sfTeamAF = (String)sfTeamCVRAFObject[0].getField("Account_Function__c");
			String sfTeamUserIdSameAsOpptyOwner = (String)sfTeamCVRAFObject[0].getField("sfbase__User__c");

			if((StringUtils.equals(sfTeamCVR, opptyOwnerCVR))&&(StringUtils.equals(sfTeamAF, opptyOwnerAF))&&(sfTeamUserID.contains(sfTeamUserIdSameAsOpptyOwner))){
				sfTeamUserID.remove(sfTeamUserIdSameAsOpptyOwner);
			}
		}

		//Add oppty owner to expected opptyteam list
		sfTeamUserID.add(opptyOwnerId);

		//Get Oppty Team
		String opptyTeamQuery = "SELECT Id,sfbase__User__c FROM sfbase__OpportunityTeam__c WHERE sfbase__Opportunity__c  = '"+OpptyId+"' AND sfbase__EndDate__c = null";
		SObject[]opptyTeamObject = ApiUtilities.executeQuery(connection, opptyTeamQuery);
		for(int j=0;j<=opptyTeamObject.length-1;j++){

			String opptyTeamId = (String)opptyTeamObject[j].getField("Id");

			String opptyTeamUserId =(String)opptyTeamObject[j].getField("sfbase__User__c");
			opptyTeamUserID.add(opptyTeamUserId);
		}

		//Validate Oppty Team with SF Team
		for (String ExpectedUser : sfTeamUserID) {

			String Userquery = "SELECT Name FROM User WHERE Id = '"+ExpectedUser+"'";
			SObject[] UserObject = ApiUtilities.executeQuery(connection, Userquery);
			String UserName =(String)UserObject[0].getField("Name");

			if(opptyTeamUserID.contains(ExpectedUser)){
				System.out.println("SalesforceTeam Member brought over to OpptyTeam, user :"+UserName);
			}else{
				System.out.println("Salesforce Team Member '"+UserName+"'(Id: "+ExpectedUser+") not found in Oppty Team");
				System.exit(0);
			}
		}

		String opptyTeamStatusQuery = "SELECT sfbase__OpptyTeamStatus__c FROM Opportunity WHERE Id ='"+OpptyId+"'";
		SObject[] opptyTeamStatusObject = ApiUtilities.executeQuery(connection, opptyTeamStatusQuery);
		String opptyTeamStatus =(String)opptyTeamStatusObject[0].getField("sfbase__OpptyTeamStatus__c");

		if(opptyTeamStatus.equalsIgnoreCase("Valid")){
			System.out.println("Valid Oppty Team");
		}
		if(opptyTeamStatus.equalsIgnoreCase("Invalid")){

			System.out.println("Oppty Team Status is Invalid.");
			System.exit(0);
		}

		return OpptyTeamProcess;

	}

	/**
	 * Create Run Specialist Forecast Using API call
	 * @return RenewalOpptyId
	 **/
	public int RunSpecialistForecastAutoGen(WebDriver driver, String OpptyId) throws Exception{

		currentUrl = driver.getCurrentUrl();
		baseUrl = "https://"+apiUtilities.getHost(currentUrl)+"/";

		Thread.sleep(10000);
		LOG.info(" : baseUrl : "+baseUrl);

		int specialistForecastRecords = 0;
		String autoGenSpecialistForecastQuery = "SELECT sfbase__AutoGenerateSpecialistForecast__c FROM Opportunity WHERE Id = '"+OpptyId+"'";
		SObject[] autoGenSpecialistForecastObject = ApiUtilities.executeQuery(connection, autoGenSpecialistForecastQuery);
		String autoGenSpecialistForecast = (String)autoGenSpecialistForecastObject[0].getField("sfbase__AutoGenerateSpecialistForecast__c");
		LOG.info(" : autoGenSpecialistForecast status : "+autoGenSpecialistForecast);

		if(autoGenSpecialistForecast.equals("false"))
		{
			commonAPI.updateField(connection, "Opportunity", OpptyId, "sfbase__AutoGenerateSpecialistForecast__c", true);
			SObject[] autoGenSpecialistForecastObject2 = ApiUtilities.executeQuery(connection, autoGenSpecialistForecastQuery);
			String autoGenSpecialistForecast2 = (String)autoGenSpecialistForecastObject2[0].getField("sfbase__AutoGenerateSpecialistForecast__c");			
			LOG.info(" : Updated autoGenSpecialistForecast Status : "+autoGenSpecialistForecast2);			
		}

		driver.get(baseUrl+"apex/SpecialistForecastAutoGenPage?id="+OpptyId);

		String specialistForecastRecordsquery 		= "SELECT Id FROM SpecialistForecast__c WHERE Opportunity__c = '"+OpptyId+"'";
		SObject[] specialistForecastRecordsObject	= null;
		for(int i = 0; i < 20; i++){
			specialistForecastRecordsObject = ApiUtilities.executeQuery(connection, specialistForecastRecordsquery);
			specialistForecastRecords = (int)specialistForecastRecordsObject.length;
			if(specialistForecastRecords > 0)
			{
				i = 22;	
			}
			Thread.sleep(10000);			
		}	


		if (specialistForecastRecords > 0){
			LOG.info(" : Specialist Forecast records created based on Auto Generate Specialist Forecast");		
			LOG.info(" : Specialist Forecast records : "+ specialistForecastRecords);
		}else{
			LOG.info(" : Specialist Forecast records not created");	
			Assert.assertTrue("Specialist Forecast records created based on Auto Generate Specialist Forecast-Failed", false);
		}

		return specialistForecastRecords;
	}

	public int ValidateAddOnOppty(PartnerConnection connection, String AccountId){
		int Oppty = 0;
		try{
			//Queryig for add on oppty created on Account
			String OpptyQuery = "SELECT Id, Name, Amount, Type FROM Opportunity WHERE AccountId = '"+AccountId+"' AND Type = 'Add-On Business'";
			
			SObject[] SObject_OpptyId = ApiUtilities.executeQuery(connection, OpptyQuery);
			Oppty = SObject_OpptyId.length;
			if(Oppty > 0){
				//validations for add on opportunity created
				String Type	=(String)SObject_OpptyId[0].getField("Type");
				if(Type.contains("Add-On")){
					LOG.info("Opportuity Created with Type Add On Business Successfuly");
					//query for Recommended Remaining Quantity from entitlement schedule
					String EntitlementIdQuery = "SELECT Id FROM sfbase__Entitlement__c WHERE sfbase__Account__c = '"+AccountId+"'";
					SObject[] EntitlementIdObject = ApiUtilities.executeQuery(connection, EntitlementIdQuery);
					String EntitlementId = (String)EntitlementIdObject[0].getField("Id");
					
					String EntitlementScheduleNameQuery = "SELECT Name FROM sfbase__EntitlementSchedule__c WHERE sfbase__Entitlement__c = '"+EntitlementId+"'";
					SObject[] EntitlementScheduleNameObject = ApiUtilities.executeQuery(connection, EntitlementScheduleNameQuery);
					String EntitlementScheduleName = (String)EntitlementScheduleNameObject[0].getField("Name");
					
					String RecommendedRemainingQuantityQuery = "SELECT RecommendedRemainingQuantity__c FROM sfbase__EntitlementSchedule__c WHERE Name = '"+EntitlementScheduleName+"'";
					SObject[] RecommendedRemainingQuantityObject = ApiUtilities.executeQuery(connection, RecommendedRemainingQuantityQuery);
					String RecommendedRemainingQuantity = (String)RecommendedRemainingQuantityObject[0].getField("RecommendedRemainingQuantity__c");
					
					//Querying for Sales price from usage details
					String ContactIdQuery = "SELECT Id FROM Contract WHERE AccountId = '"+AccountId+"'";
					SObject[] ContactIdObject = ApiUtilities.executeQuery(connection, ContactIdQuery);
					String ContactId = (String)ContactIdObject[0].getField("Id");
					
					String SalesPriceQuery = "SELECT Sales_Price__c FROM UsageDetails__c WHERE Contract__c = '"+ContactId+"'";
					SObject[] SalesPriceObject = ApiUtilities.executeQuery(connection, SalesPriceQuery);
					String SalesPrice = (String)SalesPriceObject[0].getField("Sales_Price__c");
					
					double Amount = Double.parseDouble(RecommendedRemainingQuantity) * Double.parseDouble(SalesPrice);
					String OpportunityAmount	=(String)SObject_OpptyId[0].getField("Amount");
					if(Double.parseDouble(OpportunityAmount) == Amount){
						LOG.info("Add on oppty created with Amount : "+ OpportunityAmount);
					}else{
						LOG.info("Add On Opportunity Created with Amount : 0.0");
						return 0;
					}
					return Oppty;
				}else{
					LOG.info("Opportuity Not Created with Type Add On Business");
					return 0;
				}
			}else{
				LOG.info("Add On Opportunity Not created");
				return Oppty;
			}
		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
			System.exit(0);
		}
		return Oppty;
	}
	/**
	 * Get Oppty team to Oppty team object from DB process @pdatrak
	 * @param connection
	 * @param OpptyId
	 * @return OpptyTeamList
	 **/
	public List<Object[]> Oppty_Team_List_From_DB(PartnerConnection connection,String OpptyId) throws Exception 
	{

		//Get Oppty Team 
		
		List<Object[]> OpptyTeamList = new ArrayList<Object[]>();   
		OpptyTeamList.clear();
		Object[] OpptyUserDetails = new Object[8];
		
		String sfTeamQuery = "select sfbase__User__c,TeamRoleName__c,sfbase__LevelOfAccess__c,sfbase__SplitPercent__c,Status__c,sfbase__EndDate__c,Purpose__c, sfbase__Source__c from sfbase__OpportunityTeam__c where sfbase__Opportunity__c = '"+OpptyId+"'";
		SObject[] sfTeamObject  = ApiUtilities.executeQuery(connection, sfTeamQuery);
		
		for(int j=0;j<=sfTeamObject.length-1;j++){

			String opptyTeamUserId = (String)sfTeamObject[j].getField("sfbase__User__c");
			
			String opptyTeamUserNameQuery = "SELECT Name FROM User WHERE Id = '"+opptyTeamUserId+"'";
			SObject[] opptyTeamUserNameObject  = ApiUtilities.executeQuery(connection, opptyTeamUserNameQuery);
			String opptyTeamUserName = (String)opptyTeamUserNameObject[0].getField("Name");
			
			
			String opptyTeamRole =(String)sfTeamObject[j].getField("TeamRoleName__c");
			String opptyTeamAccountLevelAccess =(String)sfTeamObject[j].getField("sfbase__LevelOfAccess__c");
			String opptyTeamSplitpercentage =(String)sfTeamObject[j].getField("sfbase__SplitPercent__c");
			String opptyTeamStatus =(String)sfTeamObject[j].getField("Status__c");
			String opptyTeamRecordStatus =(String)sfTeamObject[j].getField("sfbase__EndDate__c");
			String opptyTeamRecordStatusPurpose =(String)sfTeamObject[j].getField("Purpose__c");
			String opptyTeamRecordStatusSource =(String)sfTeamObject[j].getField("sfbase__Source__c");
			
		OpptyUserDetails = new Object[8];
		OpptyUserDetails[0] = opptyTeamUserName;
		OpptyUserDetails[1] = opptyTeamRole;
		OpptyUserDetails[2] = opptyTeamAccountLevelAccess;
		OpptyUserDetails[3] = opptyTeamSplitpercentage;
		OpptyUserDetails[6] = opptyTeamRecordStatusSource;
		OpptyUserDetails[7] = opptyTeamRecordStatus;
		if(opptyTeamRecordStatus==null && opptyTeamRecordStatusPurpose.equals("Visibility"))
		{	
		OpptyUserDetails[4] = "Sharing";
		}
		else
		{
			OpptyUserDetails[4] = opptyTeamStatus;
		}
		if(opptyTeamRecordStatus==null)
		{	
		OpptyUserDetails[5] = "Active";
		}
		else
		{
			OpptyUserDetails[5] = "Inactive";
		}
		OpptyTeamList.add(OpptyUserDetails);
		}
		
		
		//Printing Oppty Team Details in Table Format
		System.out.println("");
		System.out.println("Oppty Team Details from DataBase ( Opportunity Id: "+OpptyId+" )");
		System.out.println("");
		String TableSpacing ="%35s%20s%15s%10s%37s%15s%15s";
		System.out.format(TableSpacing, 
				"|=================================|", 
				"===================|", 
				"==============|", 
				"=========|", 
				"====================================|",
				"==============|", 
				"=============|" +"\n");
		System.out.format(TableSpacing, "USER NAME |", "TEAM ROLE |", "ACCESS LEVEL |", "SPLIT % |", "SPLIT STATUS |","STATUS |", "SOURCE |" +"\n");
		System.out.format(TableSpacing, 
				"|=================================|", 
				"===================|", 
				"==============|", 
				"=========|", 
				"====================================|",
				"==============|", 
				"=============|" +"\n");
		for(Object[] UserRecord : OpptyTeamList){	
			System.out.format(TableSpacing, UserRecord[0]+" |", UserRecord[1]+" |", UserRecord[2]+" |", UserRecord[3]+" |", UserRecord[4]+" |",UserRecord[5]+" |", UserRecord[6] +" |"+"\n");
			System.out.format(TableSpacing, 
					"|_________________________________|", 
					"___________________|", 
					"______________|", 
					"_________|", 
					"____________________________________|",
					"______________|", 
					"_____________|" +"\n");
		}
		return OpptyTeamList;
	}
	/**
	 * Check Status of the Checkbox for mark for opptyteam   @pdatrak
	 * @param connection
	 * @param OpptyId
	 * @return String Value markForOpptyTeamCheckBoxStatus 
	 **/
	
	public String markForOpptyTeamCheckBoxStatus(PartnerConnection connection,String OpptyId) throws ConnectionException{
		
		String markForOpptyTeamCheckBoxStatus = null;
		String markForOpptyTeamQuery = "SELECT sfbase__MarkForOpptyTeamCreate__c FROM Opportunity WHERE Id = '"+OpptyId+"'";
		SObject[] markForOpptyTeamObject = ApiUtilities.executeQuery(connection, markForOpptyTeamQuery);
		 markForOpptyTeamCheckBoxStatus = (String)markForOpptyTeamObject[0].getField("sfbase__MarkForOpptyTeamCreate__c");
		LOG.info(" : Mark for Oppty Team status : "+markForOpptyTeamCheckBoxStatus);
		return markForOpptyTeamCheckBoxStatus;
		
	}
	/**
	 * Oppty Team records count from DB  @pdatrak
	 * @param connection
	 * @param OpptyId
	 * @return OpptyTeamRecordsCount
	 **/
	
	public int OpptyTeamRecordsCount(PartnerConnection connection,String OpptyId) throws ConnectionException{
		
		int OpptyTeamRecordsCount = 0;
		String opptyTeamRecordsQuery = "select sfbase__User__c from sfbase__OpportunityTeam__c where sfbase__Opportunity__c = '"+OpptyId+"'";
		SObject[] opptyTeamObject  = ApiUtilities.executeQuery(connection, opptyTeamRecordsQuery);
		OpptyTeamRecordsCount = (int)opptyTeamObject.length;	
		
		return OpptyTeamRecordsCount;
	
	}
	
	
	//Verify for Oppty stage to be closed
	public String checkOpptyStage(PartnerConnection custom_connection, String opptyId) throws Exception{
		String opptyStagequery = "SELECT StageName FROM Opportunity WHERE Id = '"+opptyId+"'";
		SObject[] opptyStageObject = ApiUtilities.executeQuery(custom_connection, opptyStagequery);
		String opptyStage = (String)opptyStageObject[0].getField("StageName");

		if(opptyStage.equals("08 - Closed") || opptyStage.equals("05 Closed")){
			LOG.info(" : Opportunity is closed with stage '"+opptyStage+"'");
		}
		else {
			LOG.info(" : Opportunity is not closed. Please check.");
			System.exit(0);
		}
		return opptyStage;
	}
	
	//Verify for Oppty stage
	public String checkOpptyStage(PartnerConnection custom_connection, String opptyId, String ExpectedOpptyStage) throws Exception{
		String opptyStagequery = "SELECT StageName FROM Opportunity WHERE Id = '"+opptyId+"'";
		SObject[] opptyStageObject = ApiUtilities.executeQuery(custom_connection, opptyStagequery);
		String ActualopptyStage = (String)opptyStageObject[0].getField("StageName");

		if(ActualopptyStage.equals(ExpectedOpptyStage)){
			LOG.info(" : Opportunity stage '"+ActualopptyStage+"'");
		}
		else {
			LOG.info(" : Opportunity is "+ExpectedOpptyStage+". Please check.");
			System.exit(0);
		}
		return ActualopptyStage;
	}


	//Verify AssetLines
	public int verifyAssetLines(PartnerConnection custom_connection, String ContractId) throws ConnectionException{
		String assetLinesQuery= "SELECT Id,SfdcAssetContract__c FROM Apttus_Config2__AssetLineItem__c WHERE SfdcAssetContract__c = '"+ContractId+"'";
		SObject[] assetLinesObject = ApiUtilities.executeQuery(custom_connection, assetLinesQuery);
		int assetLineStatus = (int)assetLinesObject.length;	

		if (assetLineStatus > 0){
			LOG.info(" : AssetLines created on Contract");		
		}else{
			LOG.info(" : AssetLines missing on Contract : "+ContractId);	
			System.exit(0);
		}
		return assetLineStatus;
	}
	
	
	//Create MC NBAO Opportunity
		public String createMCNBAOOpportunity(PartnerConnection custom_connection, 
											String AccountId, 
											String OpptyName, 
											String recordType,
											String Type, 
											String Currency,
											String RLO_Id,
											GregorianCalendar NBAO_CloseDate) throws ConnectionException, InterruptedException{
			
			String OpportunityId = null;
			String recordTypeId = commonAPI.getRecordTypeId(custom_connection, "Opportunity", recordType);
			
			
			dataMap = TestDataProvider.dataReader(TEST_DATA);
			Map<String,Object> NBAOOpportunityMap = dataMap.get("GlobalOpportunity");
			
			NBAOOpportunityMap.put("Name", OpptyName);
			NBAOOpportunityMap.put("AccountId", AccountId);
			NBAOOpportunityMap.put("RecordTypeId", recordTypeId);
			NBAOOpportunityMap.put("Type", Type);
			NBAOOpportunityMap.put("CloseDate", NBAO_CloseDate);
			NBAOOpportunityMap.put("CurrencyIsoCode", Currency);
			NBAOOpportunityMap.put("Related_License_Oppty__c", RLO_Id);
			NBAOOpportunityMap.put("StageName", "01 - Identifying an Opportunity");
			NBAOOpportunityMap.put("CompetitiveStatus__c", "1 - Behind Competition");
			NBAOOpportunityMap.put("PrimaryCompetitor__c", "Cheetah/Experian");

			OpportunityId = ApiUtilities.createObject(custom_connection,"Opportunity", NBAOOpportunityMap);
			LOG.info("NBAO Opportunity Id   : "+OpportunityId);
			return OpportunityId;
		}
		
		//Create Opportunity Team
		public String addOpptyTeamMember(PartnerConnection custom_connection, 
											String userId, 
											String opptyId, 
											String teamRoleName,
											String teamRoleId,
											String splitPercent,
											String marketSegment,
											String Currency) throws ConnectionException, InterruptedException{
			
			dataMap = TestDataProvider.dataReader(TEST_DATA);
			Map<String,Object> OpportunityTeamMap = dataMap.get("GlobalOpportunityTeam");
			
			OpportunityTeamMap.put("sfbase__User__c", userId);
			OpportunityTeamMap.put("sfbase__MarketSegment__c	", marketSegment);
			OpportunityTeamMap.put("sfbase__Opportunity__c", opptyId);
			OpportunityTeamMap.put("sfbase__SplitPercent__c", splitPercent);
			OpportunityTeamMap.put("sfbase__TeamRole__c", teamRoleName);
			OpportunityTeamMap.put("TeamRoleLookup__c", teamRoleId);
			OpportunityTeamMap.put("CurrencyIsoCode", Currency);
			

			String OpportunityTeamId = ApiUtilities.createObject(custom_connection,"sfbase__OpportunityTeam__c",  OpportunityTeamMap);
			LOG.info("OpportunityTeam Id   : "+OpportunityTeamId);
			return OpportunityTeamId;
		}
		
		/**
		 * Call Run Opportuniry process
		 * @param OpptyId
		 **/
		public boolean runNewOpptyTeamProces(String OpptyId) throws Exception {

			String markForOpptyTeamQuery = "SELECT sfbase__MarkForOpptyTeamCreate__c FROM Opportunity WHERE Id = '"+OpptyId+"'";
			SObject[] markForOpptyTeamObject = ApiUtilities.executeQuery(connection, markForOpptyTeamQuery);
			String markForOpptyTeam = (String)markForOpptyTeamObject[0].getField("sfbase__MarkForOpptyTeamCreate__c");
			LOG.info(" : Mark for Oppty Team status : "+markForOpptyTeam);

			String OpptyTeamProcesScript = "Opportunity[] oppRecsToProcess = "
					+ "[SELECT Id, ownerId, o.AccountId, o.Account.sfbase__EligibleForRenewalComp__c,o.Account.sfbase__Teamquota__c,o.RecordType.name,o.stageName,Owner.sfbase__Market_Segment__c "
					+ "FROM Opportunity o "
					+ "WHERE o.id IN ('"+OpptyId+"') AND o.AccountId != null AND o.sfbase__MarkForOpptyTeamCreate__c  = true]; "
					+ "OpptyTeamService ins1 = new OpptyTeamService(); "
					+ "ins1.processAllOpptys(oppRecsToProcess);";
			LOG.info(" OpptyTeamProcesScript : "+OpptyTeamProcesScript);
			boolean OpptyTeamProcess = false;
			OpptyTeamProcess = ApiUtilities.runApexWithReturn(OpptyTeamProcesScript);
			LOG.info(" : Oppty Team Process : "+OpptyTeamProcess);
			
			String markForOpptyTeamQuery_After = "SELECT sfbase__MarkForOpptyTeamCreate__c FROM Opportunity WHERE Id = '"+OpptyId+"'";
			SObject[] markForOpptyTeamObject_After = ApiUtilities.executeQuery(connection, markForOpptyTeamQuery_After);
			String markForOpptyTeam_After = (String)markForOpptyTeamObject_After[0].getField("sfbase__MarkForOpptyTeamCreate__c");
			LOG.info(" : After Oppty Team Process Run - status : "+markForOpptyTeam_After);

			int OpptyTeamProcesStatus=0;
			String OpptyTeamProcesStatusquery= "SELECT Id FROM sfbase__OpportunityTeam__c WHERE sfbase__Opportunity__c  = '"+OpptyId+"' AND sfbase__EndDate__c = null";
			SObject[] OpptyTeamProcesStatusObject = ApiUtilities.executeQuery(connection, OpptyTeamProcesStatusquery);
			OpptyTeamProcesStatus = (int)OpptyTeamProcesStatusObject.length;		

			LOG.info(" : Oppty Team Process Count : "+OpptyTeamProcesStatus);
			if (OpptyTeamProcesStatus > 0){
				LOG.info(" : Oppty Team Created based on Oppty Team Process");		
			}else{
				LOG.info(" : Oppty Team Not Created");	
				System.exit(0);
			}
			return OpptyTeamProcess;
		}

		public OpportunityProductSummaryBean getOpportunityProductSummary(String renewalOpptyId) throws ConnectionException {
			String opsoListQuery = "SELECT sfbase__PriorAnnualOrderValue__c," +
					"sfbase__ForecastedAnnualOrderValue__c,sfbase__ForecastedOTV__c,sfbase__ForecastedTotalOrderValue__c," +
					"sfbase__PriorMonthlyOrderValue__c,sfbase__PriorOTV__c,sfbase__PriorTotalOrderValue__c " +
					"FROM sfbase__OpportunityProductSummary__c where sfbase__Opportunity__c = '" + renewalOpptyId +
					"' and sfbase__PriorAnnualOrderValue__c != 0.0";
			SObject[] opsoList = ApiUtilities.executeQuery(connection, opsoListQuery);
			opportunityProductSummaryBean.setForecastedOTV((String) opsoList[0].getField("sfbase__ForecastedOTV__c"));
			opportunityProductSummaryBean.setForecastedTotalOrderValue((String) opsoList[0].getField("sfbase__ForecastedTotalOrderValue__c"));
			opportunityProductSummaryBean.setPriorMonthlyOrderValue((String) opsoList[0].getField("sfbase__PriorMonthlyOrderValue__c"));
			opportunityProductSummaryBean.setPriorOTV((String) opsoList[0].getField("sfbase__PriorOTV__c"));
			opportunityProductSummaryBean.setPriorTotalOrderValue((String) opsoList[0].getField("sfbase__PriorTotalOrderValue__c"));
			opportunityProductSummaryBean.setSfbase__PriorAnnualOrderValue__c((String) opsoList[0].getField("sfbase__PriorAnnualOrderValue__c"));

			return opportunityProductSummaryBean;
		}
}
