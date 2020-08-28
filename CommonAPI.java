package com.salesforce.automation.commonAPI;

import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.TestDataProvider;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.GregorianCalendar;
import java.util.Map;

public class CommonAPI {

	private static final Logger LOG = LoggerFactory.getLogger(CommonAPI.class);
	protected static Map<String, Map<String, Object>> dataMap;
	final static private String TEST_DATA = "./testdata/CommonDataMap.xml";


	//Update Status field of any Record - API call - @jjayapal
	public boolean updateRecordStatus(PartnerConnection custom_connection,String Object, String RecordId, String Status) throws InterruptedException{

		try{
			//Activated the contract
			dataMap = TestDataProvider.dataReader(TEST_DATA);
			Map<String,Object> StatusUpdate = dataMap.get("GlobalUpdate");
			StatusUpdate.put("Status", Status);
			ApiUtilities.updateObject(custom_connection, Object, RecordId, StatusUpdate);

			return true;

		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return false;
	}


	//Update Status field of any Record - API call - @jjayapal
	public boolean updateDateField(PartnerConnection custom_connection,String Object, String RecordId, String FieldName, GregorianCalendar date) throws InterruptedException{

		try{
			//Activated the contract
			dataMap = TestDataProvider.dataReader(TEST_DATA);
			Map<String,Object> dateUpdate = dataMap.get("GlobalUpdate");
			dateUpdate.put(FieldName, date);
			ApiUtilities.updateObject(custom_connection, Object, RecordId, dateUpdate);
			return true;

		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return false;
	}


	//Get RecordTypeId - API call - @jjayapal
	public String getRecordTypeId(PartnerConnection custom_connection,String Object, String RecordTypeName) throws InterruptedException{

		try{
			String RT_Id_Query = "SELECT Id FROM RecordType WHERE SobjectType = '"+Object+"' and Name = '"+RecordTypeName+"'";
			SObject[] RT_Id_SObject = ApiUtilities.executeQuery(custom_connection, RT_Id_Query);
			String RecordTypeId = (String)RT_Id_SObject[0].getField("Id");
			return RecordTypeId;

		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return null;
	}

	//Update Tenant Info External ID field - API call - @jjayapal
	public boolean updateTenantInfoExternalID(PartnerConnection custom_connection,String Object, String RecordId, String Status) throws InterruptedException{

		try{
			//Activated the contract
			dataMap = TestDataProvider.dataReader(TEST_DATA);
			Map<String,Object> StatusUpdate = dataMap.get("GlobalUpdate");
			StatusUpdate.put("Status", Status);
			ApiUtilities.updateObject(custom_connection, Object, RecordId, StatusUpdate);
			return true;

		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return false;
	}


	//Get User Id based on Username @jjayapal
	public String getUserId(PartnerConnection custom_connection, String userName) throws Exception{	
		String UserId = null;
		String Name   = null;
		Assert.assertNotNull(userName, "Passed UserName is null, UserName is mandatory");

		String query= "SELECT Id, Name FROM User WHERE Username = '"+userName+"'";
		SObject[] queryObj = ApiUtilities.executeQuery(custom_connection, query);
		UserId =(String)queryObj[0].getField("Id");
		Name =(String)queryObj[0].getField("Name");

		LOG.info("User Id   : "+UserId);
		LOG.info("User Name : "+Name);

		Assert.assertNotNull(UserId, "User Id is null");

		return UserId;

	}


	//Update Record Owner @jjayapal
	public void updateRecordOwner(PartnerConnection custom_connection, String objectName, String recordId, String ownerId) throws Exception{	

		LOG.info("Owner Id updated - Start");
		dataMap = TestDataProvider.dataReader(TEST_DATA);
		Map<String,Object> updateRecordOwnerId = dataMap.get("default");
		updateRecordOwnerId.put("OwnerId", ownerId);
		boolean updateStatus = ApiUtilities.updateObjectAndReturnValue(custom_connection, objectName, recordId, updateRecordOwnerId);

		Assert.assertTrue(updateStatus, "Owner Id Update Failed");
		LOG.info("Owner Id updated");

		String Owner_Id = null;
		String query= "SELECT OwnerId FROM "+objectName+" WHERE Id = '"+recordId+"'";
		SObject[] queryObj = ApiUtilities.executeQuery(custom_connection, query);
		Owner_Id =(String)queryObj[0].getField("OwnerId");

		Assert.assertEquals(ownerId, Owner_Id, "Owner Id not updated");
		LOG.info("Owner Id updated - Success");
	}	


	//Update Record Name field of any Record - API call - @jjayapal
	public boolean updateRecordName(PartnerConnection custom_connection,String Object, String RecordId, String Name) throws InterruptedException{

		try{
			//Activated the contract
			dataMap = TestDataProvider.dataReader(TEST_DATA);
			Map<String,Object> NameUpdate = dataMap.get("GlobalUpdate");
			NameUpdate.put("Name", Name);
			ApiUtilities.updateObject(custom_connection, Object, RecordId, NameUpdate);
			return true;

		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return false;
	}


	//Update Record Name field of any Record - API call - @jjayapal
	public boolean updateField(PartnerConnection custom_connection,String Object, String RecordId, String FieldName, String FieldValue) throws InterruptedException{

		try{
			//Activated the contract
			dataMap = TestDataProvider.dataReader(TEST_DATA);
			Map<String,Object> NameUpdate = dataMap.get("GlobalUpdate");
			NameUpdate.clear();
			NameUpdate.put(FieldName, FieldValue);
			ApiUtilities.updateObject(custom_connection, Object, RecordId, NameUpdate);
			return true;

		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return false;
	}
	
	//Update Number of Employees field of any Record - API call - @devaki kamatchi
	public boolean updateField(PartnerConnection custom_connection,String Object, String RecordId, String FieldName, int FieldValue) throws InterruptedException{

		try{
		//Activated the contract
		dataMap = TestDataProvider.dataReader(TEST_DATA);
		Map<String,Object> NameUpdate = dataMap.get("GlobalUpdate");
		NameUpdate.clear();
		NameUpdate.put(FieldName, FieldValue);
		ApiUtilities.updateObject(custom_connection, Object, RecordId, NameUpdate);
		return true;

		}catch(Exception ex){
		LOG.info(" : Entered catch " + ex.getMessage());
		}
		return false;
		}
	
	

	//Update boolean field of any Record - API call - @jjayapal
	public boolean updateField(PartnerConnection custom_connection,String Object, String RecordId, String FieldName, boolean value) throws InterruptedException{

		try{
			//Activated the contract
			dataMap = TestDataProvider.dataReader(TEST_DATA);
			Map<String,Object> dateUpdate = dataMap.get("GlobalUpdate");
			dateUpdate.put(FieldName, value);
			ApiUtilities.updateObject(custom_connection, Object, RecordId, dateUpdate);
			return true;

		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return false;
	}

	//Get Any Field Value by Object Name and Record Id - API call - @jjayapal
	public String getFieldValue(PartnerConnection custom_connection,String Object, String RecordId, String FieldName) throws InterruptedException{

		try{
			String FieldValue_Query = "SELECT "+FieldName+" FROM "+Object+" WHERE Id = '"+RecordId+"'";
			SObject[] FieldValue_SObject = ApiUtilities.executeQuery(custom_connection, FieldValue_Query);
			String FieldValue = (String)FieldValue_SObject[0].getField(FieldName);
			LOG.info(" "+Object+" Name : "+FieldValue);
			System.out.println("");
			return FieldValue;

		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return null;
	}

	/**
	 * Get Any Field Value by Object Name and Record Id - API call - @jjayapal
	 * @param connection
	 * @param Object
	 * @param RecordId
	 * @param FieldName
	 * @return FieldValue
	 **/
	public String getIdValueBasedOnField(PartnerConnection custom_connection,String Object, String FieldName, String FieldValue) throws InterruptedException{

		try{
			String FieldValueId_Query = "SELECT Id FROM "+Object+" WHERE "+FieldName+" = '"+FieldValue+"'";
			SObject[] FieldValueId_SObject = ApiUtilities.executeQuery(custom_connection, FieldValueId_Query);
			String FieldValueRecordId = (String)FieldValueId_SObject[0].getField("Id");
			LOG.info(" Id of "+Object+" : "+FieldValueRecordId);
			return FieldValueRecordId;

		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return null;
	}

	//Get Any Id by Name - API call - @jjayapal
	public String getIdByName(PartnerConnection custom_connection,String Object, String Name) throws InterruptedException{
		String Id = null;
		try{
			String Id_Query = "SELECT Id FROM "+Object+" WHERE Name = '"+Name+"'";
			SObject[] FieldValue_SObject = ApiUtilities.executeQuery(custom_connection, Id_Query);
			Id = (String)FieldValue_SObject[0].getField("Id");
			LOG.info(" "+Object+" Id : "+Id);
			System.out.println("");
			System.out.println("");
			return Id;

		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
		return null;
	}

	//Update all task as complete - API call - @jjayapal
	public void taskAsComplete(PartnerConnection custom_connection,String OwnerId) throws InterruptedException{
		try{
			//Update all Task as completed for Oppty Owner
			LOG.info(" : Update Task Status for User "+OwnerId+" as Completed");
			String OpptyOwnerTaskQuery = "SELECT Id,Status FROM Task WHERE OwnerId = '"+OwnerId+"' AND Status != 'Completed'";
			SObject[] OpptyOwnerTaskObject = ApiUtilities.executeQuery(custom_connection, OpptyOwnerTaskQuery);
			int TaskCount = OpptyOwnerTaskObject.length;
			String TaskId = null;
			LOG.info(" : "+TaskCount+" Task Status going to update as Completed");

			if(TaskCount != 0){
				TaskId = null;
				for(int i = 0; i < TaskCount; i++){
					TaskId = (String)OpptyOwnerTaskObject[i].getField("Id");
					updateField(custom_connection, "Task", TaskId, "Status", "Completed");					
				}				
			}else{
				LOG.info(" : No Task Update");
			}
		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
	}
}