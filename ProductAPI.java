package com.salesforce.automation.commonAPI;

import com.salesforce.automation.util.ApiUtilities;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductAPI {

	private static final Logger LOG = LoggerFactory.getLogger(ProductAPI.class);

	//Get PriceBook Id
	public String getPriceBookIdByName(PartnerConnection custom_connection, String PriceBookName) throws ConnectionException{

		String  priceBook_Query = "SELECT Id, Name FROM Pricebook2 WHERE Name = '"+PriceBookName+"'";
		SObject[] priceBook_SObject = ApiUtilities.executeQuery(custom_connection, priceBook_Query);
		String priceBookId = (String)priceBook_SObject[0].getField("Id");

		return priceBookId;
	}

	// Get Price Book Entry Id
	public String getPriceBookEntryId(PartnerConnection custom_connection, String PriceBookName) throws ConnectionException{

		String  priceBook_Query = "SELECT Id, Name FROM Pricebook2 WHERE Name = '"+PriceBookName+"'";
		SObject[] priceBook_SObject = ApiUtilities.executeQuery(custom_connection, priceBook_Query);
		String priceBookId = (String)priceBook_SObject[0].getField("Id");

		return priceBookId;
	}

	public boolean validateProductLineItems(PartnerConnection custom_connection,
			String QuoteId,
			String OpportunityId) throws ConnectionException{

		LOG.info(" : Verifying Product Line Items - Started.........");
		LOG.info("");
		//Number of Product Lines = Quote Lines + 2
		/*LOG.info("------------------------------------------------------------------------------------------");
		LOG.info("              Verifying Count Of Product Line Items on Opportunity - Start                ");
		LOG.info("------------------------------------------------------------------------------------------");*/
		String QuoteLineCount = "SELECT Id FROM Apttus_Proposal__Proposal_Line_Item__c where Apttus_Proposal__Proposal__c = '"+QuoteId+"'";
		SObject[] QuoteLineCountObject = ApiUtilities.executeQuery(custom_connection, QuoteLineCount);
		int QLCount = (int)QuoteLineCountObject.length;
		int ExpectedPLCount = QLCount + 2;

		String ProductLineCount ="SELECT Id FROM OpportunityLineItem where OpportunityId='"+OpportunityId+"'";
		SObject[] ProductLineCountObject = ApiUtilities.executeQuery(custom_connection, ProductLineCount);
		int ActualPLCount = (int)ProductLineCountObject.length;

		if(ExpectedPLCount != ActualPLCount){
			LOG.info(" : Verifying Count Of Product Line Items on Opportunity - Failed");
			return false;
		}
		LOG.info(" : Verifying Count Of Product Line Items on Opportunity - Completed");

		//Opportunity Amount = Total price of Product Line Item
		//query for product line items from oppty with name not contains "Adjustment"
		String ProductLineItems = "SELECT Id, Product_Name__c, TotalPrice FROM OpportunityLineItem WHERE OpportunityId = '"+OpportunityId+"' AND (NOT Product_Name__c LIKE '%Adjustment%') ORDER BY ServiceDate ASC LIMIT 3";
		SObject[] ProductLineItemsObject = ApiUtilities.executeQuery(custom_connection, ProductLineItems);
		double TotalPrice = Double.parseDouble((String)ProductLineItemsObject[0].getField("TotalPrice"));

		double Adjustment_SFA_Amount =  Double.parseDouble((String)ProductLineItemsObject[1].getField("TotalPrice")) + Double.parseDouble((String)ProductLineItemsObject[2].getField("TotalPrice")) ;

		String OpptyAmount = "SELECT Amount from Opportunity where id  = '"+OpportunityId+"'";
		SObject[] OpptyAmountObject = ApiUtilities.executeQuery(custom_connection, OpptyAmount);
		double Amount = Double.parseDouble((String)OpptyAmountObject[0].getField("Amount"));

		if(TotalPrice != Amount){
			LOG.info(" : Verifying Opportunity Amount - Failed");
			return false;
		}
		LOG.info("");
		LOG.info(" : Verifying Opportunity Amount - Completed");

		//Adjustment - SFA total price = -ve value
		Adjustment_SFA_Amount = Adjustment_SFA_Amount * -1;
		String PLAdjustmentSFAPrice = "SELECT TotalPrice FROM OpportunityLineItem WHERE OpportunityId = '"+OpportunityId+"' AND Product_Name__c LIKE'Adjustment%' AND (NOT Product_Name__c LIKE'%Unallocated')";
		SObject[] PLAdjustmentSFAPriceObject = ApiUtilities.executeQuery(custom_connection, PLAdjustmentSFAPrice);
		System.out.println("PLAdjustmentSFAPriceObject : " + PLAdjustmentSFAPriceObject.length);
		double SFA_totalPrice = Double.parseDouble((String)PLAdjustmentSFAPriceObject[0].getField("TotalPrice"));

		if(SFA_totalPrice != Adjustment_SFA_Amount){
			LOG.info(" : Verifying Total Price for Adjustment-SFA Product Line Item - Failed");
			return false;
		}
		LOG.info(" : Verifying Total Price for Adjustment-SFA Product Line Item - Completed");


		//Adjustment - Unallocated total price = 0.0
		String PLAdjustmentUnallocatedPrice = "SELECT TotalPrice FROM OpportunityLineItem WHERE OpportunityId = '"+OpportunityId+"' AND Product_Name__c LIKE '%Adjustment - Unallocated%' ";
		SObject[] PLAdjustmentUnallocatedPriceObject = ApiUtilities.executeQuery(custom_connection, PLAdjustmentUnallocatedPrice);
		System.out.println("PLAdjustmentUnallocatedPriceObject : " + PLAdjustmentUnallocatedPriceObject.length);
		double AU_totalPrice = Double.parseDouble((String)PLAdjustmentUnallocatedPriceObject[0].getField("TotalPrice"));

		if(AU_totalPrice != 0.0){
			LOG.info(" : Verifying Total Price for Adjustment-Unallocated Product Line Item - Failed");
			return false;
		}
		LOG.info(" : Verifying Total Price for Adjustment-Unallocated Product Line Item - Completed");

		LOG.info("");
		LOG.info(" : Verifying Product Line Items - Completed");
		LOG.info("");

		LOG.info("*********************************************************************************");
		LOG.info("                      Monopoly Ramp Scenario Validation                          ");
		LOG.info("*********************************************************************************");

		String MonopolyValidationProdLineItems = "SELECT Product_Name__c , BookingsTreatment__c,NCO__c,NonRevenueAmount__c,QuoteLineEndDate__c,QuoteLine__c FROM OpportunityLineItem WHERE OpportunityId = '"+OpportunityId+"' AND (NOT Product_Name__c LIKE '%Adjustment%') ORDER BY ServiceDate ASC";
		SObject[] MonopolyValidationProdLineItemsObject = ApiUtilities.executeQuery(custom_connection, MonopolyValidationProdLineItems);

		String Product_Name__c = (String)MonopolyValidationProdLineItemsObject[0].getField("Product_Name__c");
		LOG.info("");
		LOG.info("");
		LOG.info("Product Name : " + Product_Name__c);

		//validate NCO to be false
		String NCO__c = (String)MonopolyValidationProdLineItemsObject[0].getField("NCO__c");
		if(NCO__c.equals("false")){
			LOG.info("     NCO Value is populated as Expected : " + NCO__c);
		}else{
			LOG.info("     NCO Value is Not populated as Expected");
			return false;
		}

		//Validate Booking Treatment Not 04
		String BookingsTreatment__c = (String)MonopolyValidationProdLineItemsObject[0].getField("BookingsTreatment__c");
		if(BookingsTreatment__c.contains("01-ACV-Recurring")){
			LOG.info("     Booking Treatment Value is populated as Expected : " + BookingsTreatment__c);
		}else{
			LOG.info("     Booking Treatment Value is Not populated as Expected");
			return false;
		}

		//Validate NonRevenueAmount = 0
		String NonRevenueAmount__c = (String)MonopolyValidationProdLineItemsObject[0].getField("NonRevenueAmount__c");
		if(NonRevenueAmount__c.equals("0.0")){
			LOG.info("     Non Revenue Amount is Populated as Expected : " + NonRevenueAmount__c);
		}else {
			LOG.info("     Non Revenue Amount is Not populated as Expected");
			return false;
		}

		//Validate QuoteLine Not NULL
		String QuoteLine__c = (String)MonopolyValidationProdLineItemsObject[0].getField("QuoteLine__c");
		if(QuoteLine__c != null){
			LOG.info("     Quote Line Value is populated as Expected : " + QuoteLine__c);
		}else{
			LOG.info("     Quote Line Value is Not populated as Expected");
			return false;
		}

		//Validate QuoteLineEndDate Not NULL 
		String QuoteLineEndDate__c = (String)MonopolyValidationProdLineItemsObject[0].getField("QuoteLineEndDate__c");
		if(QuoteLineEndDate__c != null){
			LOG.info("     Quote Line End Date Value is populated as Expected : " + QuoteLineEndDate__c);
		}else{
			LOG.info("     Quote Line End Date Value is Not populated as Expected");
			return false;
		}


		LOG.info("");
		LOG.info("Monopoly Ramp Scenario Validation - Completed");
		LOG.info("");
		LOG.info("");
		return true;
	}

	public String getPriceBookEntryId(String productName, String priceBookName, PartnerConnection connection) throws ConnectionException {
		String PricebookEntryId_Query = "SELECT Id," + " Pricebook2Id," + "Product2.name, " + "Product2.Id "
				+ "FROM PricebookEntry WHERE Pricebook2.Name = '" + priceBookName + "' "
				+ "AND Product2.Quotable__c = 'Yes' " + "AND Product2.sfbase__Status__c = 'Active'"
				+ " AND Product2.IsActive = true " + "AND unitprice >0" + " AND PricebookEntry.BillingFrequency=1"
				+ " AND Product2.name like '" + productName + "'";
		SObject[] SObject_PricebookEntryId_Query = ApiUtilities.executeQuery(connection, PricebookEntryId_Query);
		String pricebookEntryId = (String) SObject_PricebookEntryId_Query[0].getField("Id");
		return pricebookEntryId;
	}

}
