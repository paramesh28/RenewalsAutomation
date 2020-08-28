package com.salesforce.automation;

/**
 * @author pmarina
 * Base class for all java test files
 */

import com.salesforce.automation.pageobjects.HomePage;
import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.Log;
import com.salesforce.automation.util.UIUtilities;
import com.sforce.cd.Refactor.utils.SFDCOrgUtils;
import com.sforce.soap.partner.PartnerConnection;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SeleniumTestBaseClass {
	private static final String username = System.getProperty("testUsernameParam");
	private static final String password = System.getProperty("testPasswordParam");
    protected static PartnerConnection connection = ApiUtilities.login(username,password);
	protected static List<String> testObjectsToDelete = new ArrayList<String>();
	protected static Map<String, Map<String, Object>> dataMap;
	
	protected WebDriver driver=null;
	
	public WebDriver setup(WebDriver driver) throws InterruptedException{
		//Get Handle to WebDriver using Fixture 		
		driver = SFDCOrgUtils.getNewInstanceofLoggedInDriverInstance();
		return driver;
	}

	public WebDriver Customsetup(WebDriver driver) throws InterruptedException{
		//Get Handle to WebDriver using Fixture 		
		driver = SFDCOrgUtils.getNewInstanceofLoggedInDriverInstanceCustom();
		return driver;
	}
	
	public static String getTimeStamp(){
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());		
	}
	
	public static void scrollToViewElement(WebDriver driver,WebElement element) throws Exception{
	    try{
	        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",element);
	    } catch (Exception e) {
			Log.writeLog("Error while scrolling to view element","info");
			e.printStackTrace();
			throw e;
	    }
	}
	
	/**
	 * Open any tab in sandbox
	 * This method clicks on + sign in tabs and will open a specified tab.
	 * @throws Exception
	 */
	public void openTab(String tabName) throws Exception{
		try{
			HomePage homePage = HomePage.init(driver);
			homePage.getHomeAllTabs().click();
			UIUtilities.waitForPageToLoad(1000);
			scrollToViewElement(driver, homePage.getHomeBillingEventGenTab());
			homePage.getHomeBillingEventGenTab().click();
			UIUtilities.waitForPageToLoad(2000);
		}catch (Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	public void tearDown(WebDriver driver){
		if (driver!=null)
			driver.quit();
	}
	
}
