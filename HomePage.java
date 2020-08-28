package com.salesforce.automation.pageobjects;

/**
 * @author pmarina
 * PageObjects for Home page
 */

import com.salesforce.automation.pageconstants.HomePageConstants;
import com.salesforce.automation.util.UrlUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.net.MalformedURLException;

public class HomePage extends Page {

	@FindBy(xpath = HomePageConstants.HOME_APP)
	private WebElement homeApp;

	@FindBy(xpath = HomePageConstants.HOME_UAC_APP)
	private WebElement homeUacApp;

	@FindBy(xpath = HomePageConstants.HOME_SALES_APP)
	private WebElement homeSalesApp;

	@FindBy(xpath = HomePageConstants.HOME_LOG_TIMECARD)
	private WebElement homeLogTimeCard;

	@FindBy(xpath = HomePageConstants.HOME_HOME_TAB)
	private WebElement homeHomeTab;

	@FindBy(xpath = HomePageConstants.HOME_ALL_TABS)
	private WebElement homeAllTabs;

	@FindBy(xpath = HomePageConstants.HOME_BILLING_EVENT_GEN_TAB)
	private WebElement homeBillingEventGenTab;

	@FindBy(xpath = HomePageConstants.HOME_CALL_CENTER_APP)
	private WebElement homeCallCenterTab;

	@FindBy(xpath = HomePageConstants.HOME_AGENT_SEARCH_TAB)
	private WebElement homeAgentSearchTab;

	@FindBy(xpath = HomePageConstants.HOME_LOGIN)
	private WebElement homeLogin;


	public HomePage(WebDriver driver) {
		super(driver);
	}

	public static HomePage openHomePage(WebDriver driver) throws MalformedURLException {
		driver.get(UrlUtils.getServerUrlIncludingProtocol(driver.getCurrentUrl()));
		HomePage home = PageFactory.initElements(driver, HomePage.class);
		home.setDriver(driver);
		return home;
	}

	public static HomePage init(WebDriver driver) throws MalformedURLException {
		HomePage home = PageFactory.initElements(driver, HomePage.class);
		home.setDriver(driver);
		return home;
	}

	public static HomePage openHomePage(WebDriver driver,String appUrl) throws MalformedURLException {
		driver.get(appUrl);
		HomePage home = PageFactory.initElements(driver, HomePage.class);
		home.setDriver(driver);
		return home;
	}

	public WebElement getHomeApp() {
		return homeApp;
	}

	public WebElement getHomeUacApp() {
		return homeUacApp;
	}

	public WebElement getHomeSalesApp() {
		return homeSalesApp;
	}

	public WebElement getHomeLogTimeCard() {
		return homeLogTimeCard;
	}

	public WebElement getHomeHomeTab() {
		return homeHomeTab;
	}

	public WebElement getHomeAllTabs() {
		return homeAllTabs;
	}

	public WebElement getHomeBillingEventGenTab() {
		return homeBillingEventGenTab;
	}

	public WebElement getHomeCallCenterTab() {
		return homeCallCenterTab;
	}

	public WebElement getHomeAgentSearchTab() {
		return homeAgentSearchTab;
	}

	public WebElement getHomeLogin() {
		return homeLogin;
	}
}