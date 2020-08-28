
package com.salesforce.automation.SalesforceEngageAutomation;

import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.UIUtilities;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TestSalesforceEngage {
	private static final Logger LOG = LoggerFactory.getLogger(TestSalesforceEngage.class);

	public final static String Leads_tab_xpath = "//a[contains(text(),'Leads')]";
	public final static String Contacts_tab_xpath = "//a[contains(text(),'Contacts')]";
	public final static String EngageReport_tab_xpath = "//a[contains(text(),'Engage Reports')]";
	
	public final static String LeadLastName_XPATH = "//label[text()='Last Name']/parent::td/parent::tr/td[2]/div/input";
	public final static String LeadOfferType_XPATH = "//label[text()='Offer Type']/parent::td/parent::tr/td[4]/div/span/select";
	public final static String LeadOfferTypeMostRecenet_XPATH = "//label[text()='Offer Type Most Recent']/parent::td/parent::tr/td[4]/div/span/select";
	public final static String LeadSource_XPATH = "//label[text() = 'Lead Source']/parent::td/parent::tr/td[4]/div/span/select";
	public final static String LeadStatus_XPATH = "//label[text()='Lead Status']/parent::td/parent::tr/td[4]/div/span/select";
	public final static String AccountShippingCountry_XPATH = "html/body/form/div/div[2]/div[5]/table/tbody/tr[5]/td[2]/input";
	public final static String ExistingCustomer_Xpath ="//label[text()='Existing Customer']/parent::td/parent::tr/td[4]/div/span/select";
	public final static String NoOfEmployees_XPATH = "html/body/form/div/div[2]/div[7]/table/tbody/tr[1]/td[4]/div/input";
	public final static String LeadPhone_XPATH = "//label[text()='Phone']/parent::td/parent::tr/td[4]/input";
	public final static String LeadCountry_XPATh ="//label[text()='Country']/parent::td/parent::tr/td[2]/input";
	public final static String LeadSave_XPATH = "//td[@id='bottomButtonRow']/input[@value=' Save ']";


	public final static String Contact_LastName_XPATH = "//*[@id='name_lastcon2']";
	public final static String Contact_Phone_XPATH = "html/body/form/div/div[2]/div[7]/table/tbody/tr[1]/td[2]/input";
	public final static String ContactSave_XPATH = "//td[@id='bottomButtonRow']/input[@title='Save']";
	
	public final static String Org62dev1EnvURL = "https://org62--org62dev1.cs46.my.salesforce.com/";
	
	public final static String OkayNotification = "//button[contains(text(),'Okay')]"; 
	public final static String SendEngageButton_InididualRecord = "pi__send_single_pardot_email";
	public final static String SendEngageButton_MassEmail = "pi__send_pardot_emails";
	public static final String TradeShowSummaryTemplate = "//a[contains(text(), 'Trade Show Summary')]";
	public static final String SendButton = "//button[contains(text(), 'Send')]";

	private final WebDriverWait wait;
	private final WebDriverWait wait1;
	private final UIUtilities uIUtilities;
	
	
	public TestSalesforceEngage(WebDriver driver, long wait) {	
		LOG.info("----- Constructor ---- ");
		this.wait = new WebDriverWait(driver, wait);		
		this.wait1 = new WebDriverWait(driver,wait);
		this.uIUtilities = new UIUtilities(driver, wait);
		driver.switchTo().defaultContent();	
	}
	
	 ApiUtilities apiUtilities = new ApiUtilities();
	 Integer Wait = 360;
	 String env = System.getProperty("testEnvParam");
	 String username = System.getProperty("testUsernameParam");
	 String password = System.getProperty("testPasswordParam");
	 String envTag = System.getProperty("testEnvTag");
	// PartnerConnection connection = apiUtilities.custonLogin(username,password);
	
	
	Date date = new Date();
	DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	GregorianCalendar gcalendar = new GregorianCalendar();
	int hrs=gcalendar.get(Calendar.HOUR);
	int min=gcalendar.get(Calendar.MINUTE);
	int sec=gcalendar.get(Calendar.SECOND);
	
	String LeadName = "TestLead"+dateFormat.format(date)+Integer.toString(hrs)+Integer.toString(min)+Integer.toString(sec);
	String ContactName = "TestContact"+dateFormat.format(date)+Integer.toString(hrs)+Integer.toString(min)+Integer.toString(sec);


	
	public void SendEngageEmailIndividualLead(PartnerConnection connection, WebDriver driver) throws Exception{
		EngageDataCreation engageTest =  new EngageDataCreation(driver, Wait);
		engageTest.createLead(connection, LeadName);
		LOG.info("----- CreateNewRecordLead Start ---- ");	
		String query = "Select Id from Lead where Name = '"+LeadName+"'";
		SObject[] LeadId1 = ApiUtilities.executeQuery(connection, query);
		String LeadId=(String)LeadId1[0].getField("Id");
        LOG.info("Lead Id : "+LeadId);
        driver.get(env+LeadId);
        LOG.info("Lead detail page got Successfully loaded");
        driver.findElement(By.name(SendEngageButton_InididualRecord)).click();
        boolean notification = driver.findElement(By.xpath(OkayNotification)) != null;
        if(notification == true){
        uIUtilities.waitForXpath(OkayNotification);
        driver.findElement(By.xpath(OkayNotification)).click();
        }
        uIUtilities.waitForXpath(TradeShowSummaryTemplate);
    	driver.findElement(By.xpath(TradeShowSummaryTemplate)).click();
        if(notification == true){
    	uIUtilities.waitForXpath(OkayNotification);
    	driver.findElement(By.xpath(OkayNotification)).click();
        }
    	Thread.sleep(5000);
    	driver.findElement(By.xpath(SendButton)).click();
    	LOG.info("Engage email was successfully sent");
    	Thread.sleep(5000);
    	boolean title = driver.getPageSource().contains(LeadName);

        if(title)

            System.out.println("Page is redirected to lead detail page after sending engage email");

        else if(!title)

            System.out.println("Page is not redirected to contact detail page after sending engage email");
    	Thread.sleep(50000);
    	}

        

	public void SendEngageEmailIndividualContact(PartnerConnection connection, WebDriver driver) throws Exception{
		EngageDataCreation engageTest = new EngageDataCreation(driver, Wait);
		engageTest.createContact(connection, ContactName);
		LOG.info("----- CreateNewRecordContact Start ---- ");	
		String query = "Select Id from Contact where Name = '"+ContactName+"'";
		SObject[] LeadId1 = ApiUtilities.executeQuery(connection, query);
		String ContactId=(String)LeadId1[0].getField("Id");
        LOG.info("Contact Id : "+ContactId);
        driver.get(env+ContactId);
        LOG.info("Contact detail page got Successfully loaded");
        driver.findElement(By.name(SendEngageButton_InididualRecord)).click();
        uIUtilities.waitForXpath(TradeShowSummaryTemplate);
    	driver.findElement(By.xpath(TradeShowSummaryTemplate)).click();
    	Thread.sleep(5000);
    	driver.findElement(By.xpath(SendButton)).click();
    	LOG.info("Engage email was successfully sent");
    	Thread.sleep(5000);
    	boolean title = driver.getPageSource().contains(ContactName);

        if(title)

            System.out.println("Page is redirected to Contact detail page after sending engage email");

        else if(!title)

            System.out.println("Page is not redirected to Contact detail page after sending engage email");
    	Thread.sleep(50000);
	}
	
	
	public void SendEngageMassEmailLead(PartnerConnection connection, WebDriver driver) throws Exception{
		
		driver.get(env);
		uIUtilities.waitForXpath(Leads_tab_xpath);
		driver.findElement(By.xpath(Leads_tab_xpath)).click();
		Thread.sleep(5000);
		Select listview = new Select(driver.findElement(By.name("fcf")));
		listview.selectByVisibleText("All Leads");
		driver.findElement(By.name("go")).click();
		Thread.sleep(15000);
		driver.findElement(By.id("allBox")).click();
        driver.findElement(By.name(SendEngageButton_MassEmail)).click();
        uIUtilities.waitForXpath(TradeShowSummaryTemplate);
    	driver.findElement(By.xpath(TradeShowSummaryTemplate)).click();
    	Thread.sleep(5000);
    	driver.findElement(By.xpath(SendButton)).click();
    	LOG.info("Engage email was successfully sent");
    	
    	boolean title = driver.getPageSource().contains("Action");

        if(title)

            System.out.println("Page is redirected to lead list view page after sending engage email");

        else if(!title)

            System.out.println("Page is not redirected to lead list view page after sending engage email");
    	Thread.sleep(50000);
	}
	
	public void SendEngageMassEmailContact(PartnerConnection connection, WebDriver driver) throws Exception{
		
		driver.get(env);
		uIUtilities.waitForXpath(Contacts_tab_xpath);
		driver.findElement(By.xpath(Contacts_tab_xpath)).click();
		Thread.sleep(5000);
		Select listview = new Select(driver.findElement(By.name("fcf")));
		listview.selectByVisibleText("Recently Viewed Contacts");
		driver.findElement(By.name("go")).click();
		Thread.sleep(5000);
		driver.findElement(By.id("allBox")).click();
        driver.findElement(By.name(SendEngageButton_MassEmail)).click();
        uIUtilities.waitForXpath(TradeShowSummaryTemplate);
    	driver.findElement(By.xpath(TradeShowSummaryTemplate)).click();
    	Thread.sleep(5000);
    	driver.findElement(By.xpath(SendButton)).click();
    	LOG.info("Engage email was successfully sent");
    	
    	Thread.sleep(5000);
    	boolean title = driver.getPageSource().contains("Action");
        if(title)

            System.out.println("Page is redirected to contact list view page after sending engage email");

        else if(!title)

            System.out.println("Page is not redirected to contact list view page after sending engage email");
    	Thread.sleep(50000);
	}
		
	public void SendEngageReport(PartnerConnection connection, WebDriver driver) throws Exception{
		
		driver.get(env);
		Thread.sleep(10000);	
		uIUtilities.waitForXpath(EngageReport_tab_xpath);
		driver.findElement(By.xpath(EngageReport_tab_xpath)).click();
		String PardotReportingiframe = driver.findElement(By.xpath("//iframe[@id='pardotReportingIframe']")).getAttribute("name");
		driver.switchTo().frame(PardotReportingiframe);
		LOG.info("Successfully switched to the enagage reporting iframe");
		driver.findElement(By.xpath("//button[contains(text(),'Okay')]")).click();

		Thread.sleep(2000);
		boolean title = driver.getPageSource().contains("Engage Emails");

        if(title)

            System.out.println("Engaeg email tab is successfully loaded ");

        else if(!title)

            System.out.println("Engaeg email tab load failed");
    	
	}

	public void SendEngageMassEmailLead_OptOutEmail(PartnerConnection connection, WebDriver driver) throws Exception{
	
		EngageDataCreation engageTest =  new EngageDataCreation(driver, Wait);
		engageTest.createLead_OptOutEmail(connection, LeadName);
		LOG.info("----- CreateNewRecordLead Start ---- ");	
		String query = "Select Id from Lead where Name = '"+LeadName+"'";
		SObject[] LeadId1 = ApiUtilities.executeQuery(connection, query);
		String LeadId=(String)LeadId1[0].getField("Id");
	    LOG.info("Lead Id : "+LeadId);
	    driver.get(env+LeadId);
	    LOG.info("Lead detail page got Successfully loaded");
	    driver.findElement(By.name(SendEngageButton_InididualRecord)).click();
        uIUtilities.waitForXpath(OkayNotification);
        driver.findElement(By.xpath(OkayNotification)).click();
		boolean title = driver.getPageSource().contains("Open");
	
	    if(title)
	
	        System.out.println("Pass");
	
	    else if(!title)
	
	        System.out.println("Fail");
		Thread.sleep(50000);
				
		}

	public void No_AccountRecordUpdatebyPardotConnectUser(PartnerConnection connection, WebDriver driver) throws Exception{

		String query = "Select id,name from Account where lastmodifiedbyId = '0059A000000M4NO'";
		SObject[] AccountId1 = ApiUtilities.executeQuery(connection, query);
		int AccountId= AccountId1.length;
	    LOG.info("# of records updated by Pardot User: "+AccountId);
	    if(AccountId == 0){
	    	LOG.info("Pass: No AccountRecords are updated by PardotConnectorUser");
	    }
	    else{
	    	LOG.info("Fail: AccountRecords are getting updated by PardotConnectorUser");
	    }
	}

	public void SendEngaeEmail_NoEmailId(PartnerConnection connection, WebDriver driver) throws Exception{
		
		EngageDataCreation engageTest =  new EngageDataCreation(driver, Wait);
		engageTest.createLead_NoEmailId(connection, LeadName);
		LOG.info("----- CreateNewRecordLead Start ---- ");	
		String query = "Select Id from Lead where Name = '"+LeadName+"'";
		SObject[] LeadId1 = ApiUtilities.executeQuery(connection, query);
		String LeadId=(String)LeadId1[0].getField("Id");
		LOG.info("Lead Id : "+LeadId);
		driver.get(env+LeadId);
		LOG.info("Lead detail page got Successfully loaded");
		driver.findElement(By.name(SendEngageButton_InididualRecord)).click();
		LOG.info("SendEngage button clicked");
        uIUtilities.waitForXpath(OkayNotification);
        driver.findElement(By.xpath(OkayNotification)).click();
		Thread.sleep(5000);
		boolean title = driver.getPageSource().contains("Open");
		
		if(title)
		
		    System.out.println("Pass");
		
		else if(!title)
		
		    System.out.println("Fail");
		
		}
	
	public void TestPardotEngageActivityCreation_Lead(PartnerConnection connection, WebDriver driver) throws Exception{
		
        String query="SELECT Id FROM Task where Owner.name = 'Archit Jain' AND CreatedDate = Today Limit 1";
		SObject[] TaskID1 = ApiUtilities.executeQuery(connection, query);
		String TaskID=(String)TaskID1[0].getField("Id");
		LOG.info("Task Id : "+TaskID);
		Thread.sleep(5000);
		driver.get(env+TaskID);
		driver.getPageSource().contains("Pardot Email");
		LOG.info("Pardot Email Activity is successfully created");
		
	}
	
	
	
	public void SendEngageEmailIndividualLead_LighteningView(PartnerConnection connection, WebDriver driver) throws Exception{
		String query = "Select Id from Lead where Name like 'TestLead%' and Email = 'archit.jain@salesforce.com'";
		SObject[] LeadId1 = ApiUtilities.executeQuery(connection, query);
		String LeadId=(String)LeadId1[0].getField("Id");
        LOG.info("Lead Id : "+LeadId);
        //driver.get("https://org62--org62dev1.lightning.force.com/one/one.app?source=aloha#/sObject/"+LeadId+"/view?t=1480365946417");
        driver.get("https://org62--"+envTag+".lightning.force.com/one/one.app?source=aloha#/sObject/"+LeadId+"/view?t=1480365946417");
        LOG.info("Lead detail page got Successfully loaded");
        Thread.sleep(10000);
        WebElement ActionbuttonDropDown = driver.findElement(By.xpath("//span[@title = 'Show more actions for this record']"));
        ActionbuttonDropDown.click();      
        WebElement EngageActionbutton = driver.findElement(By.xpath("//*[contains(text(),'Send Engage Email')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", EngageActionbutton);
        LOG.info("EngaegEmailComposePageSuccessfullyLoaded");
        Thread.sleep(5000);
        driver.switchTo().defaultContent();
        String test = driver.findElement(By.xpath("//iframe[contains(@id, 'vfFrameId')]")).getAttribute("name");
        LOG.info("iFrameName:"+test);
        driver.switchTo().frame(test);
        uIUtilities.waitForXpath(TradeShowSummaryTemplate);
    	driver.findElement(By.xpath(TradeShowSummaryTemplate)).click();
    	LOG.info("Clicked on marketing cloud email template");
    	uIUtilities.waitForXpath(SendButton);
    	driver.findElement(By.xpath(SendButton)).click();
    	LOG.info("Engage email was successfully sent");
    	Thread.sleep(5000);
    	driver.switchTo().defaultContent();
    	boolean title = driver.getPageSource().contains("Company");

        if(title)

            System.out.println("Page is redirected to lead detail page after sending engage email");

        else if(!title)

            System.out.println("Page is not redirected to lead detail page after sending engage email");
    	
        LOG.info("Switching back to the classic view after successful execution the test case in the lightening view");       
        driver.findElement(By.xpath("//img[contains(@class, 'profileTrigger')]")).click();
        driver.findElement(By.xpath("//a[contains(text(), 'Switch to Salesforce Classic')]")).click();
        Thread.sleep(5000);
	}

     
}

