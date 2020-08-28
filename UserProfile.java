package com.salesforce.automation.pageobjects;
/**
 * This class is used for User Profile Actions
 * @author jjayapal
 */

import com.salesforce.automation.RenewalsTestBaseClass;
import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.UIUtilities;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserProfile extends RenewalsTestBaseClass {
	private final WebDriver driver;
	private final WebDriverWait wait;
	private final UIUtilities uIUtilities;
	private static final Logger LOG = LoggerFactory.getLogger(UserProfile.class);

	public static String baseUrl = null;
	public static String currentUrl = null;
	public static String currentUser = null;
	Integer Wait = 360;

	//User Profile page elements 
	public final static String Current_User_Name_XPATH = "//span[@id='userNavLabel']";
	public final static String Current_User_Logout_XPATH = "//a[@title='Logout']";
	public static final String USER_ACTION_MENU_ID = "moderatorMutton";
	public static final String USER_DETAIL_MENU_ID = "USER_DETAIL";
	public static final String LOGIN_BUTTON_NAME = "login";

	public static final String Username_XPATH = "//*[@id='username']";
	public static final String Password_XPATH = "//*[@id='password']";
	public static final String Login_XPATH = "//*[@id='Login']";
	public final static String Current_User_Name_Local_XPATH = "//a[@id='globalHeaderNameMink']/span";

	public UserProfile(WebDriver driver, long wait) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, wait);
		this.uIUtilities = new UIUtilities(driver, wait);
	} 


	//Current User logout @jjayapal  [used in sandbox Env]
	public void currentUserLogout(WebDriver driver) throws InterruptedException{

		uIUtilities.waitForXpath(Current_User_Name_XPATH);
		currentUser = driver.findElement(By.xpath(Current_User_Name_XPATH)).getText();

		uIUtilities.waitForXpath(Current_User_Name_XPATH);
		driver.findElement(By.xpath(Current_User_Name_XPATH)).click();
		Thread.sleep(2000);

		uIUtilities.waitForXpath(Current_User_Logout_XPATH);
		driver.findElement(By.xpath(Current_User_Logout_XPATH)).click();
		Thread.sleep(10000);
		LOG.info(" : Current User "+currentUser+" logged out");
	}


	//Current User logout @jjayapal  [used in Local Env]
	public void currentUserLogoutLocal(WebDriver driver) throws InterruptedException{

		uIUtilities.waitForXpath(Current_User_Name_Local_XPATH);
		currentUser = driver.findElement(By.xpath(Current_User_Name_Local_XPATH)).getText();

		uIUtilities.waitForXpath(Current_User_Name_Local_XPATH);
		driver.findElement(By.xpath(Current_User_Name_Local_XPATH)).click();
		Thread.sleep(2000);

		uIUtilities.waitForXpath(Current_User_Logout_XPATH);
		driver.findElement(By.xpath(Current_User_Logout_XPATH)).click();
		Thread.sleep(10000);
		LOG.info(" : Current User "+currentUser+" logged out");
	}

	//Get User Id @jjayapal
	public String getUserId(WebDriver driver, String user) throws InterruptedException, ConnectionException{
		String UserQuery = "SELECT Id FROM User where Name = '"+user+"'";
		SObject[] resultSet = ApiUtilities.executeQuery(connection, UserQuery);
		String userId = (String)resultSet[0].getField("Id");

		return userId;
	}

	//Login as any user @bindu
	public void LoginAs(WebDriver driver, String username, String password) throws InterruptedException{

		uIUtilities.waitForXpath(Username_XPATH);
		driver.findElement(By.xpath(Username_XPATH)).sendKeys(username);

		uIUtilities.waitForXpath(Password_XPATH);
		driver.findElement(By.xpath(Password_XPATH)).sendKeys(password);
		Thread.sleep(2000);

		uIUtilities.waitForXpath(Login_XPATH);
		driver.findElement(By.xpath(Login_XPATH)).click();
		Thread.sleep(10000);

		LOG.info("Logged in as "+username);		
	}

	//Login as custom user in Sandbox @jjayapal
	public static void customLogin(String username, String password, WebDriver driver, WebDriverWait wait) {

		String loginEndPoint 			= System.getProperty("testEnvParam");		
		Assert.assertNotNull(loginEndPoint, "Url is empty");
		Assert.assertNotNull(username, "username is empty");
		Assert.assertNotNull(password, "password is empty");		

		if (driver == null) {
			LOG.error("WebDriver did not initialize correctly");
		}		

		LOG.info("loginEndPoint is: "+loginEndPoint);
		LOG.info("username is     : "+username);
		LOG.info("password is     :  *******");

		driver.get(loginEndPoint);

		//get title of page
		LOG.info("Page title is: " + driver.getTitle());

		//enter username
		WebElement unameBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
		unameBox.sendKeys(username);

		//enter pasword
		WebElement passwdBox = driver.findElement(By.name("pw"));
		passwdBox.sendKeys(password);
		//passwdBox.submit();

		WebElement loginBtn = driver.findElement(By.name("Login"));
		loginBtn.click();

		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().toLowerCase().startsWith("salesforce");
			}
		});

		LOG.info("Post Login - page title is: " + driver.getTitle());
		LOG.info("Page URL is: " + driver.getCurrentUrl());
	}

	//Logout from current user
	public void userLogout(WebDriver driver) throws InterruptedException{
		String baseUrl = System.getProperty("testEnvParam");
		String lastChar = null;
		int baseUrlLength = baseUrl.length();

		for(int i=0; i < baseUrlLength; i++){
			lastChar = baseUrl.substring(baseUrl.length() - 1); 
			if(lastChar.endsWith("/")){
				baseUrl = baseUrl.substring(0,baseUrl.length()-1);
			}else{
				i = baseUrlLength+1;
			}
		}
		driver.get(baseUrl+"/secur/logout.jsp");
		LOG.info(" : User Logged Out");
	}

	//Login as partner user login in Sandbox @jjayapal
	public void partnerCustomLogin(String username, String password, WebDriver driver, WebDriverWait wait) {

		//Moving To Renewals Community Login Page
		driver.get(System.getProperty("testPartnerEnvParam")+"/secur/logout.jsp");
		uIUtilities.waitForPageTitleStartsWith("Login");
		
		
		String PartnerloginEndPoint = System.getProperty("testPartnerEnvParam");		
		Assert.assertNotNull(PartnerloginEndPoint, "Partnet Url is empty");
		Assert.assertNotNull(username, "Partnet username is empty");
		Assert.assertNotNull(password, "Partnet password is empty");		

		if (driver == null) {
			LOG.error("WebDriver did not initialize correctly");
		}		

		LOG.info("loginEndPoint is: "+PartnerloginEndPoint);
		LOG.info("username is     : "+username);
		LOG.info("password is     :  *******");

		driver.get(PartnerloginEndPoint);

		//get title of page
		LOG.info("Page title is: " + driver.getTitle());

		//enter username
		WebElement unameBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
		unameBox.sendKeys(username);

		//enter pasword
		WebElement passwdBox = driver.findElement(By.name("pw"));
		passwdBox.sendKeys(password);
		//passwdBox.submit();

		WebElement loginBtn = driver.findElement(By.name("Login"));
		loginBtn.click();

		uIUtilities.waitForPageTitleStartsWith("Renewals Community");

		LOG.info("Post Login - page title is: " + driver.getTitle());
		LOG.info("Page URL is: " + driver.getCurrentUrl());
	}
}
