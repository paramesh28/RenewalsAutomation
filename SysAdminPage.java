package com.salesforce.automation.pageobjects;

/**
 * @author pmarina
 */

import com.salesforce.automation.pageconstants.HomePageConstants;
import com.salesforce.automation.pageconstants.SignupPageConstants;
import com.salesforce.automation.util.UrlUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.net.MalformedURLException;

public class SysAdminPage extends Page {

	@FindBy(xpath = HomePageConstants.RC_HOME_SYSADMIN)
	private WebElement homeSysAdminBlacktab;

	//@FindBy(xpath = HomePageConstants.RC_SIGNUP)
	@FindBy(linkText = SignupPageConstants.RC_SIGNUP)
	private WebElement signUpLink;

	public SysAdminPage(WebDriver driver) {
		super(driver);
	}

	public static SysAdminPage openHomePage(WebDriver driver) throws MalformedURLException {
		driver.get(UrlUtils.getServerUrlIncludingProtocol(driver.getCurrentUrl()));
		SysAdminPage home = PageFactory.initElements(driver, SysAdminPage.class);
		home.setDriver(driver);
		return home;
	}

	public static SysAdminPage init(WebDriver driver) throws MalformedURLException {
		SysAdminPage home = PageFactory.initElements(driver, SysAdminPage.class);
		home.setDriver(driver);
		return home;
	}

	public static SysAdminPage openHomePage(WebDriver driver,String appUrl) throws MalformedURLException {
		driver.get(appUrl);
		SysAdminPage home = PageFactory.initElements(driver, SysAdminPage.class);
		home.setDriver(driver);
		return home;
	}

	public void openUserPage(WebDriver driver,String url) throws MalformedURLException {
		driver.get(UrlUtils.getServerUrlIncludingProtocol(driver.getCurrentUrl()) + url);
	}
	public WebElement getHomeSysAdminBlacktab(){

		return homeSysAdminBlacktab;
	}

	public WebElement getSignUpLink(){

		return signUpLink;
	}

}