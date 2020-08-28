package com.salesforce.automation.commonUI;

import com.salesforce.automation.commonAPI.CommonAPI;
import com.salesforce.automation.commonAPI.PageObjectApi;
import com.salesforce.automation.commonAPI.QuoteAPI;
import com.salesforce.automation.commonElements.QuotePageElements;
import com.salesforce.automation.pageconstants.PageConstants;
import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.UIUtilities;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class QuoteUI {
	private final WebDriver driver;
	private final WebDriverWait wait;
	private final UIUtilities uIUtilities;
	private final PageObjectApi pageObjectApi;
	private final ApiUtilities apiUtilities;
	private final PartnerConnection connection;
	private final QuoteAPI quoteAPI;
	private final CommonAPI commonAPI;
	public boolean SuggestedProductPopup = false;
	DateFormat df = new SimpleDateFormat("MMM dd, yyyy");

	Calendar startDate1 = new GregorianCalendar();
	Calendar endDate1 = new GregorianCalendar();

	Calendar startDate2 = new GregorianCalendar();
	Calendar endDate2 = new GregorianCalendar();

	Calendar startDate3 = new GregorianCalendar();
	Calendar endDate3 = new GregorianCalendar();


	private static final Logger LOG = LoggerFactory.getLogger(QuoteUI.class);
	DateFormat dateFormat = new SimpleDateFormat("YYYYMMdd");
	Date date = new Date();

	public QuoteUI(WebDriver driver, PartnerConnection connection, long wait) {
		this.driver = driver;
		this.connection = null;
		this.wait = new WebDriverWait(driver, wait);
		this.uIUtilities = new UIUtilities(driver, wait);
		this.pageObjectApi = new PageObjectApi();
		this.apiUtilities = new ApiUtilities();
		this.quoteAPI = new QuoteAPI();
		this.commonAPI = new CommonAPI();
	}

	String QuoteName = null;
	String QuoteUrl = null;
	String RenewalQuoteId = null;
	String HostName = null;
	String RenewalOpptyUrl = null;
	String CreateRenewalQuoteUrl = null;

	QuotePageElements quotePageElements = new QuotePageElements();



	public String createQuoteNewFlowRamp(WebDriver driver,
										 PartnerConnection connection,
										 String Platform,
										 String OpportunityId,
										 String RecordType,
										 String isMonopoly,
										 String Term,
										 String Product,
										 String isramp

	)
			throws InterruptedException, Exception {


		try {


			//1- Read Input parameters
			//Collecting PriceList, Qty, Product Name, IsMonopoly and Isramp details
			GetQuoteProducts xml = new GetQuoteProducts();
			ArrayList<GetXmlProducts> allProducts = new ArrayList<GetXmlProducts>();
			allProducts = xml.getProducts(RecordType, isMonopoly, isramp);

			LOG.info(" Executing Mutli-product flow");

			LOG.info(" : Querying Opportunity to get Account ID");
			String accountIdQuery = "SELECT AccountId, Name FROM Opportunity WHERE Id = '" + OpportunityId + "' Order By CreatedDate Desc Limit 1";
			SObject[] SObject_accountId = ApiUtilities.executeQuery(connection, accountIdQuery);
			String accountId = (String) SObject_accountId[0].getField("AccountId");
			String accountIdName = (String) SObject_accountId[0].getField("Name");
			LOG.info(" : account Id    : " + accountId);
			LOG.info(" : account Name  : " + accountIdName);
			LOG.info("");

			LOG.info(" : Querying Contact Id");
			String contactQuery = "SELECT Id, Name, Email, Phone FROM contact WHERE AccountId = '" + accountId + "' Order By CreatedDate Desc Limit 1";
			SObject[] SObject_contactId = ApiUtilities.executeQuery(connection, contactQuery);
			String contactId = (String) SObject_contactId[0].getField("Id");
			String contactName = (String) SObject_contactId[0].getField("Name");
			String contactEmail = (String) SObject_contactId[0].getField("Email");
			String contactPhone = (String) SObject_contactId[0].getField("Phone");
			LOG.info(" : contact Id    : " + contactId);
			LOG.info(" : contact Name  : " + contactName);
			LOG.info(" : contact Email : " + contactEmail);
			LOG.info(" : contact Phone : " + contactPhone);
			LOG.info("");

			//Quote Config Details
			//Quote Config Details
			LOG.info(" ==========================================================================");
			LOG.info(" ===================       Quote Config Details       =====================");
			LOG.info(" ==========================================================================");
			LOG.info(" : Quote Term        : " + allProducts.get(0).Term);
			LOG.info(" : Quote Price List  : " + allProducts.get(0).PriceList);
			LOG.info(" : Quote RecordType  : " + allProducts.get(0).RT);
			LOG.info(" ==========================================================================");

			//Quote Product details
			for (int j = 0; j < allProducts.size(); j++) {

				LOG.info(" : Product Name  : " + allProducts.get(j).Name);
				LOG.info(" : Product Qty   : " + allProducts.get(j).Qty);
				LOG.info("-----------------------------------------------------------------");
			}


			String currentUrl = null;
			String OpptytUrl = null;
			UserUI userUI = new UserUI(driver, connection, 360);
			String HostName = null;
			String quoteId = null;


			//2- Set Platform to classic or Lightning
			currentUrl = driver.getCurrentUrl();
			userUI.setUserPlatform(driver, connection, currentUrl);

			//3- move to Oppty Page
			LOG.info(" : Querying Oppty Details");
			String OpptyQuery = "SELECT Id, Name FROM Opportunity WHERE Id = '" + OpportunityId + "'";
			SObject[] SObject_Oppty = ApiUtilities.executeQuery(connection, OpptyQuery);
			String OpptyName_temp = (String) SObject_Oppty[0].getField("Name");

			LOG.info(" : Oppty Name  : " + OpptyName_temp);
			LOG.info(" : Building Oppty Url and Navigating to Oppty Page");
			HostName = apiUtilities.getHost(currentUrl);
			OpptytUrl = "https://" + HostName + "/" + OpportunityId;
			driver.get(OpptytUrl);

			//4- Click Create quote based on classic or Lightning
			if (Platform.equals("Lightning")) {
				quotePageElements.setLightningElements();

				LOG.info(" : Wait for Page title to load as - " + OpptyName_temp);
				uIUtilities.waitForPageTitleContains(OpptyName_temp);

				LOG.info(" : Wait for Oppty Name to display in Title section");
				uIUtilities.waitForXpath("//span[text()='" + OpptyName_temp + "']");

				LOG.info(" : Wait for Details Tab to load on Oppty Page");
				uIUtilities.waitForXpath(PageConstants.QUOTE_DETAILS);
				driver.findElement(By.xpath(PageConstants.QUOTE_DETAILS)).click();

				LOG.info(" : Quote Creation - Starts");

				LOG.info(" : Clicking One Action DropDown");
				uIUtilities.waitForXpath(quotePageElements.OneActionsDropDown_Xpath);
				driver.findElement(By.xpath(quotePageElements.OneActionsDropDown_Xpath)).click();
				uIUtilities.waitForPageToLoad(5000);

				LOG.info(" : Clicking Create Quote Button");
				uIUtilities.waitForXpath(quotePageElements.Create_Quote_Button_XPATH);
				driver.findElement(By.xpath(quotePageElements.Create_Quote_Button_XPATH)).click();
				LOG.info(": Create Quote Button clicked");

				uIUtilities.waitForPageToLoad(30000);
				ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
				//If Create quote tab is opening in different tab, check tab size and move to other tab ,other wise stay in same tab.
				if (tabs2.size() > 1) {
					LOG.info(": Selecting 2nd Tab in Same browser");
					driver.switchTo().window(tabs2.get(1));
					wait.until(ExpectedConditions.titleContains("Quote"));
				} else {
					uIUtilities.waitForPageTitleStartsWith("Quote");
				}
				LOG.info(": Quote creation tab is selected here.");
			} else if (Platform.equals("Classic")) {
				quotePageElements.setClassisElements();
				LOG.info(" : Clicking Create Quote Button");
				uIUtilities.waitForXpath(quotePageElements.Create_Quote_Button_XPATH);
				driver.findElement(By.xpath(quotePageElements.Create_Quote_Button_XPATH)).click();
				uIUtilities.waitForPageTitleStartsWith("Quote");
			} else {
				LOG.info(" : Platform Information is not passed correctly");
				System.exit(0);
				driver.close();
				driver.quit();
			}

			Thread.sleep(40000);
			//5- Quote config page
			quotePageElements.setLightningElements();
			Thread.sleep(40000);
			LOG.info(": Updating Quote Term.");

			driver.findElement(By.xpath(PageConstants.CPQCART_REVIETERM)).clear();
			driver.findElement(By.xpath(PageConstants.CPQCART_REVIETERM)).sendKeys(Term);


			LOG.info(" Clicking on Update Button");
			Thread.sleep(40000);

			driver.findElement(By.xpath(PageConstants.CPQCART_UpdateButton)).click();

			LOG.info(" Clicked on Update Button");

			Thread.sleep(40000);

			//Pricing_Update_Button_XPATH

			LOG.info(": Clicking Product Selection Button");
			Thread.sleep(30000);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(PageConstants.ProdSelect_Button)));

			driver.findElement(By.xpath(PageConstants.ProdSelect_Button)).click();
			Thread.sleep(20000);

			//6- Product Selection page
			//Search and select products
			for (int j = 0; j < allProducts.size(); j++) {

				Thread.sleep(5000);
				LOG.info(" : Product Name  : " + allProducts.get(j).Name);
				uIUtilities.waitForXpath(quotePageElements.Product_Selection_SearchProducts_TextBox_XPATH);
				driver.findElement(By.xpath(quotePageElements.Product_Selection_SearchProducts_TextBox_XPATH)).clear();
				driver.findElement(By.xpath(quotePageElements.Product_Selection_SearchProducts_TextBox_XPATH)).sendKeys(allProducts.get(j).Name);
				Thread.sleep(5000);

				uIUtilities.waitForXpathToBeClickable("//div[@class='slds-grid slds-col']//table//tr[@data-name='" + allProducts.get(j).Name + "']//td[3]/div/span");
				//driver.findElement(By.xpath("//div[@class='slds-grid slds-col']//table//tr[@data-name='" + allProducts.get(j).Name + "']//td[3]/div/span")).click();
				driver.findElement(By.xpath("//div[@class='slds-grid slds-col']//table//tr[@data-name='" + allProducts.get(j).Name + "']//td[3]/div/span")).click();

				LOG.info(" : Product Name  : Selected");
				Thread.sleep(5000);

				LOG.info(" : Product Qty   : " + allProducts.get(j).Qty);
				Thread.sleep(2000);
				driver.findElement(By.xpath("//div[text()='" + allProducts.get(j).Name + "']/following::td[1]/div/input")).click();
				Thread.sleep(2000);
				driver.findElement(By.xpath("//div[text()='" + allProducts.get(j).Name + "']/following::td[1]/div/input")).clear();
				Thread.sleep(2000);
				driver.findElement(By.xpath("//div[text()='" + allProducts.get(j).Name + "']/following::td[1]/div/input")).sendKeys(allProducts.get(j).Qty);
				Thread.sleep(2000);
				LOG.info(" : Product Qty   : entered");
				LOG.info(" : -------------------------------------------------------------------");
				Thread.sleep(3000);
			}


			Thread.sleep(20000);
			//7- Pricing button click
			LOG.info(" : Pricing button click ");

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(quotePageElements.Product_Selection_Pricing_Button_XPATH))).click();

			LOG.info(" : Waiting for 'Processing auto included products' text to display");


			if (Product.equalsIgnoreCase("ThreeProductsWith36Months")) {
				//- RAMP Flow
				if (isramp.toLowerCase() == "true") {
					LOG.info(" : Its RAMP Scenario");
					Thread.sleep(20000);
					WebElement elementToHover = driver.findElement(By.xpath("//div[@class='slds-dropdown-trigger']"));
					elementToHover.click();
					WebElement elementToClick = driver.findElement(By.partialLinkText("Clone"));
					elementToClick.click();
					Thread.sleep(3000);

					startDate1.add(Calendar.MONTH, 12);
					endDate1.add(Calendar.MONTH, 24);
					endDate1.add(Calendar.DATE, -1);

					startDate2.add(Calendar.MONTH, 24);
					endDate2.add(Calendar.MONTH, 12);
					endDate2.add(Calendar.DATE, -1);
					endDate2.add(Calendar.YEAR, 1);


					startDate3.add(Calendar.MONTH, 36);
					endDate3.add(Calendar.MONTH, 48);
					endDate3.add(Calendar.DATE, -1);
					endDate2.add(Calendar.YEAR, 1);

					LOG.info("-------------------------------------------------");
					LOG.info(" Product-1 StartDate : " + df.format(startDate1.getTime()));
					LOG.info(" Product-1 EndDate   : " + df.format(endDate1.getTime()));
					LOG.info("-------------------------------------------------");
					LOG.info(" Product-2 StartDate : " + df.format(startDate2.getTime()));
					LOG.info(" Product-2 EndDate   : " + df.format(endDate2.getTime()));
					LOG.info("-------------------------------------------------");

					LOG.info(" Product-3 StartDate : " + df.format(startDate3.getTime()));
					LOG.info(" Product-3 EndDate   : " + df.format(endDate3.getTime()));
					LOG.info("-------------------------------------------------");


					setTripleProducts(driver);
				}
			} else if (Product.equalsIgnoreCase("DoubleProductWith24Months")) {

				if (isramp.toLowerCase() == "true") {
					LOG.info(" : Its RAMP Scenario Double");

					startDate1.add(Calendar.MONTH, 12);
					endDate1.add(Calendar.MONTH, 24);
					endDate1.add(Calendar.DATE, -1);

					startDate2.add(Calendar.MONTH, 24);
					endDate2.add(Calendar.MONTH, 24);
					endDate2.add(Calendar.DATE, -1);
					endDate2.add(Calendar.YEAR, 1);


					LOG.info("-------------------------------------------------");
					LOG.info(" Product-1 StartDate : " + df.format(startDate1.getTime()));
					LOG.info(" Product-1 EndDate   : " + df.format(endDate1.getTime()));
					LOG.info("-------------------------------------------------");
					LOG.info(" Product-2 StartDate : " + df.format(startDate2.getTime()));
					LOG.info(" Product-2 EndDate   : " + df.format(endDate2.getTime()));
					LOG.info("-------------------------------------------------");


					setDoubleProducts(driver, " Updating Product Line-1 StartDate and EndDate", "//table//tbody/tr[1]/td[7]//input", "//table//tbody/tr[1]/td[8]//input", startDate1, endDate1, " Updating Product Line-2 StartDate and EndDate", "//table//tbody/tr[2]/td[7]//input", "//table//tbody/tr[2]/td[8]//input", startDate2, endDate2);
				}
			}
			else if (Product.equalsIgnoreCase("RAMPANDNCO")) {
				//- RAMP Flow
				if (isramp.toLowerCase() == "true") {
					LOG.info(" : Its RAMP Scenario");
					Thread.sleep(20000);
					WebElement elementToHover = driver.findElement(By.xpath("//div[@class='slds-dropdown-trigger']"));
					elementToHover.click();
					WebElement elementToClick = driver.findElement(By.partialLinkText("Clone"));
					elementToClick.click();
					Thread.sleep(3000);


					startDate1.add(Calendar.MONTH, 12);
					endDate1.add(Calendar.MONTH, 24);
					endDate1.add(Calendar.DATE, -1);

					startDate2.add(Calendar.MONTH, 13);
					endDate2.add(Calendar.MONTH, 24);
					endDate2.add(Calendar.DATE, -1);


					startDate3.add(Calendar.MONTH, 12);
					endDate3.add(Calendar.MONTH, 23);
					endDate3.add(Calendar.DATE, -1);

					LOG.info("-------------------------------------------------");
					LOG.info(" Product-1 StartDate : " + df.format(startDate1.getTime()));
					LOG.info(" Product-1 EndDate   : " + df.format(endDate1.getTime()));
					LOG.info("-------------------------------------------------");
					LOG.info(" Product-2 StartDate : " + df.format(startDate2.getTime()));
					LOG.info(" Product-2 EndDate   : " + df.format(endDate2.getTime()));
					LOG.info("-------------------------------------------------");

					LOG.info(" Product-3 StartDate : " + df.format(startDate3.getTime()));
					LOG.info(" Product-3 EndDate   : " + df.format(endDate3.getTime()));
					LOG.info("-------------------------------------------------");


					setTripleProducts(driver);
				}
			}
			else if (Product.equalsIgnoreCase("DifferentStartDateWith24Months")) {
				if (isramp.toLowerCase() == "true") {
					LOG.info(" : Its Different Date Scenario Double");

					startDate1.add(Calendar.MONTH, 12);
					endDate1.add(Calendar.MONTH, 24);
					endDate1.add(Calendar.DATE, -1);

					startDate2.add(Calendar.MONTH, 13);
					startDate2.add(Calendar.DATE, +1);
					endDate2.add(Calendar.MONTH, 24);
					endDate2.add(Calendar.DATE, -1);
					endDate2.add(Calendar.YEAR, 1);


					LOG.info("-------------------------------------------------");
					LOG.info(" Product-1 StartDate : " + df.format(startDate1.getTime()));
					LOG.info(" Product-1 EndDate   : " + df.format(endDate1.getTime()));
					LOG.info("-------------------------------------------------");
					LOG.info(" Product-2 StartDate : " + df.format(startDate2.getTime()));
					LOG.info(" Product-2 EndDate   : " + df.format(endDate2.getTime()));
					LOG.info("-------------------------------------------------");


					setDoubleProducts(driver, " Updating Product Line-1 StartDate and EndDate", "//table//tbody/tr[1]/td[7]//input", "//table//tbody/tr[1]/td[8]//input", startDate1, endDate1, " Updating Product Line-2 StartDate and EndDate", "//table//tbody/tr[2]/td[7]//input", "//table//tbody/tr[2]/td[8]//input", startDate2, endDate2);

				}
			}
			else if (Product.equalsIgnoreCase("NCOProductOnly")) {
				if (isramp.toLowerCase() == "true") {
					LOG.info(" : Its Different Date Scenario Double");

					startDate1.add(Calendar.MONTH, 12);
					endDate1.add(Calendar.MONTH, 24);
					endDate1.add(Calendar.DATE, -1);


					startDate2.add(Calendar.MONTH, 12);
					endDate2.add(Calendar.MONTH, 23);
					endDate2.add(Calendar.DATE, -1);

					LOG.info("-------------------------------------------------");
					LOG.info(" Product-1 StartDate : " + df.format(startDate1.getTime()));
					LOG.info(" Product-1 EndDate   : " + df.format(endDate1.getTime()));
					LOG.info("-------------------------------------------------");
					LOG.info(" Product-2 StartDate : " + df.format(startDate2.getTime()));
					LOG.info(" Product-2 EndDate   : " + df.format(endDate2.getTime()));
					LOG.info("-------------------------------------------------");

					setDoubleProducts(driver, " Updating Product Line-1 StartDate and EndDate", "//table//tbody/tr[1]/td[7]//input", "//table//tbody/tr[1]/td[8]//input", startDate1, endDate1, " Updating Product Line-2 StartDate and EndDate", "//table//tbody/tr[2]/td[7]//input", "//table//tbody/tr[2]/td[8]//input", startDate2, endDate2);

				}
			}


			LOG.info(" : Querying Quote Id From Oppty");
			String quoteIdQuery = "SELECT Id, Name FROM Apttus_Proposal__Proposal__c WHERE Apttus_Proposal__Opportunity__c = '" + OpportunityId + "' Order By CreatedDate Desc Limit 1";
			SObject[] SObject_quotetId = ApiUtilities.executeQuery(connection, quoteIdQuery);
			String quoteId_temp = (String) SObject_quotetId[0].getField("Id");
			String quoteName_temp = (String) SObject_quotetId[0].getField("Name");
			LOG.info(" : Quote Id    : " + quoteId_temp);
			LOG.info(" : Quote NAme  : " + quoteName_temp);


			//Updating Contact info in quote object
			LOG.info(" : Updating Contact Id, Email and Phone info in quote object");
			commonAPI.updateField(connection, "Apttus_Proposal__Proposal__c", quoteId_temp, "SfdcBillingContact__c", contactId);
			commonAPI.updateField(connection, "Apttus_Proposal__Proposal__c", quoteId_temp, "SfdcBillingEmail__c", contactEmail);
			commonAPI.updateField(connection, "Apttus_Proposal__Proposal__c", quoteId_temp, "SfdcBillingPhone__c", contactPhone);

			LOG.info(" : Querying Quote Id From Oppty");

			driver.findElement(By.xpath("//button[text()='Billing Information']")).click();
			Thread.sleep(5000);

			LOG.info(" : Waiting for text Invisible 'Clicked on Billing info button and Updating Pricing Cart, please wait...'");
			uIUtilities.waitForXpathToInvisible(quotePageElements.Pricing_BillingInfo_Button_UpdateCart_Spinner_XPATH);
			uIUtilities.waitForPageToLoad(5000);

			Thread.sleep(10000);
			LOG.info(" : Click Quote Summary button");
			uIUtilities.waitForXpath(quotePageElements.Special_Term_Quote_Summary_Button_XPATH);
			driver.findElement(By.xpath(quotePageElements.Special_Term_Quote_Summary_Button_XPATH)).click();

			Thread.sleep(10000);

			LOG.info(" : Click Complete button");
			uIUtilities.waitForXpath(quotePageElements.Quote_Header_Complete_Button_XPATH);
			driver.findElement(By.xpath(quotePageElements.Quote_Header_Complete_Button_XPATH)).click();

			LOG.info(" : Wait for Page title to load as - " + quoteName_temp);
			uIUtilities.waitForPageTitleContains(quoteName_temp);


			LOG.info(" : Wait for Quote Name to display in Title section");
			uIUtilities.waitForXpath("//span[text()='" + quoteName_temp + "']");


			LOG.info(" : Wait for Details Tab to load on Quote Page");
			LOG.info(" : Quote creation completed");
			return quoteId_temp;

		}//End of Try block
		catch (Exception ex) {
			LOG.info("Screen shot taken");
			LOG.info(" : Entered catch " + ex.getMessage());

			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File("target/surefire-reports/screenshot/screenshot_" + dateFormat.format(date) + ".jpeg"));
			LOG.info(" : Screenshot captured");
			return null;
		}
	}

	private void setDoubleProducts(WebDriver driver, String s, String s2, String s3, Calendar startDate1, Calendar endDate1, String s4, String s5, String s6, Calendar startDate2, Calendar endDate2) throws InterruptedException {
		LOG.info(s);
		String StartDate1_xpath = s2;
		String EndDate1_xpath = s3;
		uIUtilities.waitForXpath(StartDate1_xpath);
		driver.findElement(By.xpath(StartDate1_xpath)).clear();
		driver.findElement(By.xpath(StartDate1_xpath)).sendKeys(df.format(startDate1.getTime()));
		uIUtilities.waitForXpath(EndDate1_xpath);
		driver.findElement(By.xpath(EndDate1_xpath)).clear();
		driver.findElement(By.xpath(EndDate1_xpath)).sendKeys(df.format(endDate1.getTime()));

		LOG.info(s4);
		String StartDate2_xpath = s5;
		String EndDate2_xpath = s6;
		uIUtilities.waitForXpath(StartDate2_xpath);
		driver.findElement(By.xpath(StartDate2_xpath)).clear();
		driver.findElement(By.xpath(StartDate2_xpath)).sendKeys(df.format(startDate2.getTime()));
		uIUtilities.waitForXpath(EndDate2_xpath);
		driver.findElement(By.xpath(EndDate2_xpath)).clear();
		driver.findElement(By.xpath(EndDate2_xpath)).sendKeys(df.format(endDate2.getTime()));


		LOG.info(" Clicking Update Button");
		//Pricing_Update_Button_XPATH
		uIUtilities.waitForXpath(quotePageElements.Pricing_Update_Button_XPATH);
		driver.findElement(By.xpath(quotePageElements.Pricing_Update_Button_XPATH)).click();

		//wait for Pricing Cart text to display
		uIUtilities.waitForTextToDisplay(driver, "//span[contains(text(), 'Pricing Cart')]", 15);
		//wait for Pricing Cart text to not display
		uIUtilities.waitForTextNotToDisplay(driver, "//span[contains(text(), 'Pricing Cart')]", 120);
	}

	private void setTripleProducts(WebDriver driver) throws InterruptedException {
		LOG.info(" Updating Product Line-1 StartDate and EndDate");
		String StartDate1_xpath = "//table//tbody/tr[1]/td[7]//input";
		String EndDate1_xpath = "//table//tbody/tr[1]/td[8]//input";
		uIUtilities.waitForXpath(StartDate1_xpath);
		driver.findElement(By.xpath(StartDate1_xpath)).clear();
		driver.findElement(By.xpath(StartDate1_xpath)).sendKeys(df.format(startDate1.getTime()));
		uIUtilities.waitForXpath(EndDate1_xpath);
		driver.findElement(By.xpath(EndDate1_xpath)).clear();
		driver.findElement(By.xpath(EndDate1_xpath)).sendKeys(df.format(endDate1.getTime()));

		setDoubleProducts(driver, " Updating Product Line-2 StartDate and EndDate", "//table//tbody/tr[2]/td[7]//input", "//table//tbody/tr[2]/td[8]//input", startDate2, endDate2, " Updating Product Line-3 StartDate and EndDate", "//table//tbody/tr[3]/td[7]//input", "//table//tbody/tr[3]/td[8]//input", startDate3, endDate3);
	}

	public String createQuoteNewFlowNoRamp(WebDriver driver,
										   PartnerConnection connection,
										   String Platform,
										   String OpportunityId,
										   String RecordType,
										   String isMonopoly,
										   String Term,
										   String Product,
										   String isramp
	) throws InterruptedException, Exception {
		try {

			//1- Read Input parameters
			//Collecting PriceList, Qty, Product Name, IsMonopoly and Isramp details
			GetQuoteProducts xml = new GetQuoteProducts();
			ArrayList<GetXmlProducts> allProducts = new ArrayList<GetXmlProducts>();
			allProducts = xml.getProducts(RecordType, isMonopoly, isramp);

			LOG.info(" Executing NoRamp-product flow");

			LOG.info(" : Querying Opportunity to get Account ID");
			String accountIdQuery = "SELECT AccountId, Name FROM Opportunity WHERE Id = '" + OpportunityId + "' Order By CreatedDate Desc Limit 1";
			SObject[] SObject_accountId = ApiUtilities.executeQuery(connection, accountIdQuery);
			String accountId = (String) SObject_accountId[0].getField("AccountId");
			String accountIdName = (String) SObject_accountId[0].getField("Name");
			LOG.info(" : account Id    : " + accountId);
			LOG.info(" : account Name  : " + accountIdName);
			LOG.info("");

			LOG.info(" : Querying Contact Id");
			String contactQuery = "SELECT Id, Name, Email, Phone FROM contact WHERE AccountId = '" + accountId + "' Order By CreatedDate Desc Limit 1";
			SObject[] SObject_contactId = ApiUtilities.executeQuery(connection, contactQuery);
			String contactId = (String) SObject_contactId[0].getField("Id");
			String contactName = (String) SObject_contactId[0].getField("Name");
			String contactEmail = (String) SObject_contactId[0].getField("Email");
			String contactPhone = (String) SObject_contactId[0].getField("Phone");
			LOG.info(" : contact Id    : " + contactId);
			LOG.info(" : contact Name  : " + contactName);
			LOG.info(" : contact Email : " + contactEmail);
			LOG.info(" : contact Phone : " + contactPhone);
			LOG.info("");

			//Quote Config Details
			//Quote Config Details
			LOG.info(" ==========================================================================");
			LOG.info(" ===================       Quote Config Details       =====================");
			LOG.info(" ==========================================================================");
			LOG.info(" : Quote Term        : " + allProducts.get(0).Term);
			LOG.info(" : Quote Price List  : " + allProducts.get(0).PriceList);
			LOG.info(" : Quote RecordType  : " + allProducts.get(0).RT);
			LOG.info(" ==========================================================================");

			//Quote Product details
			for (int j = 0; j < allProducts.size(); j++) {

				LOG.info(" : Product Name  : " + allProducts.get(j).Name);
				LOG.info(" : Product Qty   : " + allProducts.get(j).Qty);
				LOG.info("-----------------------------------------------------------------");
			}


			String currentUrl = null;
			String OpptytUrl = null;
			UserUI userUI = new UserUI(driver, connection, 360);
			String HostName = null;
			String quoteId = null;


			//2- Set Platform to classic or Lightning
			currentUrl = driver.getCurrentUrl();
			userUI.setUserPlatform(driver, connection, currentUrl);

			//3- move to Oppty Page
			LOG.info(" : Querying Oppty Details");
			String OpptyQuery = "SELECT Id, Name FROM Opportunity WHERE Id = '" + OpportunityId + "'";
			SObject[] SObject_Oppty = ApiUtilities.executeQuery(connection, OpptyQuery);
			String OpptyName_temp = (String) SObject_Oppty[0].getField("Name");

			LOG.info(" : Oppty Name  : " + OpptyName_temp);
			LOG.info(" : Building Oppty Url and Navigating to Oppty Page");
			HostName = apiUtilities.getHost(currentUrl);
			OpptytUrl = "https://" + HostName + "/" + OpportunityId;
			driver.get(OpptytUrl);

			//4- Click Create quote based on classic or Lightning
			if (Platform.equals("Lightning")) {
				quotePageElements.setLightningElements();

				LOG.info(" : Wait for Page title to load as - " + OpptyName_temp);
				uIUtilities.waitForPageTitleContains(OpptyName_temp);

				LOG.info(" : Wait for Oppty Name to display in Title section");
				uIUtilities.waitForXpath("//span[text()='" + OpptyName_temp + "']");

				LOG.info(" : Wait for Details Tab to load on Oppty Page");
				uIUtilities.waitForXpath(PageConstants.QUOTE_DETAILS);
				driver.findElement(By.xpath(PageConstants.QUOTE_DETAILS)).click();

				LOG.info(" : Quote Creation - Starts");

				LOG.info(" : Clicking One Action DropDown");
				uIUtilities.waitForXpath(quotePageElements.OneActionsDropDown_Xpath);
				driver.findElement(By.xpath(quotePageElements.OneActionsDropDown_Xpath)).click();
				uIUtilities.waitForPageToLoad(5000);

				LOG.info(" : Clicking Create Quote Button");
				uIUtilities.waitForXpath(quotePageElements.Create_Quote_Button_XPATH);
				driver.findElement(By.xpath(quotePageElements.Create_Quote_Button_XPATH)).click();
				LOG.info(": Create Quote Button clicked");

				uIUtilities.waitForPageToLoad(30000);
				ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
				//If Create quote tab is opening in different tab, check tab size and move to other tab ,other wise stay in same tab.
				if (tabs2.size() > 1) {
					LOG.info(": Selecting 2nd Tab in Same browser");
					driver.switchTo().window(tabs2.get(1));
					wait.until(ExpectedConditions.titleContains("Quote"));
				} else {
					uIUtilities.waitForPageTitleStartsWith("Quote");
				}
				LOG.info(": Quote creation tab is selected here.");
			} else if (Platform.equals("Classic")) {
				quotePageElements.setClassisElements();
				LOG.info(" : Clicking Create Quote Button");
				uIUtilities.waitForXpath(quotePageElements.Create_Quote_Button_XPATH);
				driver.findElement(By.xpath(quotePageElements.Create_Quote_Button_XPATH)).click();
				uIUtilities.waitForPageTitleStartsWith("Quote");
			} else {
				LOG.info(" : Platform Information is not passed correctly");
				System.exit(0);
				driver.close();
				driver.quit();
			}

			Thread.sleep(40000);
			//5- Quote config page
			quotePageElements.setLightningElements();
			Thread.sleep(40000);
			LOG.info(": Updating Quote Term.");

			if(Product.equalsIgnoreCase("OneLineItemWith24Months")){

				driver.findElement(By.xpath(PageConstants.CPQCART_REVIETERM)).clear();
				driver.findElement(By.xpath(PageConstants.CPQCART_REVIETERM)).sendKeys(Term);


				LOG.info(" Clicking on Update Button");
				Thread.sleep(20000);

				driver.findElement(By.xpath(PageConstants.CPQCART_UpdateButton)).click();

				LOG.info(" Clicked on Update Button");
				Thread.sleep(20000);
				//Pricing_Update_Button_XPATH
				//  uIUtilities.waitForXpath(quotePageElements.Pricing_Update_Button_XPATH);
				//  driver.findElement(By.xpath(quotePageElements.Pricing_Update_Button_XPATH)).click();

				//wait for Pricing Cart text to display
				//  uIUtilities.waitForTextToDisplay(driver, "//span[contains(text(), 'Pricing Cart')]", 15);
				//wait for Pricing Cart text to not display
				//     uIUtilities.waitForTextNotToDisplay(driver, "//span[contains(text(), 'Pricing Cart')]", 120);

			}


			LOG.info(" : Querying Quote Id From Oppty");
			String quoteIdQuery = "SELECT Id, Name FROM Apttus_Proposal__Proposal__c WHERE Apttus_Proposal__Opportunity__c = '" + OpportunityId + "' Order By CreatedDate Desc Limit 1";
			SObject[] SObject_quotetId = ApiUtilities.executeQuery(connection, quoteIdQuery);
			String quoteId_temp = (String) SObject_quotetId[0].getField("Id");
			String quoteName_temp = (String) SObject_quotetId[0].getField("Name");
			LOG.info(" : Quote Id    : " + quoteId_temp);
			LOG.info(" : Quote NAme  : " + quoteName_temp);


			//Updating Contact info in quote object
			LOG.info(" : Updating Contact Id, Email and Phone info in quote object");
			commonAPI.updateField(connection, "Apttus_Proposal__Proposal__c", quoteId_temp, "SfdcBillingContact__c", contactId);
			commonAPI.updateField(connection, "Apttus_Proposal__Proposal__c", quoteId_temp, "SfdcBillingEmail__c", contactEmail);
			commonAPI.updateField(connection, "Apttus_Proposal__Proposal__c", quoteId_temp, "SfdcBillingPhone__c", contactPhone);

			LOG.info(" : Querying Quote Id From Oppty");

			driver.findElement(By.xpath("//button[text()='Billing Information']")).click();
			Thread.sleep(5000);

			LOG.info(" : Waiting for text Invisible 'Clicked on Billing info button and Updating Pricing Cart, please wait...'");
			uIUtilities.waitForXpathToInvisible(quotePageElements.Pricing_BillingInfo_Button_UpdateCart_Spinner_XPATH);
			uIUtilities.waitForPageToLoad(5000);

			Thread.sleep(10000);
			LOG.info(" : Click Quote Summary button");
			uIUtilities.waitForXpath(quotePageElements.Special_Term_Quote_Summary_Button_XPATH);
			driver.findElement(By.xpath(quotePageElements.Special_Term_Quote_Summary_Button_XPATH)).click();

			Thread.sleep(10000);

			LOG.info(" : Click Complete button");
			uIUtilities.waitForXpath(quotePageElements.Quote_Header_Complete_Button_XPATH);
			driver.findElement(By.xpath(quotePageElements.Quote_Header_Complete_Button_XPATH)).click();

			LOG.info(" : Wait for Page title to load as - " + quoteName_temp);
			uIUtilities.waitForPageTitleContains(quoteName_temp);


			LOG.info(" : Wait for Quote Name to display in Title section");
			uIUtilities.waitForXpath("//span[text()='" + quoteName_temp + "']");


			LOG.info(" : Wait for Details Tab to load on Quote Page");
			uIUtilities.waitForXpath(PageConstants.TabList_Details);
			driver.findElement(By.xpath(PageConstants.TabList_Details)).click();
			LOG.info(" : Quote creation completed");
			return quoteId_temp;

		}//End of Try block
		catch (Exception ex) {
			LOG.info("Screen shot taken");
			LOG.info(" : Entered catch " + ex.getMessage());

			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File("target/surefire-reports/screenshot/screenshot_" + dateFormat.format(date) + ".jpeg"));
			LOG.info(" : Screenshot captured");
			return null;
		}
	}


	public void closeSuggestedProductWindow(){

		List<String> namesofProducts = new ArrayList<String>();

		LOG.info(" : Check for is any Suggested Product Window Present");
		uIUtilities.waitForPageToLoad(15000);

		boolean isSuggestProductPopupBox = false;
		isSuggestProductPopupBox = uIUtilities.isElementPresent(driver, quotePageElements.Product_Selection_Suggested_ProductList_XPATH);

		List<WebElement> getAllSuggestProductNames = null;
		getAllSuggestProductNames = driver.findElements(By.xpath(quotePageElements.Product_Selection_Suggested_ProductList_XPATH));
		if(isSuggestProductPopupBox)
		{
			LOG.info(" : Trying to close Suggested Products Window ");
			driver.findElement(By.xpath("//div[@aria-labelledby='suggestedProductsHeader']//button[@title='Close']")).click();
			uIUtilities.waitForPageToLoad(5000);

		}else{
			SuggestedProductPopup = true;
			LOG.info(": No Suggested Products are available ");
		}
	}
}
