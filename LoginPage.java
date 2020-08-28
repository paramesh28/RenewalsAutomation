package com.sforce.cd.Refactor.page;

/**
 * Interface for the login page actions
 * @author kvyas
 *
 */
public interface LoginPage extends Page {
	

	/***
	 * logs in to the application with username and password
	 * @param username
	 * @param password
	 */
	public void loginToPage(String username, String password);


}
