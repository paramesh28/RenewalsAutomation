package com.salesforce.automation.commonUI;

import com.salesforce.automation.commonAPI.PageObjectApi;
import com.salesforce.automation.commonElements.UserPageElement;
import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.UIUtilities;
import com.sforce.soap.partner.PartnerConnection;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class UserUI {

	private final WebDriver driver;
	private final WebDriverWait wait;
	private final UIUtilities uIUtilities;
	private final PageObjectApi pageObjectApi;
	private final ApiUtilities apiUtilities;
	private final PartnerConnection connection;
	private static final Logger LOG = LoggerFactory.getLogger(UserUI.class);
	private static String HostName = null;


	public UserUI(WebDriver driver, PartnerConnection connection, long wait) {
		this.driver 		= driver;
		this.connection		= null;
		this.wait 			= new WebDriverWait(driver, wait);
		this.uIUtilities 	= new UIUtilities(driver, wait);
		this.pageObjectApi 	= new PageObjectApi();
		this.apiUtilities	= new ApiUtilities();
	}

	UserPageElement userElement = new UserPageElement();

	/**
	 * Set User Platform - Using UI call - @jjayapal
	 * @param WebDriver
	 * @param connection
	 * @param currentUrl
	 **/
	public void setUserPlatform(WebDriver driver, PartnerConnection connection, String currentUrl){
		String testExpectedEnv = System.getProperty("Platform");
		HostName = apiUtilities.getHost(currentUrl); 
		LOG.info(" Expected Test Platform: "+testExpectedEnv);
		LOG.info(" ");

		if (currentUrl.contains("lightning") && testExpectedEnv.equals("Classic")){
			LOG.info(" Current Platform is    : Lightning");
			//driver.get("https://"+HostName+"/lightning/switcher?destination=classic");
			driver.get("https://"+HostName+"/ltng/switcher?destination=classic");
			LOG.info(" Switching Lightning to : Classic View");
			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return d.getTitle().toLowerCase().contains("salesforce");
				}
			});
		}


		if (!currentUrl.contains("lightning") && testExpectedEnv.equals("Lightning")){
			LOG.info(" Current Platform is    : Classic");
			//driver.get("https://"+HostName+"/lightning/switcher?destination=lex");
			driver.get("https://"+HostName+"/ltng/switcher?destination=lex"); 
			LOG.info(" Switching Classic to   : Lightning View");
			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return d.getTitle().toLowerCase().contains("salesforce");
				}
			});
		}        

		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().toLowerCase().contains("salesforce");
			}
		});        

		currentUrl = driver.getCurrentUrl();
		if (currentUrl.contains("lightning")){
			LOG.info("    Test Platform is    : Lightning View");
			LOG.info(" ");
			LOG.info(" ");
		}else{
			LOG.info("Test Platform is        : Classic View");
			LOG.info(" ");
			LOG.info(" ");
		}

		LOG.info("Page URL is: " + driver.getCurrentUrl());
	}


	public void UiLoginAs(WebDriver driver, PartnerConnection connection, String Platform, String UserId) throws InterruptedException{

		String currentUrl = driver.getCurrentUrl();
		HostName = apiUtilities.getHost(currentUrl);
		driver.navigate().refresh();
		if(Platform.equals("Lightning")){
			userElement.setLightningElements();

			Date d = new Date();

			//Changing platform to Classic
			//driver.get("https://"+HostName+"/lightning/switcher?destination=classic");
			driver.get("https://"+HostName+"/ltng/switcher?destination=classic");

			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return d.getTitle().toLowerCase().contains("salesforce");
				}
			});

			currentUrl = driver.getCurrentUrl();
			HostName = apiUtilities.getHost(currentUrl);


			//Moving to User LoginAs Classic Page			
			driver.get("https://"+HostName+"/"+UserId+"?noredirect=1&isUserEntityOverride=1");

			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return d.getTitle().toLowerCase().contains("salesforce");
				}
			});


			//Click LoginAs Button
			LOG.info(" : Click LoginAs Button");
			userElement.setClassisElements();
			uIUtilities.waitForXpath(userElement.LoginAs_Button_Xpath);
			driver.findElement(By.xpath(userElement.LoginAs_Button_Xpath)).click();

			Thread.sleep(10000);	
			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return d.getTitle().toLowerCase().contains("salesforce");
				}
			});
			Thread.sleep(10000);
			setUserPlatform(driver, connection, currentUrl);
			Thread.sleep(10000);
			//Verify Login

		}

		if(Platform.equals("Classic")){
			userElement.setLightningElements();

			Date d = new Date();
			driver.navigate().refresh();
			//Changing platform to Classic
			//driver.get("https://"+HostName+"/lightning/switcher?destination=classic");
			driver.get("https://"+HostName+"/ltng/switcher?destination=classic");

			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return d.getTitle().toLowerCase().contains("salesforce");
				}
			});

			currentUrl = driver.getCurrentUrl();
			HostName = apiUtilities.getHost(currentUrl); 

			//Moving to User LoginAs Classic Page			
			driver.get("https://"+HostName+"/"+UserId+"?noredirect=1&isUserEntityOverride=1");

			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return d.getTitle().toLowerCase().contains("salesforce");
				}
			});


			//Click LoginAs Button
			LOG.info(" : Click LoginAs Button");
			userElement.setClassisElements();
			uIUtilities.waitForXpath(userElement.LoginAs_Button_Xpath);
			driver.findElement(By.xpath(userElement.LoginAs_Button_Xpath)).click();

			Thread.sleep(10000);	
			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return d.getTitle().toLowerCase().contains("salesforce");
				}
			});
			Thread.sleep(10000);
			setUserPlatform(driver, connection, currentUrl);
			Thread.sleep(10000);
			//Verify Login
		}		
	}

	public void UiLogout(WebDriver driver, PartnerConnection connection, String Platform) throws InterruptedException{

		String currentUrl = driver.getCurrentUrl();
		HostName = apiUtilities.getHost(currentUrl); 

		//Click Logout link
		if(Platform.equals("Lightning")){
			userElement.setLightningElements();
			driver.findElement(By.xpath("//div[contains(@class, 'profileTrigger branding-user-profile bgimg slds-avatar slds-avatar_profile-image-small circular forceEntityIcon')]")).click();
			uIUtilities.waitForXpath(userElement.LogOut_Link_Xpath);
			if(driver.findElement(By.xpath(userElement.LogOut_Link_Xpath)).isDisplayed()){
				LOG.info("Click on Logout Link");
				driver.findElement(By.xpath(userElement.LogOut_Link_Xpath)).click();
			}else{
				LOG.info("Logout is not displayed");
				System.exit(0);
			}
			Thread.sleep(5000);
			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return d.getTitle().toLowerCase().contains("salesforce");
				}
			});	
			
			LOG.info(" : Current User Logged Out");
		}

		if(Platform.equals("Classic")){
			userElement.setLightningElements();
			driver.get("https://"+HostName+"/secur/logout.jsp");

			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return d.getTitle().toLowerCase().contains("salesforce");
				}
			});	
			
			LOG.info(" : Current User Logged Out");
			
			Thread.sleep(5000);
			currentUrl = driver.getCurrentUrl();
			setUserPlatform(driver, connection, currentUrl);
		}
		
		Thread.sleep(10000);
		currentUrl = driver.getCurrentUrl();
		setUserPlatform(driver, connection, currentUrl);
		Thread.sleep(10000);
	}
}
