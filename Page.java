package com.salesforce.automation.pageobjects;

/**
 * @author pmarina
 * Base page class for page files
 */

import com.salesforce.automation.pageconstants.PageConstants;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

public class Page {
	private WebDriver driver;
	
	@FindBy(xpath = PageConstants.PAGE_EDIT_SAVE)
	private WebElement pageEditSave;

	@FindBy(xpath = PageConstants.PAGE_BUTTON_EDIT)
	private WebElement pageButtonEdit;
	
	public Page(WebDriver driver){
		this.setDriver(driver);
	}
	
	protected void setDriver(WebDriver driver) {
		this.driver = driver;
	}
	protected WebDriver getDriver() {
		return driver;
	}
	
	public void selectByVisibleText(WebElement element,String text){
		Select select = new Select(element);
		select.selectByVisibleText(text);
	}

	public void selectByValue(WebElement element,String text){
		Select select = new Select(element);
		select.selectByValue(text);
	}

	public WebElement getPageEditSave() {
		return pageEditSave;
	}
	
	public WebElement getPageButtonEdit() {
		return pageButtonEdit;
	}
}
