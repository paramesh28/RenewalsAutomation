package com.salesforce.automation.commonElements;

public class UserPageElement {

	//Buttons
	public static String LoginAs_Button_Xpath 	= null;
	
	//Link
	public static String LogOut_Link_Xpath 	= null;

	public UserPageElement() {

		//Buttons
		this.LoginAs_Button_Xpath 				= null;
		
		//Link
		this.LogOut_Link_Xpath 				= null;
	}

	public void setLightningElements(){
		
		LoginAs_Button_Xpath 					= "//input[@title='Login']";
		
		//Link
		//LogOut_Link_Xpath                     = "//div[contains(@class,'system-message level-info')]//a[contains(text(),'Log out as')]";
		LogOut_Link_Xpath = "//div[contains(@class,'profile-card-toplinks')]//a[contains(text(),'Log Out')]";
	}
	
	public void setClassisElements() {
		
		LoginAs_Button_Xpath 					= "//td[@id='topButtonRow']/input[@title='Login']";
		
	}
}
