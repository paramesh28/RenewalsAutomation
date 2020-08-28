package com.salesforce.automation.commonAPI;

import com.salesforce.automation.RenewalsTestBaseClass;
import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.TestDataProvider;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

public class PageObjectApi extends RenewalsTestBaseClass {
	private static final Logger LOG = LoggerFactory.getLogger(PageObjectApi.class);
	final static private String PageObjectCommonAPI = "./testdata/PageObjectCommonAPI.xml";
	protected static Map<String, Map<String, Object>> dataMap = TestDataProvider.dataReader(PageObjectCommonAPI);

	DateFormat dateFormat = new SimpleDateFormat("YYYYMMdd");
	GregorianCalendar gcalendar;
	Date date;
	int hrs;
	int min;
	int sec;
	String Name;
	String OpptyName;
	String accountId, AcctName;
	String opptyId;
	String RTID;

	//Get Record Type
	public String getRecordTypeId(String objectName, String recordTypeName) throws ConnectionException{
		RTID = null;
		String query= "SELECT Id FROM RecordType where Name='"+recordTypeName+"' and SobjectType = '"+objectName+"'";
		SObject[] RID = ApiUtilities.executeQuery(connection, query);
		RTID =(String)RID[0].getField("Id");

		return RTID;
	}

	//Get Name of the Record
	public String getName(String objectName, String Id) throws ConnectionException{
		Name = null;
		String query= "SELECT Name FROM "+objectName+" where Id='"+Id+"'";
		SObject[] Name1 = ApiUtilities.executeQuery(connection, query);
		Name =(String)Name1[0].getField("Name");
		LOG.info(" : "+objectName+" Name : "+Name);

		return Name;
	}

	//Create Mock Org Id
	public String createMockOrgId(String accountId) throws ConnectionException{
		String mockOrgId = null;
		Map<String,Object> orgId = dataMap.get("GlobalOrgId");
		orgId.put("Account__c", accountId);
		mockOrgId = ApiUtilities.createObject(connection,"MockOrg__c", orgId);
		LOG.info("OrgId : "+mockOrgId);

		return mockOrgId;
	}

	//Create Account
	public String createAccount(String recordType) throws ConnectionException{
		String accountId = null;
		Date date = new Date();
		GregorianCalendar gcalendar = new GregorianCalendar();
		hrs=gcalendar.get(Calendar.HOUR);
		min=gcalendar.get(Calendar.MINUTE);
		sec=gcalendar.get(Calendar.SECOND);
		AcctName = "TestAcct"+dateFormat.format(date)+Integer.toString(hrs)+Integer.toString(min)+Integer.toString(sec);
		String recordTypeId = getRecordTypeId("Account", recordType);

		//dataMap = TestDataProvider.dataReader(PageObjectCommonAPI);
		Map<String,Object> accountMap = dataMap.get("GlobalAccount");
		accountMap.put("Name", AcctName);
		accountMap.put("RecordTypeId", recordTypeId);
		accountId = ApiUtilities.createObject(connection,"Account", accountMap);
		LOG.info("Account Id   : "+accountId);
		return accountId;
	}


	//Create Account
	public String createAccount(PartnerConnection custom_connection, String recordType) throws ConnectionException{
		String accountId = null;
		Date date = new Date();
		GregorianCalendar gcalendar = new GregorianCalendar();
		hrs=gcalendar.get(Calendar.HOUR);
		min=gcalendar.get(Calendar.MINUTE);
		sec=gcalendar.get(Calendar.SECOND);
		AcctName = "TestAcct"+dateFormat.format(date)+Integer.toString(hrs)+Integer.toString(min)+Integer.toString(sec);
		String recordTypeId = getRecordTypeId("Account", recordType);

		//dataMap = TestDataProvider.dataReader(PageObjectCommonAPI);
		Map<String,Object> accountMap = dataMap.get("GlobalAccount");
		accountMap.put("Name", AcctName);
		accountMap.put("RecordTypeId", recordTypeId);
		accountId = ApiUtilities.createObject(custom_connection,"Account", accountMap);
		LOG.info("Account Id   : "+accountId);
		return accountId;
	}

	//Create Tenant Info Exact Target
	public String createTenantInfoExactTarget(String accountId, String TenantId, String ORGID ) throws ConnectionException{

		String query2 = "SELECT Id FROM FulfillmentProvider__c where Name='ExactTarget'";
		SObject[] FullfilmentProvider = ApiUtilities.executeQuery(connection, query2);
		String FullfilmentProviderID=(String)FullfilmentProvider[0].getField("Id");
		LOG.info("Fullfilment Provider Id :"+FullfilmentProviderID);

		String query3= "SELECT Id FROM CloudServiceProvider where Name = 'ExactTarget'";
		SObject[] TenantId1 = ApiUtilities.executeQuery(connection, query3);
		String CloudServiceProviderID=(String)TenantId1[0].getField("Id");
		LOG.info(" : Cloud Service Provider Id :"+CloudServiceProviderID);

		Map<String,Object> createTenantInfo=dataMap.get("GlobalTenantInfo");
		createTenantInfo.put("SfdcFulfillmentProvider__c",FullfilmentProviderID);
		createTenantInfo.put("Apttus_Config2__Type__c",CloudServiceProviderID);
		createTenantInfo.put("Apttus_Config2__AccountId__c",accountId);
		createTenantInfo.put("SfdcParameters__c","{"+"\""+"ExtTenantEmail"+"\""+":"+"\""+"tenant@tenant.com"+"\""+","+"\""+"ExtTenantName"+"\""+":"+"\""+"ExactTarget-"+ORGID+"\""+","+"\""+"ExtTenantId"+"\""+":"+"\""+ORGID+"\""+"}");
		createTenantInfo.put("SfdcTenant__c", TenantId);
		createTenantInfo.put("Name",ORGID);
		createTenantInfo.put("SfdcCustomRecordName__c",ORGID);
		String TenantInfoId = ApiUtilities.createObject(connection,"Apttus_Config2__AccountLocation__c", createTenantInfo);
		return TenantInfoId;
	}


	//Create Tenant
	public String createTenant(String accountId, String TenantName,String orgId, String CloudServiceProvider) throws Exception{

		String query= "SELECT Id FROM CloudServiceProvider where Name = '"+CloudServiceProvider+"'";
		SObject[] TenantId1 = ApiUtilities.executeQuery(connection, query);
		String CloudServiceProviderID=(String)TenantId1[0].getField("Id");
		LOG.info(" : Tenant ID for "+CloudServiceProvider);
		//dataMap = TestDataProvider.dataReader(PageObjectCommonAPI);
		Map<String,Object> createTenant = dataMap.get("GlobalTenant");
		createTenant.put("AccountId", accountId);
		createTenant.put("Name",TenantName);        	
		createTenant.put("CloudServiceProviderId",CloudServiceProviderID);
		createTenant.put("ExternalId", orgId);
		String TenantId = ApiUtilities.createObject(connection,"Tenant", createTenant);
		LOG.info(" : TenantId for "+CloudServiceProvider+" : "+TenantId);

		return TenantId;
	}


	//Create Oppty
	public String createOpportunite(PartnerConnection Custom_connection, String accountId, String recordType, String type, String stageName) throws Exception{

		String OpptyId = null;
		String recordTypeId = getRecordTypeId("Opportunity", recordType);

		date = new Date();
		gcalendar = new GregorianCalendar();
		hrs=gcalendar.get(Calendar.HOUR);
		min=gcalendar.get(Calendar.MINUTE);
		sec=gcalendar.get(Calendar.SECOND);

		OpptyName = "Oppty_"+dateFormat.format(date)+Integer.toString(hrs)+Integer.toString(min)+Integer.toString(sec);

		Map<String,Object> createOppty = dataMap.get("GlobalOpportunity");		
		createOppty.put("Name", OpptyName);
		createOppty.put("AccountId",accountId);
		createOppty.put("RecordTypeId",recordTypeId);
		createOppty.put("Type",type);
		createOppty.put("StageName", stageName);
		createOppty.put("ForecastCategoryName","Omitted");
		if(recordType.contains("MC Services")){	
			String query= "SELECT Id FROM Opportunity Where Name like '%testoppty%' limit 1";
			SObject[] RelatedLicenseOppty = ApiUtilities.executeQuery(connection, query);
			String RelatedLicenseOpptyId=(String)RelatedLicenseOppty[0].getField("Id");
			createOppty.put("Related_License_Oppty__c",RelatedLicenseOpptyId);			
		}		
		OpptyId = ApiUtilities.createObject(Custom_connection,"Opportunity", createOppty);
		LOG.info(" : Oppty Id : "+OpptyId);

		return OpptyId;		
	}

	//Create Oppty
	public String createAddOnOpportunite(PartnerConnection Custom_connection, String accountId, String recordType, String type, String stageName) throws Exception{

		String OpptyId = null;
		String recordTypeId = getRecordTypeId("Opportunity", recordType);

		date = new Date();
		gcalendar = new GregorianCalendar();
		hrs=gcalendar.get(Calendar.HOUR);
		min=gcalendar.get(Calendar.MINUTE);
		sec=gcalendar.get(Calendar.SECOND);

		OpptyName = "AddOn-Oppty_"+dateFormat.format(date)+Integer.toString(hrs)+Integer.toString(min)+Integer.toString(sec);

		Map<String,Object> createOppty = dataMap.get("GlobalOpportunity");		
		createOppty.put("Name", OpptyName);
		createOppty.put("AccountId",accountId);
		createOppty.put("RecordTypeId",recordTypeId);
		createOppty.put("Type",type);
		createOppty.put("StageName", stageName);
		createOppty.put("ForecastCategoryName","Omitted");
		if(recordType.contains("MC Services")){	
			String query= "SELECT Id FROM Opportunity Where Name like '%testoppty%' limit 1";
			SObject[] RelatedLicenseOppty = ApiUtilities.executeQuery(connection, query);
			String RelatedLicenseOpptyId=(String)RelatedLicenseOppty[0].getField("Id");
			createOppty.put("Related_License_Oppty__c",RelatedLicenseOpptyId);			
		}		
		OpptyId = ApiUtilities.createObject(Custom_connection,"Opportunity", createOppty);
		LOG.info(" : Oppty Id : "+OpptyId);

		return OpptyId;		
	}


	//Quote Approval Process
	public boolean QuoteApproval(String QuoteId, String UN, String PWD) throws Exception{

		String Script_Step1 = "Apttus_Proposal__Proposal__c updQT = [SELECT id,SfdcQuoteStatus__c,SfdcConversionPending__c FROM Apttus_Proposal__Proposal__c WHERE Id = '"+QuoteId+"'];updQT.SfdcQuoteStatus__c = 'Awaiting Conversion';updQT.SfdcConversionPending__c = true;update updQT;";
		boolean quoteApproval = ApiUtilities.runApexAsUserWithReturn(Script_Step1, UN, PWD);
		LOG.info(" : Quote Approval Process : "+quoteApproval);

		return quoteApproval;
	}


	//Quote Convert Process
	public boolean QuoteConvert(String QuoteNumber, String UN, String PWD) throws Exception{

		String Script_Step2 = "String quoteNumber = SfdcConvertQuoteProcess.invokeConversions('"+QuoteNumber+"','1','jjayapal@salesforce.com','false','All');System.debug('return: ' +quoteNumber);";
		boolean quoteConvert = ApiUtilities.runApexAsUserWithReturn(Script_Step2, UN, PWD);
		LOG.info(" : Quote Convert Process: "+quoteConvert);

		return quoteConvert;
	}


	//Set Conversion Pending True
	public String setConversionPendingTrue(String QuoteId) throws Exception{

		String query= "SELECT SfdcConversionPending__c FROM Apttus_Proposal__Proposal__c where Id = '"+QuoteId+"'";
		SObject[] ConversionPending1 = ApiUtilities.executeQuery(connection, query);
		String ConversionPending=(String)ConversionPending1[0].getField("SfdcConversionPending__c");
		LOG.info(" : Conversion Pending before setting to true :: "+ConversionPending);

		if(ConversionPending.equals("false"))
		{
			Map<String,Object> updateConversionPending=dataMap.get("GlobalQuote");
			updateConversionPending.put("SfdcConversionPending__c", true);
			boolean ConversionPendingStatus = ApiUtilities.updateObjectAndReturnValue(connection, "Apttus_Proposal__Proposal__c", QuoteId, updateConversionPending);
			LOG.info(" : Conversion Pending Status field is set to true  " +ConversionPendingStatus);
		}

		query= "SELECT SfdcConversionPending__c FROM Apttus_Proposal__Proposal__c where Id = '"+QuoteId+"'";
		SObject[] ConversionPending2 = ApiUtilities.executeQuery(connection, query);
		String ConversionPending_New=(String)ConversionPending2[0].getField("SfdcConversionPending__c");
		LOG.info(" : Conversion Pending after setting to true : "+ConversionPending_New);

		return null;
	}	


	//create Asset Lines
	public boolean createAssetLines(String ContractID, String UN, String PWD) throws Exception{
		String ScriptAssetLines = "AssetBatchProcess.runAssetBatchProcessOnce('"+ContractID+"','jjayapal@salesforce.com');";
		boolean status = ApiUtilities.runApexAsUserWithReturn(ScriptAssetLines,UN,PWD);
		return status;

	}


	//create Coso
	public boolean createCoso(String ContractID, String UN, String PWD) throws Exception{
		String ScriptCosoGenerate = "RenewalsCosoGeneration cosoProcess = new RenewalsCosoGeneration(); cosoProcess.runCosoProcess('jjayapal@salesforce.com','20',null,'"+ContractID+"');";
		boolean status = ApiUtilities.runApexAsUserWithReturn(ScriptCosoGenerate,UN,PWD);
		return status;

	}


	//create Renewal Oppty
	public boolean createRenewalOppty(String ContractID, String UN, String PWD) throws Exception{
		String ScriptRenewalOppty = "AssetBatchProcess.runAssetBatchProcessOnce('"+ContractID+"','jjayapal@salesforce.com');";
		boolean status = ApiUtilities.runApexAsUserWithReturn(ScriptRenewalOppty,UN,PWD);
		return status;		
	}

	//Get Contract Ids
	public SObject[] getContractId(String accountId) throws Exception{

		String query= "SELECT Id FROM Contract where AccountId = '"+accountId+"'";
		SObject[] ContractId = ApiUtilities.executeQuery(connection, query);
		//String ContractId=(String)ContractId1[0].getField("Id");
		//System.out.println("Contract Id :: "+ContractId);

		return ContractId;		
	}

	//Get Order Ids
	public SObject[] getOrderId(String accountId) throws Exception{

		String query= "SELECT Id FROM Order where AccountId = '"+accountId+"'";
		SObject[] OrderId = ApiUtilities.executeQuery(connection, query);
		//String OrderId=(String)OrderId1[0].getField("Id");
		//System.out.println("Order Id :: "+OrderId);

		return OrderId;	   
	}

	//Update Order Status to Provisioned
	public void OrderStatusUpdate(String OrderId) throws Exception{	  

		boolean updateOrderStatus = false;
		String query= "SELECT Status FROM Order where Id = '"+OrderId+"'";
		SObject[] OrderStatus1 = ApiUtilities.executeQuery(connection, query);
		String OrderStatus=(String)OrderStatus1[0].getField("Status");
		LOG.info("Order Status  :: "+OrderStatus);

		if(!OrderStatus.equals("Provisioned"))
		{
			Map<String,Object> updateOrderStatus1=dataMap.get("GlobalOrder");
			updateOrderStatus1.put("Status", "Provisioned");
			updateOrderStatus = ApiUtilities.updateObjectAndReturnValue(connection, "Order", OrderId,updateOrderStatus1);
			LOG.info("Order Status updated to Provisioned");
		}
	}

	//update COSO Update Required field to true
	public void cOSOUpdateRequired(String ContractID) throws Exception{	  


		String query= "SELECT sfbase__COSOUpdateRequired__c FROM Contract WHERE Id = '"+ContractID+"'";
		SObject[] Status1 = ApiUtilities.executeQuery(connection, query);
		String Status=(String)Status1[0].getField("sfbase__COSOUpdateRequired__c");
		LOG.info("COSO Update Required  is : "+Status);

		if(Status.matches("false"))
		{

			Map<String,Object> updateCOSO = dataMap.get("GlobalContract");
			updateCOSO.put("sfbase__COSOUpdateRequired__c", true);
			ApiUtilities.updateObjectAndReturnValue(connection, "Contract", ContractID, updateCOSO);

			query= "SELECT sfbase__COSOUpdateRequired__c FROM Contract WHERE Id = '"+ContractID+"'";
			Status1 = ApiUtilities.executeQuery(connection, query);
			Status = (String)Status1[0].getField("sfbase__COSOUpdateRequired__c");

			Assert.assertEquals(Status, "true", "Update COSO Update Required - failed");		
			LOG.info("COSO Update Required set to : "+Status);
		}
	}

	//update Has Renewal Opportunity field to false
	public void setHasRenewalOpportunity(String ContractID) throws Exception{	  


		String query= "SELECT sfbase__HasRenewalOpportunity__c FROM Contract WHERE Id = '"+ContractID+"'";
		SObject[] Status1 = ApiUtilities.executeQuery(connection, query);
		String Status=(String)Status1[0].getField("sfbase__HasRenewalOpportunity__c");
		LOG.info("Has Renewal Opportunity  is : "+Status);

		if(Status.matches("true"))
		{
			Map<String,Object> updateHasRenewalOppty =dataMap.get("GlobalContract");
			updateHasRenewalOppty.put("sfbase__HasRenewalOpportunity__c", false);
			ApiUtilities.updateObjectAndReturnValue(connection, "Contract", ContractID, updateHasRenewalOppty);

			query= "SELECT sfbase__HasRenewalOpportunity__c FROM Contract WHERE Id = '"+ContractID+"'";
			Status1 = ApiUtilities.executeQuery(connection, query);
			Status = (String)Status1[0].getField("sfbase__HasRenewalOpportunity__c");

			Assert.assertEquals(Status, "false", "Update Has Renewal Opportunity - failed");				
			LOG.info("Has Renewal Opportunity is : "+Status);
		}
	}

	//Get User Id based on Username @jjayapal
	public String getUserId(String userName) throws Exception{	
		String UserId = null;
		String Name   = null;
		Assert.assertNotNull(userName, "Passed UserName is null, UserName is mandatory");

		String query= "SELECT Id, Name FROM User WHERE Username = '"+userName+"'";
		SObject[] queryObj = ApiUtilities.executeQuery(connection, query);
		UserId =(String)queryObj[0].getField("Id");
		Name =(String)queryObj[0].getField("Name");

		LOG.info("User Id   : "+UserId);
		LOG.info("User Name : "+Name);

		Assert.assertNotNull(UserId, "User Id is null");

		return UserId;

	}

	//Update Record Owner 
	public void updateRecordOwnerId(String objectName, String recordId, String ownerId) throws Exception{	

		LOG.info("Owner Id updated - Start");
		Map<String,Object> updateRecordOwnerId = dataMap.get("default");
		updateRecordOwnerId.put("OwnerId", ownerId);
		boolean updateStatus = ApiUtilities.updateObjectAndReturnValue(connection, objectName, recordId, updateRecordOwnerId);

		Assert.assertTrue(updateStatus, "Owner Id Update Failed");
		LOG.info("Owner Id updated");

		String Owner_Id = null;
		String query= "SELECT OwnerId FROM "+objectName+" WHERE Id = '"+recordId+"'";
		SObject[] queryObj = ApiUtilities.executeQuery(connection, query);
		Owner_Id =(String)queryObj[0].getField("OwnerId");

		Assert.assertEquals(ownerId, Owner_Id, "Owner Id not updated");
		LOG.info("Owner Id updated - Success");
	}

}