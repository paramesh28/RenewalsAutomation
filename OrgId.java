package com.salesforce.automation.pageobjects;
/**
 * This class is used for Create, Update Org Id info
 * @author jjayapal
 */

import com.salesforce.automation.util.ApiUtilities;
import com.salesforce.automation.util.UIUtilities;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class OrgId {
	private final WebDriver driver;
	private final WebDriverWait wait;
	private final UIUtilities uIUtilities;
	private static final Logger LOG = LoggerFactory.getLogger(OrgId.class);

	public static String baseUrl = null;
	public static String currentUrl = null;
	public static String currentUser = null;
	Integer Wait = 360;

	//Create OrgId page elements 
	public static final String COMPANY_NAME_LOCATOR_ID = "p2";	
	public static final String USERNAME_LOCATOR_ID = "username" ;
	public static final String EMAIL_LOCATOR_ID = "email";
	public static final String PASSWORD_LOCATOR_ID = "p19";
	public static final String REQUIRE_PASSWORD_RESET_LOCATOR_CHECKBOX = "p22";
	public static final String TEMPLATE_LOCATOR_PICKLIST = "p6";

	public static final String SAVE_XPATH  = "//input[@name = 'save']";
	public static final String RESULTS_XPATH  = "//table[@class='detailList']//td[@class = 'labelCol']";
	public static final String ORGID_LINK_XPATH  = "//table[@class='detailList']//td[@class = 'data2Col']//a";

	public OrgId(WebDriver driver, long wait) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, wait);
		this.uIUtilities = new UIUtilities(driver, wait);
	} 


}
