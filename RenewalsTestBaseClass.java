package com.salesforce.automation;

import com.salesforce.automation.pageconstants.PageConstants;
import com.salesforce.automation.pageconstants.SignupPageConstants;
import com.salesforce.automation.pageobjects.SignupPage;
import com.salesforce.automation.pageobjects.SysAdminPage;
import com.salesforce.automation.pageobjects.UserProfile;
import com.sforce.cd.Refactor.utils.WebDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class RenewalsTestBaseClass extends SeleniumTestBaseClass {

	//clicking on Black tab access and Signup link 	
	private static final Logger LOG = LoggerFactory.getLogger(RenewalsTestBaseClass.class);

	static UserProfile userProfile;

	public static void sysAdminPage(WebDriver driver) throws InterruptedException {

		SysAdminPage sp=PageFactory.initElements(driver, SysAdminPage.class);
		Thread.sleep(3000);

		sp.getHomeSysAdminBlacktab().click();

		Thread.sleep(3000);
		sp.getSignUpLink().click();
		Thread.sleep(3000);

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	//Creation of org and org id:enter information in Bulk signup page and click on Save
	public static void bulkSignup(WebDriver driver) throws InterruptedException
	{

		SignupPage sp=PageFactory.initElements(driver, SignupPage.class);

		//To generate a random test username and enter in userlist
		GregorianCalendar gcalendar = new GregorianCalendar();
		int hour=gcalendar.get(Calendar.HOUR);
		int min=gcalendar.get(Calendar.MINUTE);
		int sec=gcalendar.get(Calendar.SECOND);
		int rndNum=Integer.valueOf(String.valueOf(hour) + String.valueOf(min) + String.valueOf(sec));
		String text1="test"+Integer.valueOf(String.valueOf(rndNum));
		String text2=text1+"@salesforce.com";

		sp.getuserlist().sendKeys(text2);

		sp.getEmail().sendKeys(text2);
		sp.getcompanyName().sendKeys("company_C1");
		sp.getselectTemplate().sendKeys("Generic Template");
		sp.getpassword().sendKeys("abcd1234");

		sp.getPasswordReset().click();
		Thread.sleep(15000);
		WebDriverWait wait = new WebDriverWait(driver,1200);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(SignupPageConstants.RC_SAVE)));
		sp.getSavebtn().click();
		Thread.sleep(15000);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	public static void changeOpptyOwnerToUser1(WebDriver driver,String opptyId, String OpptyName) throws InterruptedException
	{
		try {

			String oppty_url = driver.getCurrentUrl();
			LOG.info("Oppty URL: " + oppty_url);
			WebDriverWait wait = new WebDriverWait(driver, 1500);
	
			//Login to Renewals Community Portal
			Integer Wait = 360;
			userProfile 	= new UserProfile(driver, Wait);
			String customLogin_UN = System.getProperty("Srev_USER");
			String customLogin_PWD = System.getProperty("Srev_PWD");
			userProfile.partnerCustomLogin(customLogin_UN,customLogin_PWD,driver,wait);

			//Move to Renewal Oppty page
			String community_renewal_url = System.getProperty("testPartnerEnvParam")+"/"+opptyId;

			LOG.info("Community Renewal URL: " + community_renewal_url);
			driver.get(community_renewal_url);
			Thread.sleep(59000);

			String oppty_header = driver.getTitle();
			String type = driver.findElement(By.xpath(PageConstants.OPPTY_TYPE)).getText();

			Assert.assertEquals(type,"Renewal");
			LOG.info(" : Verify Opportunity Type as Renewal - Pass");
			LOG.info("Optty page header : " + oppty_header);

			Assert.assertTrue(oppty_header.contains("Opportunity"));
			LOG.info(" : Verify Opportunity Header contains Opportunity - Pass");

			Assert.assertNotNull(WebDriverUtils.waitAndFindByXpath(PageConstants.OPPTY_NAME, wait).getText());
			LOG.info(" : Verify Opportunity Name is Present - Pass");

			Assert.assertNotNull(WebDriverUtils.waitAndFindByXpath(PageConstants.OPPTY_STAGE, wait).getText());
			LOG.info(" : Verify Opportunity Stage is Present - Pass");

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.info(" : Entered catch " + e.getStackTrace());
			Assert.assertNull(e, "Job Failed");
		}
	}

	public static void changeOpptyOwnerToOpUser(WebDriver driver,String opptyId) throws InterruptedException
	{
		try {

			String oppty_url = driver.getCurrentUrl();
			LOG.info("Oppty URL: " + oppty_url);

			WebDriverWait wait = new WebDriverWait(driver, 1500);
			
			//Login to Renewals Community Portal
			Integer Wait = 360;
			userProfile 	= new UserProfile(driver, Wait);
			String customLogin_UN = System.getProperty("Srev_Ops_USER");
			String customLogin_PWD = System.getProperty("Srev_Ops_PWD");
			userProfile.partnerCustomLogin(customLogin_UN,customLogin_PWD,driver,wait);
			
			
			String community_renewal_url = System.getProperty("testPartnerEnvParam")+"/"+opptyId;
			LOG.info("Community Renewal URL: " + community_renewal_url);

			driver.get(community_renewal_url);
			Thread.sleep(10000);

			String renewal_type = WebDriverUtils.waitAndFindByXpath(PageConstants.OPPTY_NAME, wait).getText();
			Assert.assertTrue(renewal_type.contains("Srev Renewal"));
			LOG.info(" : Verify Opportunity Type as Renewal - Pass");

			String oppty_header = driver.getTitle();
			LOG.info("Optty page header : " + oppty_header);

			Assert.assertTrue(oppty_header.contains("Opportunity"));
			LOG.info(" : Verify Page header contains 'Opportunity' - Pass");

			Assert.assertNotNull(WebDriverUtils.waitAndFindByXpath(PageConstants.OPPTY_NAME, wait).getText());
			LOG.info(" : Verify, Is Opportunity Name Present - Pass");

			Assert.assertNotNull(WebDriverUtils.waitAndFindByXpath(PageConstants.OPPTY_STAGE, wait).getText());
			LOG.info(" : Verify, Is Opportunity Stage Present - Pass");
		}
		catch(Exception ex){
			LOG.info(" : Entered catch " + ex.getMessage());
			LOG.info(" : Entered catch " + ex.getStackTrace());
			Assert.assertNull(ex, "Job Failed");
		}
	}

}