package com.salesforce.automation.commonAPI;

import com.salesforce.automation.commonElements.QuotePageElements;
import com.salesforce.automation.util.ApiUtilities;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class QuoteAPI {
	private static final Logger LOG = LoggerFactory.getLogger(QuoteAPI.class);

	QuotePageElements quoteElement 	= new QuotePageElements();

	PageObjectApi 	pageObjectApi 	= new PageObjectApi();
	CommonAPI commonAPI = new CommonAPI();
	ApiUtilities 	apiUtilities	= new ApiUtilities();

	String RenewalQuoteId 			= null;
	String HostName					= null;
	String RenewalOpptyUrl			= null;
	String CreateRenewalQuoteUrl 	= null;

	/**
	 * Parse the QuoteProducts.xml file and get the data into Map  @pdatrak
	 * @param RecordType - RecordTye will be either NB or MC and will be pass from the calling method.
	 * @return quoteProductsDataMap
	 */
	public static List<Map<String, String>> ParseQuoteProductsXML(String RecordType)
	{

		List<Map<String, String>> quoteProductsListDataMap = new ArrayList<Map<String, String>>();

		try {
			String fXmlFile = System.getProperty("QuoteProductsData");
			File inputFile = new File(fXmlFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("Products");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					if(eElement.getAttribute("Recordtype").equals(RecordType)){
						LOG.info("PriceList : " + eElement.getAttribute("PriceList"));
						LOG.info("Term      : " + eElement.getAttribute("Term"));
						LOG.info("RecordType: " + eElement.getAttribute("Recordtype"));
						LOG.info("");
						LOG.info("----------------------------");
						LOG.info("");	
						NodeList nList1 = doc.getElementsByTagName(RecordType+"Product");
						for (int i = 0; i < nList1.getLength(); i++) {
							Node productsNodes = nList1.item(i);
							if (productsNodes.getNodeType() == Node.ELEMENT_NODE) {
								Element eElement1 = (Element) productsNodes;
								HashMap<String, String> productmap = new HashMap<String, String>();
								if((eElement.getAttribute("Recordtype").equals(RecordType)&& (eElement1.getNodeName().contains(RecordType)))){
									LOG.info("Name    : " + eElement1.getAttribute("Name"));
									LOG.info("QTY     : " + eElement1.getAttribute("Qty"));
									LOG.info("isramp  : " + eElement1.getAttribute("isramp"));
									LOG.info("isMonopolyProduct    : " + eElement1.getAttribute("isMonopolyProduct"));
									LOG.info("");
									LOG.info("----------------------------");
									LOG.info("");	
									productmap.put("Name", eElement1.getAttribute("Name"));
									productmap.put("QTY", eElement1.getAttribute("Qty"));
									productmap.put("isramp", eElement1.getAttribute("isramp"));
									productmap.put("PriceList", eElement.getAttribute("PriceList"));
									productmap.put("Term", eElement.getAttribute("Term"));
									productmap.put("RecordType", eElement.getAttribute("Recordtype"));
									quoteProductsListDataMap.add(productmap);

								}
							}
						}
					}

				}

			}
			//To print the values of quoteProductsListDataMap
			for (int i=0; i<quoteProductsListDataMap.size(); i++){
				HashMap<String, String> hm = (HashMap) quoteProductsListDataMap.get(i);
				for (Entry<String, String> tempHM : hm.entrySet()) {
					//LOG.info("key*** = " + tempHM.getKey()+ "   :  value*** = " + tempHM.getValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return quoteProductsListDataMap;
	}

	public void submitForConvert(PartnerConnection connection, String QuoteId) throws Exception{
		String submitForConvertScript = "Apttus_Proposal__Proposal__c updQT = [SELECT SfdcQuoteStatus__c FROM Apttus_Proposal__Proposal__c WHERE Id = '"+QuoteId+"'];updQT.SfdcQuoteStatus__c = 'Sales Ops Review'; update updQT;";
		boolean ApprovalStatus1 = ApiUtilities.runApexWithReturn(submitForConvertScript);
		commonAPI.updateField(connection, "Apttus_Proposal__Proposal__c", QuoteId, "SfdcQuoteStatus__c", "Sales Ops Review");
		LOG.info(" : Quote Approval : "+ApprovalStatus1);

		String QuoteStatus = null;
		String QuoteStatusQuery= "SELECT SfdcQuoteStatus__c FROM Apttus_Proposal__Proposal__c WHERE Id = '"+QuoteId+"'";
		SObject[] QuoteStatusSObject = ApiUtilities.executeQuery(connection, QuoteStatusQuery);
		QuoteStatus = (String)QuoteStatusSObject[0].getField("SfdcQuoteStatus__c");
		Assert.assertEquals(QuoteStatus, "Sales Ops Review", "Quote status is: "+QuoteStatus+" not changed to Sales Ops Review");
	}


	public void ConvertQuote(PartnerConnection connection, String QuoteId) throws Exception{

		String quotenumber 		= null;
		String processName 		= null;
		String script 			= null;
		String quotestatus 		= null;
		boolean convertQuote 	= false;

		LOG.info(" : Updating, Quote Sfdc Conversion Pending as true");
		commonAPI.updateField(connection, "Apttus_Proposal__Proposal__c", QuoteId, "SfdcConversionPending__c", true);

		LOG.info(" : Updating, Quote 'SfdcCustomer PO Required' and 'Sfdc PO Number' as N/A");
		commonAPI.updateField(connection, "Apttus_Proposal__Proposal__c", QuoteId, "SfdcCustomerPORequired__c", "N/A");
		//commonAPI.updateField(connection, "Apttus_Proposal__Proposal__c ", QuoteId, "SfdcPONumber__c ", "NA");


		LOG.info(" : Get Sfdc Conversion Assigned To Value");
		String SfdcConversionAssignedTo__c = commonAPI.getFieldValue(connection, "Apttus_Proposal__Proposal__c", QuoteId, "SfdcConversionAssignedTo__c");
		Assert.assertNotNull(SfdcConversionAssignedTo__c);
		int strLength = SfdcConversionAssignedTo__c.length();
		processName = SfdcConversionAssignedTo__c.substring(strLength-1);

		LOG.info(" : Get Quote Number and Building script");
		quotenumber = commonAPI.getFieldValue(connection, "Apttus_Proposal__Proposal__c", QuoteId, "Name");
		script = "SfdcConvertQuoteProcess.invokeConversions"+processName+"('"+ quotenumber+ "','1','jjayapal@salesforce.com','false','Sunita','Process');";

		LOG.info(" : Run the script 1st times");
		convertQuote = ApiUtilities.runApexWithReturn(script);
		quotestatus = commonAPI.getFieldValue(connection, "Apttus_Proposal__Proposal__c", QuoteId, "SfdcQuoteStatus__c");
		if (quotestatus.equals("Conversion In-Progress")) {
			script = "SfdcConvertQuoteProcess.invokeConversions"+processName+"('"
					+ quotenumber 
					+ "','1','jjayapal@salesforce.com','false','Sunita','Process');";
			LOG.info(" : Run the script 2nd times");
			convertQuote = ApiUtilities.runApexWithReturn(script);
		}else{
			LOG.info(" : Quote Status not moved to Conversion In-Progress in 1st run");
		}

		LOG.info(" : Verifying Quote Status");
		quotestatus = commonAPI.getFieldValue(connection, "Apttus_Proposal__Proposal__c", QuoteId, "SfdcQuoteStatus__c");
		Assert.assertEquals(quotestatus, "Converted", "Quote status is: "+quotestatus+" not changed to Converted");
	}

	/**
	 * Parse the QuoteProducts.xml file and get the data into Map  @pdatrak
	 * @param RecordType - RecordTye will be either NB or MC and will be pass from the calling method.
	 * @return quoteProductsDataMap
	 */
	public static List<Map<String, String>> updateMonopolyProductsQuoteProductsXML(String RecordType,HashMap<String, String> monopolyProductsmap)
	{

		List<Map<String, String>> quoteProductsListDataMap = new ArrayList<Map<String, String>>();

		if(RecordType.contains("New Business / Add-On") ){
			RecordType = "NB";
		}
		if(RecordType.contains("MC Services New Business / Add-On")){
			RecordType = "MC";
		}

		try {
			String fXmlFile = System.getProperty("QuoteProductsData");
			File inputFile = new File(fXmlFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("Products");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					if(eElement.getAttribute("Recordtype").equals(RecordType)){
						LOG.info("PriceList : " + eElement.getAttribute("PriceList"));
						LOG.info("Term      : " + eElement.getAttribute("Term"));
						LOG.info("RecordType: " + eElement.getAttribute("Recordtype"));
						LOG.info("");
						LOG.info("----------------------------");
						LOG.info("");	
						NodeList nList1 = doc.getElementsByTagName(RecordType+"Product");
						for (int i = 0; i < nList1.getLength(); i++) {
							Node productsNodes = nList1.item(i);
							if (productsNodes.getNodeType() == Node.ELEMENT_NODE) {
								Element eElement1 = (Element) productsNodes;
								if((eElement.getAttribute("Recordtype").equals(RecordType)&& (eElement1.getNodeName().contains(RecordType)))){
									if(eElement1.getAttribute("isMonopolyProduct").equals("true") && eElement1.hasAttribute("bookingTreatment1")){
										eElement1.setAttribute("bookingTreatment1",monopolyProductsmap.get("bookingTreatment1"));
										LOG.info(" : Updated Monopoly product name for bookingTreatment1 here ="+monopolyProductsmap.get("bookingTreatment1"));
									}
									if(eElement1.getAttribute("isMonopolyProduct").equals("true") && eElement1.hasAttribute("bookingTreatment2")){
										eElement1.setAttribute("bookingTreatment2",monopolyProductsmap.get("bookingTreatment2"));
										LOG.info(" : Updated Monopoly product name for bookingTreatment2 here =" +monopolyProductsmap.get("bookingTreatment2"));
									}
									if(eElement1.getAttribute("isMonopolyProduct").equals("true") && eElement1.hasAttribute("bookingTreatment3")){
										LOG.info(" : Updated Monopoly product name for bookingTreatment3 here =" +monopolyProductsmap.get("bookingTreatment3"));
										eElement1.setAttribute("bookingTreatment3",monopolyProductsmap.get("bookingTreatment3"));
									}
									if(eElement1.getAttribute("isMonopolyProduct").equals("true") && eElement1.hasAttribute("bookingTreatment4")){
										eElement1.setAttribute("bookingTreatment4",monopolyProductsmap.get("bookingTreatment4"));
										LOG.info(" : Updated Monopoly product name for bookingTreatment4 here =" +monopolyProductsmap.get("bookingTreatment4"));
									}
									if(eElement1.getAttribute("isMonopolyProduct").equals("true") && eElement1.hasAttribute("bookingTreatment5") && eElement.getAttribute("Recordtype").equals("MC")){
										eElement1.setAttribute("bookingTreatment5",monopolyProductsmap.get("bookingTreatment5"));
										LOG.info(" : Updated Monopoly product name for bookingTreatment4 here =" +monopolyProductsmap.get("bookingTreatment5"));
									}

									HashMap<String, String> productmap = new HashMap<String, String>();
									productmap.put("Name", eElement1.getAttribute("Name"));
									productmap.put("QTY", eElement1.getAttribute("Qty"));
									productmap.put("isramp", eElement1.getAttribute("isramp"));
									productmap.put("isMonopolyProduct", eElement1.getAttribute("isMonopolyProduct"));
									productmap.put("bookingTreatment1", eElement1.getAttribute("bookingTreatment1"));
									productmap.put("bookingTreatment2", eElement1.getAttribute("bookingTreatment2"));
									productmap.put("bookingTreatment3", eElement1.getAttribute("bookingTreatment3"));
									productmap.put("bookingTreatment4", eElement1.getAttribute("bookingTreatment4"));
									productmap.put("bookingTreatment5", eElement1.getAttribute("bookingTreatment5"));

									productmap.put("PriceList", eElement.getAttribute("PriceList"));
									productmap.put("Term", eElement.getAttribute("Term"));
									productmap.put("RecordType", eElement.getAttribute("Recordtype"));
									quoteProductsListDataMap.add(productmap);

								}
							}
						}
					}

				}

			}
			//To print the values of quoteProductsListDataMap
			for (int i=0; i<quoteProductsListDataMap.size(); i++){
				HashMap<String, String> hm = (HashMap) quoteProductsListDataMap.get(i);
				for (Entry<String, String> tempHM : hm.entrySet()) {
					//LOG.info("key*** = " + tempHM.getKey()+ "   :  value*** = " + tempHM.getValue());
				}
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(fXmlFile));
			transformer.transform(source, result);
			LOG.info("Updating QuoteProducts.xml with Monopoly Products are done");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("Updating QuoteProducts.xml with Monopoly Products failed");
		}

		return quoteProductsListDataMap;
	}
	public static HashMap<String, String> getMonopolyProductsFromDB(PartnerConnection connection) throws Exception
	{
		HashMap<String, String> monopolyDatabaseProductsmap = new HashMap<String, String>();
		try{
			//ADD Monopoly queries here 
			//Example of 01-ACV-Recurring-License&Support is SalesforceIQ - Starter
			/*String bookingTreatmentId1 = "SELECT Product2Id FROM PricebookEntry  "
					+ "WHERE "
					+ "Pricebook2.Name = 'CPQ - Direct - Commercial - Ohana - WW - USD'"
					+ "AND Product2.Quotable__c = 'Yes'"
					+ "AND Product2.sfbase__Status__c = 'Active'"
					+ "AND Product2.IsActive = true "
					+ "AND BillingFrequency = 12 "
					+ "AND Product2.SfdcRestrictedProductsApprover__c = null "
					+ "AND Product2.Bookings_Treatment__c = '01-ACV-Recurring-License&Support' "
					+ "LIMIT 1";*/
			
			String bookingTreatmentId1 = "SELECT "
					+ "Product2Id, Product2.name  "
					+ "FROM PricebookEntry "
					+ "WHERE Pricebook2.Name = 'CPQ - Direct - Commercial - Ohana - WW - USD' "
					+ "AND Product2.Quotable__c = 'Yes' "
					+ "AND Product2.sfbase__Status__c = 'Active' "
					+ "AND Product2.IsActive = true "
					+ "AND BillingFrequency = 12 "
					+ "AND Product2.SfdcRestrictedProductsApprover__c = null "
					+ "AND Product2.Bookings_Treatment__c = '01-ACV-Recurring-License&Support' "
					+ "AND (NOT Product2.name like '%Restricted%') "
					+ "AND (NOT Product2.name like '%Emerging Market%') "
					+ "AND Product2.name like '%Sales Cloud%' "
					+ "AND unitprice >100 "
					+ "AND unitprice <1000 "
					+ "LIMIT 1";
			
			SObject[] bookingTreatmentID1Object = ApiUtilities.executeQuery(connection, bookingTreatmentId1);
			String monopolyProductId1 = (String)bookingTreatmentID1Object[0].getField("Product2Id");
			if(monopolyProductId1.equals(null))
			{
				LOG.info(" : For bookingtreatment 01-ACV-Recurring-License&Support No Products are available");

			}
			else
			{
				String bookingTreatment1 = "SELECT name from Product2 where id ='"+monopolyProductId1+"'";
				SObject[] bookingTreatment1Object = ApiUtilities.executeQuery(connection, bookingTreatment1);
				String monopolyProductName1 = (String)bookingTreatment1Object[0].getField("Name");
				monopolyDatabaseProductsmap.put("bookingTreatment1",monopolyProductName1);
				LOG.info("Adding BookingTreatment=01-ACV-Recurring-License&Support, Monopoly Product to monopolyDatabaseProductsmap --> "+ monopolyDatabaseProductsmap.get("bookingTreatment1"));
			}

			//Example of product for 04-NonACV-NonRecurring-License&Support   =  Developer Support - 10 Cases
			// using Product name like developer cause Issues Data.com Product there is no suggested products showing up for  Data.com Corporate Records Additional product so going with above product.
			/*String bookingTreatmentId2 = "SELECT Product2Id FROM PricebookEntry "
					+ "WHERE Pricebook2.Name = 'CPQ - Direct - Commercial - Ohana - WW - USD' "
					+ "AND Product2.name like '%developer%'"
					+ "AND Product2.Quotable__c = 'Yes' "
					+ "AND Product2.sfbase__Status__c = 'Active' "
					+ "AND Product2.IsActive = true "
					+ "AND Product2.SfdcRestrictedProductsApprover__c = null "
					+ "AND Product2.Bookings_Treatment__c = '04-NonACV-NonRecurring-License&Support' "
					+ "AND unitprice >0 "
					+ "limit 1";*/
			
			String bookingTreatmentId2 = "SELECT Product2Id "
					+ "FROM PricebookEntry "
					+ "WHERE Pricebook2.Name = 'CPQ - Direct - Commercial - Ohana - WW - USD'"
					+ "AND Product2.Quotable__c = 'Yes' "
					+ "AND Product2.sfbase__Status__c = 'Active' "
					+ "AND Product2.IsActive = true "
					+ "AND Product2.SfdcRestrictedProductsApprover__c = null "
					+ "AND Product2.Bookings_Treatment__c = '04-NonACV-NonRecurring-License&Support' "
					+ "AND unitprice >100 "
					+ "AND unitprice <1000"
					+ "AND (NOT Product2.name like '%Restricted%') "
					+ "AND (NOT Product2.name like '%Emerging Market%') "
					+ "limit 1";			
			
			SObject[] bookingTreatmentId2Object = ApiUtilities.executeQuery(connection, bookingTreatmentId2);
			String monopolyProductId2 = (String)bookingTreatmentId2Object[0].getField("Product2Id");
			if(monopolyProductId2.equals(null))
			{
				LOG.info(" : For bookingtreatment 04-NonACV-NonRecurring-License&Support  No Products are available");

			}
			else
			{
				String bookingTreatmentName2 = "SELECT name from Product2 where id ='"+monopolyProductId2+"'";
				SObject[] bookingTreatment2NameObject = ApiUtilities.executeQuery(connection, bookingTreatmentName2);
				String monopolyProductName2 = (String)bookingTreatment2NameObject[0].getField("Name");

				monopolyDatabaseProductsmap.put("bookingTreatment2",monopolyProductName2);
				LOG.info("Adding BookingTreatment=04-NonACV-NonRecurring-License&Support Monopoly Products to  monopolyDatabaseProductsmap --> "+ monopolyDatabaseProductsmap.get("bookingTreatment2"));
			}

			//Example of product for 06-NonACV-NonRecurring-Services   = Desk.com - Configuration + (Tier 2)
			/*String bookingTreatmentId3 = "SELECT Product2Id FROM PricebookEntry "
					+ "WHERE Pricebook2.Name = 'CPQ - Direct - Commercial - Ohana - WW - USD' "
					+ "AND Product2.Quotable__c = 'Yes' "
					+ "AND Product2.sfbase__Status__c = 'Active' "
					+ "AND Product2.IsActive = true "
					+ "AND Product2.SfdcRestrictedProductsApprover__c = null "
					+ "AND Product2.Bookings_Treatment__c = '06-NonACV-NonRecurring-Services' "
					+ "AND unitprice >0 "
					+ "limit 1";*/			
			
			String bookingTreatmentId3 = "SELECT Product2Id "
					+ "FROM PricebookEntry "
					+ "WHERE Pricebook2.Name = 'CPQ - Direct - Commercial - Ohana - WW - USD' "
					+ "AND Product2.Quotable__c = 'Yes' "
					+ "AND Product2.sfbase__Status__c = 'Active' "
					+ "AND Product2.IsActive = true "
					+ "AND Product2.SfdcRestrictedProductsApprover__c = null "
					+ "AND Product2.Bookings_Treatment__c = '06-NonACV-NonRecurring-Services' "
					+ "AND unitprice >0"
					+ "AND unitprice <1000	"
					+ "AND (NOT Product2.name like '%Restricted%')"
					+ "limit 1";
			
			SObject[] bookingTreatmentId3Object = ApiUtilities.executeQuery(connection, bookingTreatmentId3);
			String monopolyProductId3 = (String)bookingTreatmentId3Object[0].getField("Product2Id");
			if(monopolyProductId3.equals(null))
			{
				LOG.info(" : For bookingtreatment 06-NonACV-NonRecurring-Services No Products are available");

			}
			else
			{
				String bookingTreatmentName3 = "SELECT name from Product2 where id ='"+monopolyProductId3+"'";
				SObject[] bookingTreatment3NameObject = ApiUtilities.executeQuery(connection, bookingTreatmentName3);
				String monopolyProductName3 = (String)bookingTreatment3NameObject[0].getField("Name");
				monopolyDatabaseProductsmap.put("bookingTreatment3",monopolyProductName3);
				LOG.info("Adding BookingTreatment=06-NonACV-NonRecurring-Services Monopoly Products to  monopolyDatabaseProductsmap --> "+ monopolyDatabaseProductsmap.get("bookingTreatment3"));
			}

			//Example of product for 02-ACV-Recurring-Usage   = Heroku - Dev Starter
			/*String bookingTreatmentId4 = "SELECT Product2Id FROM PricebookEntry "
					+ "WHERE Pricebook2.Name = 'CPQ - Direct - Commercial - Ohana - WW - USD' "
					+ "AND Product2.Quotable__c = 'Yes' "
					+ "AND Product2.sfbase__Status__c = 'Active' "
					+ "AND Product2.IsActive = true "
					+ "AND BillingFrequency = 12 "
					+ "AND Product2.Bookings_Treatment__c = '02-ACV-Recurring-Usage' "
					+ "limit 1";*/			
			
			String bookingTreatmentId4 = "SELECT Product2Id "
					+ "FROM PricebookEntry "
					+ "WHERE Pricebook2.Name = 'CPQ - Direct - Commercial - Ohana - WW - USD' "
					+ "AND Product2.Quotable__c = 'Yes' "
					+ "AND Product2.sfbase__Status__c = 'Active' "
					+ "AND Product2.IsActive = true "
					+ "AND BillingFrequency = 12 "
					+ "AND Product2.SfdcRestrictedProductsApprover__c = null "
					+ "AND Product2.Bookings_Treatment__c = '02-ACV-Recurring-Usage' "
					+ "AND (NOT Product2.name like '%Restricted%') "
					+ "AND unitprice >0 "
					+ "AND unitprice <1000	"
					+ "limit 1";			
			
			SObject[] bookingTreatmentId4Object = ApiUtilities.executeQuery(connection, bookingTreatmentId4);
			String monopolyProductId4 = (String)bookingTreatmentId4Object[0].getField("Product2Id");
			if(monopolyProductId4.equals(null))
			{
				LOG.info(" : For bookingtreatment 02-ACV-Recurring-Usage No Products are available");

			}
			else
			{
				String bookingTreatmentName4 = "SELECT name from Product2 where id ='"+monopolyProductId4+"'";
				SObject[] bookingTreatment4NameObject = ApiUtilities.executeQuery(connection, bookingTreatmentName4);
				String monopolyProductName4 = (String)bookingTreatment4NameObject[0].getField("Name");
				monopolyDatabaseProductsmap.put("bookingTreatment4",monopolyProductName4);
				LOG.info("Adding BookingTreatment=02-ACV-Recurring-Usage Monopoly Products to  monopolyDatabaseProductsmap --> "+ monopolyDatabaseProductsmap.get("bookingTreatment4"));
			}


			//Example of product for 03-ACV-Recurring-Services - MC - Technology Specialist - Term
			String bookingTreatmentId5 = "SELECT Product2Id FROM PricebookEntry "
					+ "WHERE Pricebook2.Name = 'CPQ - Direct - Services - Marketing Cloud - WW - USD'"
					+ "AND Product2.Quotable__c = 'Yes' "
					+ "AND Product2.sfbase__Status__c = 'Active' "
					+ "AND Product2.IsActive = true "
					+ "AND Product2.SfdcRestrictedProductsApprover__c = null "
					+ "AND Product2.Bookings_Treatment__c = '03-ACV-Recurring-Services' "
					+ "AND unitprice >0 "
					+ "limit 1";
			SObject[] bookingTreatmentId5Object = ApiUtilities.executeQuery(connection, bookingTreatmentId5);
			String monopolyProductId5 = (String)bookingTreatmentId5Object[0].getField("Product2Id");
			if(monopolyProductId4.equals(null))
			{
				LOG.info(" : For bookingtreatment 003-ACV-Recurring-Services No Products are available");

			}
			else
			{
				String bookingTreatmentName5 = "SELECT name from Product2 where id ='"+monopolyProductId5+"'";
				SObject[] bookingTreatment5NameObject = ApiUtilities.executeQuery(connection, bookingTreatmentName5);
				String monopolyProductName5 = (String)bookingTreatment5NameObject[0].getField("Name");
				monopolyDatabaseProductsmap.put("bookingTreatment5",monopolyProductName5);
				LOG.info("Adding BookingTreatment=03-ACV-Recurring-Services Monopoly Products to  monopolyDatabaseProductsmap --> "+ monopolyDatabaseProductsmap.get("bookingTreatment5"));
			}
		}
		catch(Exception ex){
			LOG.info(" : monopoly validation failed. " + ex.getMessage());
		}
		return monopolyDatabaseProductsmap;

	}


	public boolean Monopoly_Validations(PartnerConnection connection,String OpptyID) throws Exception{
		try{

			String QuoteIdQuery = "SELECT Id FROM Apttus_Proposal__Proposal__c where Apttus_Proposal__Opportunity__c = '"+OpptyID+"' Order By CreatedDate Desc Limit 1";
			//System.out.println("QuoteIdQuery : "+QuoteIdQuery);
			SObject[] QuoteId1 = ApiUtilities.executeQuery(connection, QuoteIdQuery);
			//System.out.println("QuoteId1 : "+QuoteId1);
			String QuoteId=(String)QuoteId1[0].getField("Id");
			LOG.info("QuoteId for "+OpptyID+" : "+QuoteId);

			String query="SELECT Id FROM OpportunityLineItem where OpportunityId='"+OpptyID+"'";
			SObject[] OpptyLineItemID1 = ApiUtilities.executeQuery(connection, query);

			for(int i=0;i<=OpptyLineItemID1.length-1;i++)
			{

				String OpptyLineItemID=(String)OpptyLineItemID1[i].getField("Id");

				String opptyProduct2IdQuery = "SELECT PriceBookEntry.Product2Id FROM OpportunityLineItem WHERE Id = '"+OpptyLineItemID+"'";	
				SObject[] opptyProduct2Id = ApiUtilities.executeQuery(connection, opptyProduct2IdQuery);
				com.sforce.ws.bind.XmlObject test1 = opptyProduct2Id[0].getChild("PricebookEntry");
				com.sforce.ws.bind.XmlObject test2 = test1.getChild("Product2Id");
				String OpptyProduct2Id = (String)test2.getValue();

				LOG.info("OpptyProduct2Id:: "+OpptyProduct2Id);


				String OpptyProductNameQuery = "SELECT Name FROM Product2 WHERE Id = '"+OpptyProduct2Id+"'";
				SObject[] OpptyProductName1 = ApiUtilities.executeQuery(connection, OpptyProductNameQuery);
				String OpptyProductName=(String)OpptyProductName1[0].getField("Name");

				LOG.info("------------------------------------------------------------");
				LOG.info("         Validation for "+OpptyProductName+"                 ");
				LOG.info("------------------------------------------------------------");

				//if(OpptyProductName.equals("Adjustment - SFA")||OpptyProductName.equals("Adjustment - Support")||OpptyProductName.equals("Adjustment - Unallocated")){
				if(OpptyProductName.contains("Adjustment")||OpptyProductName.contains("Allocation")||OpptyProductName.contains("Developer Support")){
					LOG.info("Validations not applicable for "+OpptyProductName+" this product");	
				}
				else{
					String productBookingsTreatmentQuery = "SELECT Bookings_Treatment__c FROM Product2 WHERE Id = '"+OpptyProduct2Id+"'";
					SObject[] productBookingsTreatment1 = ApiUtilities.executeQuery(connection, productBookingsTreatmentQuery);
					String productBookingsTreatment=(String)productBookingsTreatment1[0].getField("Bookings_Treatment__c");

					LOG.info("Bookings Treatment for "+OpptyProductName+" : "+productBookingsTreatment);

					String OpptyBookingsTreatmentQuery = "SELECT BookingsTreatment__c FROM OpportunityLineItem WHERE Id = '"+OpptyLineItemID+"'";
					SObject[] opptyBookingsTreatment1 = ApiUtilities.executeQuery(connection, OpptyBookingsTreatmentQuery);
					String opptyBookingsTreatment=(String)opptyBookingsTreatment1[0].getField("BookingsTreatment__c");

					LOG.info("Bookings Treatment test for  "+OpptyProductName+" : "+opptyBookingsTreatment);

					String NCOQuery = "SELECT NCO__c FROM OpportunityLineItem WHERE Id = '"+OpptyLineItemID+"'";
					SObject[] NCO1 = ApiUtilities.executeQuery(connection, NCOQuery);
					String NCO=(String)NCO1[0].getField("NCO__c");

					//LOG.info("NCO for OpptyLineItem for "+OpptyProductName+" : "+NCO);

					if((NCO.equalsIgnoreCase("true")) && (productBookingsTreatment.equals("01-ACV-Recurring-License&Support")) && (opptyBookingsTreatment.equals("04-NonACV-NonRecurring-License&Support")) && (opptyBookingsTreatment.equals("06-NonACV-NonRecurring-Services")))
					{
						LOG.info("NCO for OpptyLineItem for "+OpptyProductName+" is  "+NCO+". Bookings Treatment on Product for "+OpptyProductName+" is "+productBookingsTreatment+" and Bookings Treatment on Oppty Line Item is "+opptyBookingsTreatment);
					}
					else if((NCO.equalsIgnoreCase("false")) && (productBookingsTreatment.equals(opptyBookingsTreatment))){
						LOG.info("NCO for OpptyLineItem for "+OpptyProductName+" is  "+NCO+". Bookings Treatment on Product for "+OpptyProductName+" is "+productBookingsTreatment+" and Bookings Treatment on Oppty Line Item is "+opptyBookingsTreatment);
					}

					String QuoteLineIdQuery = "SELECT QuoteLine__c FROM OpportunityLineItem WHERE Id = '"+OpptyLineItemID+"'";
					SObject[] QuoteLineId1 = ApiUtilities.executeQuery(connection, QuoteLineIdQuery);
					String QuoteLineId=(String)QuoteLineId1[0].getField("QuoteLine__c");
					LOG.info("QuoteLineId on Oppty for "+OpptyProductName+" : "+QuoteLineId);

					String QuoteLineIdQuery2 = "SELECT Id FROM Apttus_Proposal__Proposal_Line_Item__c where Apttus_Proposal__Proposal__c = '"+QuoteId+"' and Apttus_Proposal__Product__c = '"+OpptyProduct2Id+"'";
					SObject[] QuoteLineId_2 = ApiUtilities.executeQuery(connection, QuoteLineIdQuery2);
					for(int j=0;j<=QuoteLineId_2.length-1;j++){
						String QuoteLineId2=(String)QuoteLineId_2[j].getField("Id");
						LOG.info("QuoteLineId on Quote : "+QuoteLineId2);


						if(QuoteLineId.equals(QuoteLineId2)){
							LOG.info("Quote Line Id on OpptyLineItem is "+QuoteLineId+" and on Quote is "+QuoteLineId2+". Populated correctly");
							//Assert.assertTrue(false);

						}	
					}

					String QuoteLineEndDateQuery = "Select QuoteLineEndDate__c FROM OpportunityLineItem WHERE Id = '"+OpptyLineItemID+"'";
					SObject[] QuoteLineEndDate1 = ApiUtilities.executeQuery(connection, QuoteLineEndDateQuery);
					String QuoteLineEndDate=(String)QuoteLineEndDate1[0].getField("QuoteLineEndDate__c");
					LOG.info("QuoteLineEndDate  for "+OpptyProductName+" : "+QuoteLineEndDate);

					String QuoteLineEndDate_Query = "Select Apttus_QPConfig__EndDate__c	FROM Apttus_Proposal__Proposal_Line_Item__c where Id = '"+QuoteLineId+"'";
					SObject[] QuoteLine_EndDate1 = ApiUtilities.executeQuery(connection, QuoteLineEndDate_Query);
					String QuoteLine_EndDate=(String)QuoteLine_EndDate1[0].getField("Apttus_QPConfig__EndDate__c");
					LOG.info("QuoteLineEndDate for  : "+QuoteLine_EndDate);

					//Assert.assertEquals("Quote Line End Date not populated correctly", QuoteLine_EndDate, QuoteLineEndDate);
					if(!QuoteLineEndDate.equals(QuoteLine_EndDate)){
						LOG.info("Quote Line End Date on OpptyLineItem "+QuoteLineEndDate+" and on Quote is "+QuoteLine_EndDate+". Not populated correctly on Oppty Line Item");
						//Assert.assertTrue(false);
					}else{
						LOG.info("Quote Line End Date on OpptyLineItem "+QuoteLineEndDate+" and on Quote is "+QuoteLine_EndDate+". Populated correctly on Oppty Line Item");
					}
					
					
					String NonRevenueAmount_Query = "Select NonRevenueAmount__c FROM OpportunityLineItem WHERE Id = '"+OpptyLineItemID+"'";
					SObject[] NonRevenueAmount1 = ApiUtilities.executeQuery(connection, NonRevenueAmount_Query);
					String NonRevenueAmount=(String)NonRevenueAmount1[0].getField("NonRevenueAmount__c");
					LOG.info("Non Revenue Amount for "+OpptyProductName+" : "+NonRevenueAmount);

					if(NonRevenueAmount.equals("0.0") ){
						LOG.info("Non Revenue Amount for "+OpptyProductName+" rolls up to Oppty header");
					}

				}
			}

		}
		catch(Exception ex){
			LOG.info(" : monopoly validation failed. " + ex.getMessage());
			return false;
		}
		return true;
	}
}