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
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(SalesforceEnforcer.class)
public class ServiceRenewalTest extends RenewalsTestBaseClass {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceRenewalTest.class);
    public static String priceBookName = System.getProperty("priceBookName");
    public static String productName = System.getProperty("productName");
    public static String Platform = System.getProperty("Platform");
    public static String Term = System.getProperty("Term");
    public static String Product = System.getProperty("Product");
    public static String isramp = System.getProperty("isramp");
    public static String isservices = System.getProperty("isservices");
    //public static String Currency = System.getProperty("Currency");
    //public static String CloudServiceProvider = System.getProperty("CloudServiceProvider");
    /*This Validation is mapped to Taleggio-ID T-3117331, T-3117332, T-3117333, T-3117334 ,T-3117335 , T-3117336, T-3117337 */

    @Test(groups = "functional", enabled = true)
    @Fixture(factory = {KumoniumWebDriverExtension.DESIRED_CAPABILITIES.class})
    public void ServiceRenewalTest(){

        System.setProperty("jobName", "Renewal-ServiceRenewalTest");
        System.setProperty("maxDuration", "10000");
        System.setProperty("commandTimeout", "500");
        System.setProperty("idleTimeout", "900");
        System.setProperty("screenResolution", "1280x1024");

        /*This Validation is mapped to Taleggio-ID T-3117331, T-3117332 */

        try {
            GenerateRenewalOppty generateRenewalOppty = new GenerateRenewalOppty().invoke();
            UserUI userUI = generateRenewalOppty.getUserUI();
            QuoteUI quoteUI = generateRenewalOppty.getQuoteUI();
            String contractId = generateRenewalOppty.getContractId();
            String renewalOpptyId = generateRenewalOppty.getRenewalOpptyId();
            WebDriver driver = generateRenewalOppty.getDriver();
            quoteUI.createQuoteNewFlowRamp(driver, connection, Platform, renewalOpptyId, "NB", isservices, Term, Product, isramp) ;

            //Asserting for Ramp Scenarios.
            /*This Method is mapped to Taleggio-ID T-3117331, T-3117332, T-3117333*/
            AssertValidations(connection, renewalOpptyId, contractId, Product,Term);
        } catch (Exception ex) {
            LOG.info(" : Entered catch " + ex.getMessage());
        }
    }

    /*Assert Validations*/
    /*This Method is mapped to Taleggio-ID T-3117331, T-3117332, T-3117333, T-3117334 ,T-3117335 , T-3117336, T-3117337 */
    public static void AssertValidations(PartnerConnection connection, String RenewalOpptyId, String ContractId, String Product,String Term) throws Exception {

        AssertCommonFields assertCommonFields = new AssertCommonFields(connection, RenewalOpptyId, ContractId, Term,priceBookName,productName).invoke();
        float amountValue = assertCommonFields.getAmountvalue();
        float forecasted_Attrition_float = assertCommonFields.getForecasted_attrition_float();
        float forecastedAnnualOrderValue = assertCommonFields.getForecastedAnnualOrderValue();
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
                                                  forecastedAnnualOrderValue,
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

