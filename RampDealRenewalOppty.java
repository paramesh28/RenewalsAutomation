package com.sforce.cd.Lunar;

import com.salesforce.automation.RenewalsTestBaseClass;
import com.salesforce.automation.commonUI.QuoteUI;
import com.salesforce.automation.commonUI.UserUI;
import com.sforce.cd.Lunar.helper.AssertCommonFields;
import com.sforce.cd.Lunar.helper.GenerateRenewalOppty;
import com.sforce.cd.kumonium.extensions.testng.SalesforceEnforcer;
import com.sforce.cd.kumonium.extensions.webdriver.KumoniumWebDriverExtension;
import com.sforce.soap.partner.PartnerConnection;
import org.ehoffman.testng.extensions.Fixture;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(SalesforceEnforcer.class)
public class RampDealRenewalOppty extends RenewalsTestBaseClass {

    private static final Logger LOG = LoggerFactory.getLogger(RampDealRenewalOppty.class);
    public static String priceBookName = System.getProperty("priceBookName");
    public static String productName = System.getProperty("productName");
    public static String Platform = System.getProperty("Platform");
    public static String Term = System.getProperty("Term");
    public static String Product = System.getProperty("Product");
    //public static String Currency = System.getProperty("Currency");
    //public static String CloudServiceProvider = System.getProperty("CloudServiceProvider");

    @Test(groups = "functional", enabled = true)
    @Fixture(factory = {KumoniumWebDriverExtension.DESIRED_CAPABILITIES.class})
    public void RampDealRenewalOppty(){

        System.setProperty("jobName", "Renewal-RampDealRenewalOppty");
        System.setProperty("maxDuration", "10000");
        System.setProperty("commandTimeout", "500");
        System.setProperty("idleTimeout", "900");
        System.setProperty("screenResolution", "1280x1024");

        try {
            GenerateRenewalOppty generateRenewalOppty = new GenerateRenewalOppty().invoke();
            UserUI userUI = generateRenewalOppty.getUserUI();
            QuoteUI quoteUI = generateRenewalOppty.getQuoteUI();
            String contractId = generateRenewalOppty.getContractId();
            String renewalOpptyId = generateRenewalOppty.getRenewalOpptyId();
            WebDriver driver = generateRenewalOppty.getDriver();

            if("SingleProductWith12Months".equalsIgnoreCase(Product)) {
                //Calling Quote flow for No Ramp Scenarios.
                /*This Method is mapped to   T-2994035,  T-2895024,  T-2895025,  T-2895026*/
                LOG.info(" : Entering Single Product Script");
                quoteUI.createQuoteNewFlowNoRamp(driver, connection, Platform, renewalOpptyId, "NB", "false", Term, Product,"false");
            }
            else if(("OneLineItemWith24Months".equalsIgnoreCase(Product))){
                //Calling Quote flow for One line item  Scenarios.
                /*This Method is mapped to Taleggio-ID T-2895038*/

                LOG.info(" : Entering OneLineItem Script");
                quoteUI.createQuoteNewFlowNoRamp(driver, connection, Platform, renewalOpptyId, "NB", "false", Term, Product,"false");
            }

            /*This Method is mapped to Taleggio-ID T-2895038, T-2895038*/
            else{
                LOG.info(" Calling Ramp Scenarios");
                quoteUI.createQuoteNewFlowRamp(driver, connection, Platform, renewalOpptyId, "NB", "false", Term, Product, "true");
            }

            //Asserting for Ramp Scenarios.
            /*This Method is mapped to Taleggio-ID T-2895038, T-2895038*/

            AssertValidations(connection, renewalOpptyId, contractId, Product, Term);

            //Logging out as Renewal Manager
            userUI.UiLogout(driver, connection, Platform);

        } catch (Exception ex) {
            LOG.info(" : Entered catch " + ex.getMessage());
            //driver.close();
           Assert.assertNotNull("Renewal Oppty Failed");
        }
    }

    /*Assert Validations*/
    /*This Method is mapped to Taleggio-ID T-2895038, T-2895038,  T-2994034,   T-2994035,  T-2895024,  T-2895025,  T-2895026*/
    public static void AssertValidations(PartnerConnection connection, String RenewalOpptyId, String ContractId, String Product,String Term) throws Exception {

        AssertCommonFields assertCommonFields = new AssertCommonFields(connection, RenewalOpptyId, ContractId, Term, priceBookName, productName).invoke();
        float amountValue = assertCommonFields.getAmountvalue();
        float forecasted_Attrition_float = assertCommonFields.getForecasted_attrition_float();
        float forecastedTotalContractValue = assertCommonFields.getForecastedTotalContractValue();
        float forecastedRenewalQuantity_Actual = assertCommonFields.getForecastedRenewalQuantity_actual();
        float forecastedContractTerm = assertCommonFields.getForecastedContractTerm();
        float priorAnnualOrderValue = assertCommonFields.getPriorAnnualOrderValue();
        float forecastedChangeInTotalACV = assertCommonFields.getForecastedChangeInTotalACV();
        float forecastedChangeInTotalACVCalc = assertCommonFields.getForecastedChangeInTotalACVCalc();
        float forecastedRenewalQuantity__c = assertCommonFields.getForecastedRenewalQuantity__c();
        float valueTerm = assertCommonFields.getValueTerm();
        float countProduct = assertCommonFields.getCountproduct();
        assertCommonFields.assertAdjustmentFields(amountValue,
                                                  forecasted_Attrition_float,
                                                  forecastedTotalContractValue,
                                                  forecastedRenewalQuantity_Actual,
                                                  forecastedContractTerm,
                                                  priorAnnualOrderValue,
                                                  forecastedChangeInTotalACV,
                                                  forecastedChangeInTotalACVCalc,
                                                  forecastedRenewalQuantity__c,
                                                  valueTerm,
                                                  countProduct);
    }
}
