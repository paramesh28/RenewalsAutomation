package com.salesforce.automation.pageobjects;

/**
 * This class is used for Quote page
 * @author jjayapal
 */

import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.UIUtilities;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class QuotePage {
	private final WebDriver driver;
	private final WebDriverWait wait;
	private final WebDriverWait wait1;
	private static final Logger LOG = LoggerFactory.getLogger(QuotePage.class);

	public final static String Create_Quote_Button_XPATH = "//input[@value='Create Quote']";
	public final static String Quote_Name_Title_XPATH = "//h2[@class='pageDescription']";
	public final static String Quote_Config_XPATH = "//*[@value='Configure']";
	public final static String Quote_Step_Menu_XPATH = "//*[@id='stepMenu']/div";

	public final static String Quote_Approvals_Button_XPATH = "//input[@value='Submit/Review Approvals']";
	public final static String Quote_Approvals_Submit_XPATH = "//input[@value='Submit']";
	public final static String Quote_Approvals_Return_XPATH = "//input[@value='Return']";

	public final static String Quote_Publish_Button_XPATH = "//input[@value='Publish']";
	public final static String Quote_Publish_Print_XPATH = "//input[@value='Print']";
	public final static String Quote_Publish_Next_XPATH = "//input[@value='Next']";
	public final static String Quote_Publish_Close_XPATH = "//input[@value='Close']";

	public final static String Quote_Submit_for_Convert_Button_XPATH = "//input[@value='Submit For Conversion']";
	public final static String Quote_Convert_Button_XPATH = "//input[@value='Convert Quote']";

	public final static String Quote_Price_List_XPATH = "//label[contains(text(),'Price List')]/../following-sibling::td/span/span/div/select";
	public final static String Quote_Term_XPATH = "//label[contains(text(), 'Term')]/parent::th/following::td[1]/span/div/input";
	public final static String SEARCH_TEXT_BOX_NAME = "j_id0:idForm:j_id530";
	public final static String SEARCH_BUTTON_NAME = "j_id0:idForm:j_id532";
	public static String ADD_TO_CART_BUTTON_XPATH;
	public final static String PRODUCT_SELECTED_TEXT_ID = "j_id0:idForm:j_id176:0:j_id207";
	public static String PRODUCT_SELECTED_TEXT_XPATH = "//div[@class='apt-added-indicator']";
	public final static String PRICING_BUTTON_ID = "CustomAction21";

	//public final static String Pricing_Button1_Xpath = "//ul[@class='pageButtons centerPageButtons']//child::a[contains(text(), 'Pricing')]";
	public final static String Pricing_Button1_Xpath = "//ul[@class='pageButtons centerPageButtons']//a[text()='Pricing']";
	//public final static String Pricing_Button2_Xpath = "//input[@id='SfdcUXSetPricing']";
	public final static String Pricing_Button2_Xpath = "//ul[@id='centerMenuBar']//input[@value ='Pricing']";
	public final static String QUANTITY_ID = "j_id0:idProductConfigurationForm:j_id167:0:j_id202:3:j_id210";
	public final static String QUANTITY_XPATH = "//input[@id='j_id0:idProductConfigurationForm:j_id167:0:j_id202:3:j_id210']";
	public final static String UPDATE_BUTTON_XPATH = "//li[@id='Reprice']/a";
	public final static String BILLING_INFO_BUTTON_XPATH = "//ul[@class='pageButtons centerPageButtons']//a[text()='Billing Info']";
	//public final static String BILLING_INFO_BUTTON_XPATH = "//a[contains(text(),'Billing Info')]";
	public final static String SPECIAL_TERMS_BUTTON_ID = "SfdcUXEditSpecialTerms";
	public final static String SPECIAL_TERMS_BUTTON_XPATH = "//ul[@id='centerMenuBar']//input[@value='Special Terms']";
	public final static String QUOTE_SUMMARY_BUTTON_XPATH = "//ul[@id='centerMenuBar']//input[@value='Quote Summary']";
	public final static String COMPLETE_BUTTON_XPATH = "//ul[@id='centerMenuBar']//input[@value='Complete']";
	public final static String CONTACT_SEARCH_ICON_BUTTON_ID = "j_id0:formId:j_id89:billingInfoSectionId:j_id186:j_id192_lkwgt";
	public final static String CONTACT_SEARCH_ICON_BUTTON_XPATH = "//img[@title = 'Billing Contact Lookup (New Window)']";
	public final static String CONTACT_NEW_BUTTON_NAME = "new";
	public final static String FNAME_ID = "name_first";
	public final static String LNAME_ID = "name_last";
	public final static String EMAIL_ID = "email";
	public final static String STREET_ID = "street";
	public final static String CITY_ID = "city";
	public final static String STATE_ID = "state";
	public final static String ZIP_CODE_ID = "zip";
	public final static String COUNTRY_ID = "country";
	public final static String PHONE1_ID = "phone1";
	public final static String SAVE_BUTTON_XPATH = "//*[@id='bottomButtonRow']/input[1]";
	public final static String BILLING_CONTACT_ID = "j_id0:formId:j_id89:billingInfoSectionId:j_id186:j_id192";
	public final static String BILLING_CONTACTEMAIL_ID = "j_id0:formId:j_id89:billingInfoSectionId:j_id203:billingEmailId";
	public final static String BILLING_CONTACT_PHONE_ID = "j_id0:formId:j_id89:billingInfoSectionId:j_id217:billingPhoneId";
	public final static String MODIFY_QUANTITY = "1";

	public final static String Quote_SelectProducts_XPATH = "//input[@id='SfdcSelectProducts']";
	public final static String Quote_Products_Type_XPATH = "//div[@id='idSearchDiv']/span/select";

	String currentUrl = null;
	String baseUrl = null;
	String pageTitle = null;
	UIUtilities uIUtilities;

	public QuotePage(WebDriver driver, long wait) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, wait);
		this.wait1 = new WebDriverWait(driver, 150);
		this.uIUtilities = new UIUtilities(driver, wait);
	}

	// Create quote for renewal quote without any input parameter @jjayapal
	public void quoteCreate(WebDriver driver, long wait) throws InterruptedException {

		Thread.sleep(10000);
		uIUtilities.waitForXpath(Create_Quote_Button_XPATH);
		driver.findElement(By.xpath(Create_Quote_Button_XPATH)).click();
		Thread.sleep(10000);

		int i = 0;
		String pageTitle;
		String quoteName;

		for (i = 1; i <= 50; i++) {
			pageTitle = driver.getTitle();

			if (pageTitle.startsWith("Quote")) {
				uIUtilities.waitForXpath(Quote_Name_Title_XPATH);
				quoteName = driver.findElement(By.xpath(Quote_Name_Title_XPATH)).getText();
				Assert.assertTrue(quoteName.startsWith("Q"));
				LOG.info(" : Quote Creation Done");
				LOG.info(" : Quote Name : " + quoteName);
				i = 50;
			} else {
				LOG.info(" : Quote Creation is in Progress");
				Thread.sleep(5000);
			}
		}
		Thread.sleep(10000);
	}

	//Move into quote config and product selection page
	public void quoteConfig(WebDriver driver, long wait) throws InterruptedException {
		uIUtilities.waitForXpath(Quote_Config_XPATH);
		driver.findElement(By.xpath(Quote_Config_XPATH)).click();
		Thread.sleep(10000);
		uIUtilities.waitForXpath(Quote_Step_Menu_XPATH);
		driver.findElement(By.xpath(Quote_Step_Menu_XPATH)).click();
		Thread.sleep(2000);
		uIUtilities.waitForXpath("//*[@id='SfdcSelectProducts']/input");
		driver.findElement(By.xpath("//*[@id='SfdcSelectProducts']/input")).click();
	}

	//Quote Approvals Process - @jjayapal
	public void quoteApprovals(WebDriver driver, long wait) throws InterruptedException {
		switchToFrame();
		driver.findElement(By.xpath(Quote_Approvals_Button_XPATH)).click();

		wait1.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().startsWith("https://");
			}
		});
		uIUtilities.waitForXpath(Quote_Approvals_Submit_XPATH);
		driver.findElement(By.xpath(Quote_Approvals_Submit_XPATH)).click();

		Thread.sleep(60000);
		wait1.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().startsWith("https://");
			}
		});

		uIUtilities.waitForXpath(Quote_Approvals_Submit_XPATH);
		driver.findElement(By.xpath(Quote_Approvals_Submit_XPATH)).click();

		Thread.sleep(60000);
		wait1.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				//return d.getTitle().startsWith("https://");
				return d.getPageSource().contains("Your request(s) have been submitted for approval. You will be notified when approvals are complete.");
			}
		});

		Thread.sleep(5000);
		uIUtilities.waitForXpath(Quote_Approvals_Return_XPATH);
		driver.findElement(By.xpath(Quote_Approvals_Return_XPATH)).click();

		Thread.sleep(30000);
		wait1.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().startsWith("Quote (CPQ)");
			}
		});
	}

	//Switch frame -
	public void switchToFrame() {
		driver.manage().timeouts().implicitlyWait(240, TimeUnit.SECONDS);

		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				Boolean retrunValue = null;
				try {
					uIUtilities.waitForXpath("//iframe[@title = 'SfdcQuoteButtonsVisibilityPage']");
					driver.switchTo().frame(driver.findElement(By.xpath("//iframe[@title = 'SfdcQuoteButtonsVisibilityPage']")));
					LOG.info("...FOUND.....");
					retrunValue = Boolean.TRUE;
				} catch (Exception ex) {
					LOG.info("...NOT FOUND.....");
					retrunValue = Boolean.FALSE;
				}
				return retrunValue;
			}
		});
	}

	//Quote publish process normal flow - @jjayapal
	public void quotePublish(WebDriver driver, long wait) throws InterruptedException {

		// update the xpath value for -> "Quote_Publish_Button_XPATH"
		switchToFrame();
		driver.findElement(By.xpath(Quote_Publish_Button_XPATH)).click();
		Thread.sleep(15000);
		uIUtilities.waitForXpath(Quote_Publish_Print_XPATH);
		driver.findElement(By.xpath(Quote_Publish_Print_XPATH)).click();
		Thread.sleep(5000);
		uIUtilities.waitForXpath(Quote_Publish_Next_XPATH);
		driver.findElement(By.xpath(Quote_Publish_Next_XPATH)).click();
		Thread.sleep(10000);
		uIUtilities.waitForXpath(Quote_Publish_Close_XPATH);
		driver.findElement(By.xpath(Quote_Publish_Close_XPATH)).click();
	}

	//Quote publish process for services product[attachment process] - @jjayapal
	public void quotePublish(WebDriver driver, String docPath, long wait) throws InterruptedException, AWTException {

		// update the xpath value for -> "Quote_Publish_Button_XPATH"
		switchToFrame();
		driver.findElement(By.xpath(Quote_Publish_Button_XPATH)).click();
		Thread.sleep(15000);


		wait1.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().startsWith("Salesforce ");
			}
		});
		driver.findElement(By.linkText("Add attachment")).click();
		Thread.sleep(10000);


		wait1.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().startsWith("Attach File ");
			}
		});


		//		String sysPath = System.getProperty("user.dir");
		//		System.out.println(" Check, this the system path"+sysPath);
		//		sysPath +=  "/src/test/resources/sample.docx";
		//		System.out.println("File found");
		//		System.out.println("file path"+sysPath);

		wait1.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//input[@id='file']"))));
		driver.findElement(By.xpath("//input[@id='file']")).sendKeys("C:\\Users\\Administrator\\Downloads\\sample.doc");

		wait1.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//input[@id='Attach']"))));
		driver.findElement(By.xpath("//input[@id='Attach']")).click();

		Thread.sleep(45000);

		wait1.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//input[@title='Done']"))));
		driver.findElement(By.xpath("//input[@title='Done']")).click();

		wait1.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().startsWith("Salesforce ");
			}
		});

		driver.findElement(By.name("AttachedFile")).click();
		Thread.sleep(3000);
		driver.findElement(By.xpath("//input[@value='Send']")).click();
		Thread.sleep(30000);

		wait1.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().startsWith("Quote (CPQ)");
			}
		});
	}

	//Quote submit for convert process - @jjayapal
	public void quoteSubmitForConvert(WebDriver driver, long wait) throws InterruptedException {

		// update the xpath value for -> "Quote_Submit_for_Convert_Button_XPATH"
		switchToFrame();
		Thread.sleep(30000);
		driver.findElement(By.xpath(Quote_Submit_for_Convert_Button_XPATH)).click();
		wait1.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().startsWith("Quote (CPQ)");
			}
		});
	}

	//Quote Convert process - @jjayapal
	public void quoteConvert(WebDriver driver, long wait) throws InterruptedException {

		// update the xpath value for -> "Quote_Convert_Button_XPATH"
		Thread.sleep(10000);
		switchToFrame();
		Thread.sleep(10000);
		driver.findElement(By.xpath(Quote_Convert_Button_XPATH)).click();
		Thread.sleep(40000);
		wait1.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().startsWith("Quote (CPQ)");
			}
		});
	}



}
