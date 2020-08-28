package com.salesforce.automation.pageobjects;

/**
 * @author skonkimalla
 * PageObjects for Home page
 */

import com.salesforce.automation.pageconstants.SignupPageConstants;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SignupPage extends Page {

	public SignupPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	public String Orgid;
	public String successMsg;

	@FindBy(xpath = SignupPageConstants.RC_USERNAME_LIST)
	private WebElement usernameList;

	@FindBy(xpath = SignupPageConstants.RC_EMAIL)
	private WebElement email;

	@FindBy(xpath = SignupPageConstants.RC_COMPANY_NAME)
	private WebElement companyName;

	@FindBy(xpath = SignupPageConstants.RC_TEMPLATE)
	private WebElement selectTemplate;

	@FindBy(xpath = SignupPageConstants.RC_PASSWORD)
	private WebElement password;

	@FindBy(xpath = SignupPageConstants.RC_SAVE)
	private WebElement saveBtn;

	@FindBy(xpath=SignupPageConstants.SUCCESSFUL_SIGNUP_MESSAGE)
	private WebElement signupMsg;

	@FindBy(xpath=SignupPageConstants.RC_ORG_ID)
	private WebElement verifyOrgId;

	@FindBy(xpath=SignupPageConstants.RC_PASSWORD_RESET)
	private WebElement passwordReset;

	public WebElement getuserlist(){

		return usernameList;
	}

	public WebElement getEmail(){

		return email;
	}

	public WebElement getselectTemplate()
	{
		return selectTemplate;
	}

	public WebElement getpassword()
	{
		return password;
	}

	public WebElement getcompanyName()
	{
		return  companyName;
	}

	public WebElement getSavebtn(){

		return saveBtn;
	}

	public WebElement getSuccessMsg(){

		return signupMsg;
	}

	public WebElement getPasswordReset(){

		return passwordReset;
	}
	
	public WebElement getOrgId(){

		return verifyOrgId;
	}

}