package com.salesforce.automation.commonAPI;

import com.salesforce.automation.beans.OrderBean;
import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.TestDataProvider;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.GregorianCalendar;
import java.util.Map;

public class OrderAPI {
	private static final Logger LOG = LoggerFactory.getLogger(OrderAPI.class);

	public String ContactId;
	final static private String TEST_DATA = "./testdata/Order.xml";
	protected static Map<String, Map<String, Object>> dataMap = TestDataProvider.dataReader(TEST_DATA);

	CommonAPI commonAPI = new CommonAPI();

	/**
	 * Update Order Status Using API call
	 * @param connection
	 * @param OrderId
	 **/
	public void UpdateOrderStatus(PartnerConnection connection, String OrderId, String Status) throws InterruptedException{

		try{
			String OrderStatusQuery= "SELECT Status FROM Order WHERE Id = '"+OrderId+"'";
			SObject[] OrderStatusSobject = ApiUtilities.executeQuery(connection, OrderStatusQuery);
			String OrderStatus=(String)OrderStatusSobject[0].getField("Status");
			LOG.info("Current Order Status is : "+OrderStatus);

			if(!OrderStatus.equals(Status)){
				commonAPI.updateRecordStatus(connection, "Order", OrderId, Status);
				LOG.info("Order Status Updated from "+OrderStatus+" to "+Status);
			}

			String OrderStatusQuery1= "SELECT Status FROM Order WHERE Id = '"+OrderId+"'";
			SObject[] OrderStatusSobject1 = ApiUtilities.executeQuery(connection, OrderStatusQuery1);
			String OrderStatus1=(String)OrderStatusSobject1[0].getField("Status");			
			Assert.assertEquals(OrderStatus1, Status, "Order not updated to "+Status);
		}catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
		}
	}

	public String createOrder(PartnerConnection custom_connection,
			String ContractId,
			GregorianCalendar StartDate,
			int OrderTerm,
			String Pricebook2Id,
			String RecordTypeId,
			String OrderSubType,
			String OrderType,
			String Status){

		String OrderId = null;

		dataMap = TestDataProvider.dataReader(TEST_DATA);
		Map<String,Object> order=dataMap.get("default");

		order.put("ContractId", ContractId);
		order.put("EffectiveDate",StartDate);
		order.put("sfbase__OrderTermAps__c",OrderTerm);
		order.put("Pricebook2Id", Pricebook2Id);
		order.put("RecordTypeId",RecordTypeId);
		order.put("Order_Sub_Type__c",OrderSubType);
		order.put("Type", OrderType);
		order.put("Status", Status);
		order.put("sfbase__CustomerPORequired__c", "No");

		OrderId = ApiUtilities.createObject(custom_connection,"Order", order);	

		return OrderId;
	}

	//Create Order Line Item
	public String createOrderLineItem(PartnerConnection custom_connection,
									  String OrderId,
									  int Quantity,
									  String PricebookEntryId) throws ConnectionException {

		String OrderLineItemId = null;

		//Get UnitPrice from PricebookEntry object
		String  UnitPrice_Query = "SELECT UnitPrice FROM PricebookEntry WHERE Id = '"+PricebookEntryId+"'";
		SObject[] UnitPrice_SObject = ApiUtilities.executeQuery(custom_connection, UnitPrice_Query);
		String UnitPrice = (String)UnitPrice_SObject[0].getField("UnitPrice");
		Double UnitPrice1 = Double.parseDouble(UnitPrice);

		double unitPrice2 = UnitPrice1;
		LOG.info("Printing unitpirce --> " +unitPrice2);
		UnitPrice = String.valueOf(unitPrice2);

		dataMap = TestDataProvider.dataReader(TEST_DATA);
		Map<String,Object> OrderItem = dataMap.get("default");

		OrderItem.put("OrderId", OrderId);
		OrderItem.put("UnitPrice",UnitPrice);
		OrderItem.put("Quantity",Quantity);
		OrderItem.put("PricebookEntryId", PricebookEntryId);

		OrderLineItemId = ApiUtilities.createObject(custom_connection,"OrderItem", OrderItem);
		return OrderLineItemId;
	}

	//Update Order Approve - LastApprovedDate - API call - @jjayapal
	public boolean updateOrderApproveDate(PartnerConnection custom_connection, String RecordId, GregorianCalendar StartDate) throws InterruptedException, Exception{

		//Activated the contract
		dataMap = TestDataProvider.dataReader(TEST_DATA);
		Map<String,Object> StatusUpdate = dataMap.get("default");//GlobalUpdateOrderApproveDate");
		StatusUpdate.put("LastApprovedDate", StartDate);
		ApiUtilities.updateObject(custom_connection, "Order", RecordId, StatusUpdate);
		return true;

	}

	//create Reduction Order
	public String createReductionOrder(PartnerConnection custom_connection,
			String ContractId,
			GregorianCalendar RO_StartDate,
			String Pricebook2Id,
			String RecordTypeId,
			String OrderSubType,
			String OrderType,
			String Status){

		String ReductionOrderId = null;

		dataMap = TestDataProvider.dataReader(TEST_DATA);
		Map<String,Object> ReductionOrder=dataMap.get("GlobalOrder");

		ReductionOrder.put("ContractId", ContractId);
		ReductionOrder.put("EffectiveDate",RO_StartDate);		
		/*ReductionOrder.put("sfbase__OrdProvisionDatetime__c",RO_StartDate);
					ReductionOrder.put("sfbase__PartnerProvisioningDate__c",RO_StartDate);*/
		ReductionOrder.put("Pricebook2Id", Pricebook2Id);
		ReductionOrder.put("RecordTypeId",RecordTypeId);
		ReductionOrder.put("Order_Sub_Type__c",OrderSubType);
		ReductionOrder.put("Type", OrderType);
		ReductionOrder.put("Status", Status);
		ReductionOrder.put("OrdCommissionSubtype", "AddAttrition");
		ReductionOrder.put("OrdCommissionType", "Attrition");
		ReductionOrder.put("sfbase__CMCode__c", "Cancellation");
		ReductionOrder.put("sfbase__CMReason__c", "Cancellation");
		ReductionOrder.put("IsReductionOrder", true);

		ReductionOrderId = ApiUtilities.createObject(custom_connection,"Order", ReductionOrder);	

		return ReductionOrderId;
	}

	//Create Order Line Item
	public String createReductionOrderLineItem(PartnerConnection custom_connection,	String ReductionOrderId, String OriginalOrderItemId, int Quantity) throws Exception{

		String ReductionOrderLineItemId = null;

		dataMap = TestDataProvider.dataReader(TEST_DATA);
		Map<String,Object> ReductionOrderItem = dataMap.get("GlobalOrderItem");

		String OriginalOrderItem_Query = "SELECT ConfigurationType__c,"
				+ "PricebookEntryId,"
				+ "PriceListItem__c,"
				+ "UnitPrice,"
				+ "sfbase__ProductBillingFrequency__c"
				+ " FROM OrderItem WHERE id = '"+OriginalOrderItemId+"'";

		SObject[] OriginalOrderItem_SObject = ApiUtilities.executeQuery(custom_connection, OriginalOrderItem_Query);

		String ConfigurationType = (String)OriginalOrderItem_SObject[0].getField("ConfigurationType");
		String PricebookEntryId = (String)OriginalOrderItem_SObject[0].getField("PricebookEntryId");
		String PriceListItem = (String)OriginalOrderItem_SObject[0].getField("PriceListItem");
		String UnitPrice = (String)OriginalOrderItem_SObject[0].getField("UnitPrice");
		String sfbaseProductBillingFrequency = (String)OriginalOrderItem_SObject[0].getField("sfbase__ProductBillingFrequency__c");


		ReductionOrderItem.put("OrderId", ReductionOrderId);
		ReductionOrderItem.put("ConfigurationType__c", ConfigurationType);
		ReductionOrderItem.put("PricebookEntryId", PricebookEntryId);
		ReductionOrderItem.put("OriginalOrderItemId", OriginalOrderItemId);
		ReductionOrderItem.put("PriceListItem__c", PriceListItem);
		ReductionOrderItem.put("Quantity",Quantity);
		ReductionOrderItem.put("UnitPrice", 1500);
		ReductionOrderItem.put("sfbase__ProductBillingFrequency__c", sfbaseProductBillingFrequency);


		ReductionOrderLineItemId = ApiUtilities.createObject(custom_connection,"OrderItem", ReductionOrderItem);	
		return ReductionOrderLineItemId;
	}
	//Get Order Id using quote Id from Order Table
	public String getContractId(PartnerConnection custom_connection, String OrderId) throws Exception{	  

		String ContractIdQuery= "SELECT ContractId FROM Order WHERE Id = '"+OrderId+"'";
		SObject[] ContractIdObject = ApiUtilities.executeQuery(custom_connection, ContractIdQuery);
		String ContractId = (String)ContractIdObject[0].getField("ContractId");
		LOG.info("Contract Id : "+ContractId);

		return ContractId;
	}


	//Get OrderLine Id using Order Id and PricebookEntryId from OrderItem Table
	public String getOrderLineId(PartnerConnection custom_connection, String OrderId, String PricebookEntryId) throws Exception{	  

		String OrderLineIdQuery= "SELECT Id FROM OrderItem WHERE OrderId = '"+OrderId+"' AND PricebookEntryId = '"+PricebookEntryId+"'";
		SObject[] OrderLineIdObject = ApiUtilities.executeQuery(custom_connection, OrderLineIdQuery);
		String OrderLineId = (String)OrderLineIdObject[0].getField("Id");
		LOG.info("OrderLine Id : "+OrderLineId);

		return OrderLineId;
	}


	//Compare Number of Quote lines on Quote to Number of Order on Contract
	public boolean verifyOrdersOnContract(PartnerConnection custom_connection, String ContractId, String QuoteId) throws Exception{	  

		String QuoteLineCount = "SELECT Id FROM Apttus_Proposal__Proposal_Line_Item__c where Apttus_Proposal__Proposal__c = '"+QuoteId+"'";
		SObject[] QuoteLineCountObject = ApiUtilities.executeQuery(custom_connection, QuoteLineCount);
		int QLCount = (int)QuoteLineCountObject.length;

		String OrderCount = "SELECT id FROM Order where ContractId = '"+ContractId+"'";
		SObject[] OrderCountObject = ApiUtilities.executeQuery(custom_connection, OrderCount);
		int OCount = (int)OrderCountObject.length;
		System.out.println("OCount : " + OCount );

		if(QLCount != OCount){
			LOG.info("Orders are Not Created as Expected");
			return false;
		}
		LOG.info("Orders are Created as Expected");
		return true;
	}

	public OrderBean getOrderValueByAccount(String accountId, PartnerConnection connection) throws ConnectionException {
		OrderBean orderBean = new OrderBean();
		String OrderIdQuery = "SELECT sfbase__OrderLifeTimeValue__c,TotalAmount FROM Order where AccountId='" + accountId + "'";
		SObject[] OrderIdQuerySObject = ApiUtilities.executeQuery(connection, OrderIdQuery);
		orderBean.setOrderId((String)OrderIdQuerySObject[0].getField("sfbase__OrderLifeTimeValue__c"));
		orderBean.setTotalAmount((String) OrderIdQuerySObject[0].getField("TotalAmount"));
		return orderBean;
	}
}