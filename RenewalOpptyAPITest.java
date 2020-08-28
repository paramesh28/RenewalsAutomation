package com.sforce.cd.Lunar;

import com.salesforce.automation.RenewalsTestBaseClass;
import com.salesforce.automation.commonAPI.*;
import com.salesforce.automation.commonUI.QuoteUI;
import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.UIUtilities;
import com.sforce.cd.kumonium.extensions.testng.SalesforceEnforcer;
import com.sforce.cd.kumonium.extensions.webdriver.KumoniumWebDriverExtension;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import org.ehoffman.testng.extensions.Fixture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Listeners(SalesforceEnforcer.class)
public class RenewalOpptyAPITest extends RenewalsTestBaseClass {

    private static final Logger LOG = LoggerFactory.getLogger(RenewalOpptyAPITest.class);
    public static String priceBookName = System.getProperty("priceBookName");
    public static String productName = System.getProperty("productName");
    public static String productNameOne = System.getProperty("productNameOne");
    public static String productNameTwo = System.getProperty("productNameTwo");
    public static String Currency = System.getProperty("Currency");
    public static String CloudServiceProvider = System.getProperty("CloudServiceProvider");
    //public static String Platform = System.getProperty("Platform");
    //public static String Quantity = System.getProperty("Quantity");

    @Test(groups = "functional", enabled = true)
    @Fixture(factory = {KumoniumWebDriverExtension.DESIRED_CAPABILITIES.class})
    public void RenewalOpptyAPITest(){

        System.setProperty("jobName", "Renewal-RenewalOpptyAPITest");
        System.setProperty("maxDuration", "10000");
        System.setProperty("commandTimeout", "500");
        System.setProperty("idleTimeout", "900");
        System.setProperty("screenResolution", "1280x1024");

        try {
            driver = Customsetup(driver);
            driver.manage().timeouts().pageLoadTimeout(240, TimeUnit.SECONDS);
            driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);

            new UIUtilities(driver, 360);

            TenantAPI tenantAPI = new TenantAPI();
            AccountAPI accountAPI = new AccountAPI();
            TenantInformationAPI tenantInformationAPI = new TenantInformationAPI();
            ContactAPI contactAPI = new ContactAPI();
            ContractAPI contractAPI = new ContractAPI();
            OpportunityAPI opportunityAPI = new OpportunityAPI();
            CommonAPI commonAPI = new CommonAPI();
            OrderAPI orderAPI = new OrderAPI();
            ProductAPI productAPI = new ProductAPI();
            MockOrgAPI mockOrgAPI = new MockOrgAPI();
            //QuoteUI quoteUI = new QuoteUI(driver, connection, Wait);

            GregorianCalendar ContractStartDate = new GregorianCalendar();
            LOG.info("ContractStartDate");

            // Create New Account
            String AccountId = accountAPI.createAccount(connection, "Sales");
            LOG.info("AccountIdConnectionSales");

            // Create Mock Org Id
            String mockOrgId = mockOrgAPI.createMockOrgId(connection, AccountId);
            String MockOrgName = mockOrgAPI.getMockOrgNameById(connection, mockOrgId);

            // Create Tenant
            String accountName = commonAPI.getFieldValue(connection, "Account", AccountId, "Name");
            String tenantId_Forcecom = tenantAPI.createTenant(connection, AccountId, "Force" + accountName, MockOrgName,
                    CloudServiceProvider);

            // Create Tenant Information
            tenantInformationAPI.createTenantInfo(connection, tenantId_Forcecom, AccountId, CloudServiceProvider,
                    MockOrgName);

            // Create New Contact on Account
            String contactId = contactAPI.creatContact(connection, AccountId);

            // Get priceBookId for pricebookname
            String priceBookId = productAPI.getPriceBookIdByName(connection, priceBookName);

            // Create New Contract on Account
            String contractId;
            if (Currency.equals("JPY")) {
                contractId = contractAPI.createContract_JPY(connection, AccountId, priceBookId, contactId,
                        ContractStartDate, 12);

            } else {
                contractId = contractAPI.createContract(connection, AccountId, priceBookId, contactId,
                        ContractStartDate, 12);
            }

            // Update Contract Status as Activated
            commonAPI.updateRecordStatus(connection, "Contract", contractId, "Activated");

            // Order Record Type
            /*This Method is mapped to Taleggio-ID T-2895039*/
            String Order_RT_ID = commonAPI.getRecordTypeId(connection, "Order", "New Business");

            // Create New Order
            /*This Method is mapped to Taleggio-ID T-2895040*/
            String orderId = orderAPI.createOrder(connection, contractId, ContractStartDate, 12, priceBookId,
                    Order_RT_ID, "New", "Standard", "Draft");

            String PricebookEntryId_Query = "SELECT Id," + " Pricebook2Id," + "Product2.name, " + "Product2.Id "
                    + "FROM PricebookEntry WHERE Pricebook2.Name = '" + priceBookName + "' "
                    + "AND Product2.Quotable__c = 'Yes' " + "AND Product2.sfbase__Status__c = 'Active'"
                    + " AND Product2.IsActive = true " + "AND unitprice >0" + " AND PricebookEntry.BillingFrequency=1"
                    + " AND Product2.name like '" + productName + "'";
            SObject[] SObject_PricebookEntryId_Query = ApiUtilities.executeQuery(connection, PricebookEntryId_Query);
            String pricebookEntryId = (String) SObject_PricebookEntryId_Query[0].getField("Id");

            // Add Order Line Item to Order
            orderAPI.createOrderLineItem(connection, orderId, 20, pricebookEntryId);

            String PricebookEntryId_Query_One = "SELECT Id, Pricebook2Id, Product2.name, Product2.Id"
                    + " FROM PricebookEntry WHERE Pricebook2.Name = '" + priceBookName + "'"
                    + " AND Product2.Quotable__c = 'Yes' AND Product2.sfbase__Status__c = 'Active'"
                    + " AND Product2.IsActive = true AND unitprice >0 AND PricebookEntry.BillingFrequency=1"
                    + " AND Product2.name like '" + productNameOne + "'";
            SObject[] SObject_PricebookEntryId_Query_One = ApiUtilities.executeQuery(connection, PricebookEntryId_Query_One);
            String pricebookEntryId_One = (String) SObject_PricebookEntryId_Query_One[0].getField("Id");

            // Add Order Line Item to Order
            /*This Method is mapped to Taleggio-ID T-2895038,  T-2895035*/
            orderAPI.createOrderLineItem(connection, orderId, 10, pricebookEntryId_One);

            String PricebookEntryId_Query_Two = "SELECT Id, Pricebook2Id, Product2.name, Product2.Id"
                    + " FROM PricebookEntry WHERE Pricebook2.Name = '" + priceBookName + "'"
                    + " AND Product2.Quotable__c = 'Yes' AND Product2.sfbase__Status__c = 'Active'"
                    + " AND Product2.IsActive = true AND unitprice >0 AND PricebookEntry.BillingFrequency=1"
                    + " AND Product2.name like '" + productNameTwo + "'";
            SObject[] SObject_PricebookEntryId_Query_Two = ApiUtilities.executeQuery(connection, PricebookEntryId_Query_Two);
            String PricebookEntryId_Two = (String) SObject_PricebookEntryId_Query_Two[0].getField("Id");

            // Add Order Line Item to Order
            /*This Method is mapped to Taleggio-ID T-2895039*/
            orderAPI.createOrderLineItem(connection, orderId, 10, PricebookEntryId_Two);

            // Order Activate Process
            /*This Method is mapped to Taleggio-ID T-2895040*/
            commonAPI.updateRecordStatus(connection, "Order", orderId, "Activated");
            commonAPI.updateDateField(connection, "Order", orderId, "ActivatedDate", ContractStartDate);

            // Order Provisioned Process
            commonAPI.updateRecordStatus(connection, "Order", orderId, "Provisioned");
            commonAPI.updateDateField(connection, "Order", orderId, "sfbase__OrdProvisionDatetime__c",
                    ContractStartDate);
            commonAPI.updateDateField(connection, "Order", orderId, "sfbase__PartnerProvisioningDate__c",
                    ContractStartDate);

            // Create Asset Line
            /*This Method is mapped to Taleggio-ID T-2895032*/
            contractAPI.createAssertLine(connection, contractId);

            // Create COSO
            /*This Method is mapped to Taleggio-ID T-2895038*/
            contractAPI.createCOSO(connection, contractId);
            LOG.info(" : Renewal Oppty: Create Renewal Oppty - Start");

            //Adding UI Call instead of APICall.
            String RenewalOpptyId = opportunityAPI.CreateRenewalOppty(connection, contractId);
            LOG.info(" : Renewal Oppty: Create Renewal Oppty - Done" + RenewalOpptyId);

            // OPSO Creation
            /*This Method is mapped to Taleggio-ID T-2895039*/
            opportunityAPI.CreateOPSO(connection, RenewalOpptyId);
            LOG.info(" : OPSO Creation Done");

            // RenewalValidations on FCV, Orders,
            /*This Method is mapped to Taleggio-ID T-2895038,T-2895040*/
            AssertValidations(connection, RenewalOpptyId, AccountId);
            calculationHybridAmount(connection, contractId);

        } catch (Exception ex) {
            LOG.info(" : Entered catch " + ex.getMessage());
            driver.close();
            Assert.assertNotNull("Renewal Oppty Failed");
        }
    }

    /*Assert Validations*/
    /*This Method is mapped to Taleggio-ID T-2895038,T-2895040,  T-2895041,  T-2895042, T-2895042, T-2895043, T-2895046,  T-2895034,T-2895033,  T-2895035*/
    public static void AssertValidations(PartnerConnection connection, String RenewalOpptyId, String AccountId) throws Exception {

        String OPSO_ListQuery = "SELECT sfbase__PriorAnnualOrderValue__c FROM sfbase__OpportunityProductSummary__c WHERE sfbase__Opportunity__c = '" + RenewalOpptyId + "' and sfbase__PriorAnnualOrderValue__c != 0.0";
        LOG.info("OPSO query" + OPSO_ListQuery);
        SObject[] OPSO_ListQuerySObject = ApiUtilities.executeQuery(connection, OPSO_ListQuery);
        String sfbase__ForecastedAnnualOrderValue__c=(String) OPSO_ListQuerySObject[0].getField("sfbase__ForecastedAnnualOrderValue__c");
        String sfbase__ForecastedChangeInACV__c =(String) OPSO_ListQuerySObject[0].getField("sfbase__ForecastedChangeInACV__c");

        String RenewalOpptyIdquery = "SELECT sfbase__ForecastedAnnualOrderValue__c,sfbase__ForecastedOTV__c,sfbase__ForecastedTotalOrderValue__c,sfbase__PriorMonthlyOrderValue__c,sfbase__PriorOTV__c,sfbase__PriorTotalOrderValue__c FROM sfbase__OpportunityProductSummary__c where sfbase__Opportunity__c = '" + RenewalOpptyId + "' and sfbase__PriorAnnualOrderValue__c != 0.0";
        SObject[] RenewalOpptyIdSObject = ApiUtilities.executeQuery(connection, RenewalOpptyIdquery);
        RenewalOpptyId = (String) RenewalOpptyIdSObject[0].getField("sfbase__ForecastedAnnualOrderValue__c");
        String ForecastedOTV = (String) RenewalOpptyIdSObject[0].getField("sfbase__ForecastedOTV__c");
        //String ForecastedTotalOrderValue = (String) RenewalOpptyIdSObject[0].getField("sfbase__ForecastedTotalOrderValue__c");
        //String PriorMonthlyOrderValue = (String) RenewalOpptyIdSObject[0].getField("sfbase__PriorMonthlyOrderValue__c");
        //String PriorOTV = (String) RenewalOpptyIdSObject[0].getField("sfbase__PriorOTV__c");
        //String PriorTotalOrderValue = (String) RenewalOpptyIdSObject[0].getField("sfbase__PriorTotalOrderValue__c");

        String OrderIdQuery = "SELECT sfbase__OrderLifeTimeValue__c,TotalAmount FROM Order where AccountId='" + AccountId + "'";
        SObject[] OrderIdQuerySObject = ApiUtilities.executeQuery(connection, OrderIdQuery);
        String OrderIdValue = (String) OrderIdQuerySObject[0].getField("sfbase__OrderLifeTimeValue__c");
        //String TotalAmount = (String) OrderIdQuerySObject[0].getField("TotalAmount");
        LOG.info(OrderIdValue);

        LOG.info("Asserting Opso Validations");
        LOG.info("RenewalOpptyId : " + RenewalOpptyId);
        LOG.info("ForecastedOTV : " + ForecastedOTV);
        LOG.info("ForecastedTotalOrderValue : " + sfbase__ForecastedChangeInACV__c);
        LOG.info("ForecastedTotalOrderValue : " + sfbase__ForecastedAnnualOrderValue__c);
    }


    /**
     * Sum total of Hybrid amount from COSOs validation - Using API call - @pdatrak
     *
     * @param connection
     * @param ContractId
     * @throws ConnectionException
     * @throws Exception
     */

    /*This Method is mapped to Taleggio-ID T-2895038, T-2895039, T-2895040,T-2895042,T-2895046, T-2895032, T-2895045*/
    public static void calculationHybridAmount(PartnerConnection connection, String ContractId){
        try {
            LOG.info(" : Querying for RM Brand from Contract id.");
            String RMBrandQuery = "Select Rm_Brand__c from Contract where id ='" + ContractId + "'";
            SObject[] SObject_RMBrand = ApiUtilities.executeQuery(connection, RMBrandQuery);
            String RMBrand = (String) SObject_RMBrand[0].getField("Rm_Brand__c");
            LOG.info(" : RMBrand   : " + RMBrand);

            List<String> ContractOrderSummaryValues = new ArrayList<String>();
            LOG.info(" : Querying for Contract Order Summary Values from Contract id.");
            String ContractOrderSummaryValuesQuery = "Select Id, sfbase__MonthlyOrderValue__c, sfbase__OTV__c From sfbase__ContractOrderSummary__c Where sfbase__Contract__c = '"
                    + ContractId + "' limit 1";
            SObject[] SObject_ContractOrderSummaryValues = ApiUtilities.executeQuery(connection,
                    ContractOrderSummaryValuesQuery);
            ContractOrderSummaryValues.add((String) SObject_ContractOrderSummaryValues[0].getField("Id"));
            ContractOrderSummaryValues.add((String) SObject_ContractOrderSummaryValues[0].getField("Name"));
            ContractOrderSummaryValues
                    .add((String) SObject_ContractOrderSummaryValues[0].getField("sfbase__MonthlyOrderValue__c"));
            ContractOrderSummaryValues.add((String) SObject_ContractOrderSummaryValues[0].getField("sfbase__OTV__c"));
            Assert.assertNotSame("COSOs not created", ContractOrderSummaryValues.size(), null);
            double hybridAmount = 0;
            for (int h = 0; h <= SObject_ContractOrderSummaryValues.length - 1; h++) {
                if (ContractOrderSummaryValues.get(3) != null) {
                    hybridAmount += Double.parseDouble(ContractOrderSummaryValues.get(3));
                }
            }
            LOG.info("Hybrid Amount: " + hybridAmount);

        } catch (Exception e) {
            LOG.info("Entered catch " + e.getMessage());
            e.printStackTrace();
        }
    }
}
