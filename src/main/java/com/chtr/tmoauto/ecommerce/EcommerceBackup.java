package com.chtr.tmoauto.ecommerce;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.chtr.tmoauto.logging.Logging;
import com.chtr.tmoauto.webui.GUI;

public class EcommerceBackup {
	
	static GUI fwgui = new GUI();
	Logging log = new Logging();
	String TrackingId = null;
	String AccountNumber = null;
	String addressLine = null;
	String emailId = null;
	String customerName = null;
	String confirmationNumber = null;
	String primaryNumber = null;
	String RetailDebugURL = null;
	String promotionID = null;
	String BusRefID = null;
	
	/**
	 * This function performs Salesforce validation.
	 * 
	 * @param input_filename
	 * @param worksheet_name
	 * @return void
	 * @author Gaurav Kumar
	 * @throws Exception
	 * @since 1/24/2017
	 */
	public void Ecom_validate_salesforce(String input_filename,
			String worksheet_name, String test_data) throws Exception {
		
		if (test_data.contains("FILE_"))
		{
			String workspace_name = fwgui.fw_get_workspace_name();
			String variables_path = workspace_name + "\\variables\\";
			
			String[] expected_value_arr = test_data.split("_");
			String expected_value_file = expected_value_arr[1];			
			test_data = fwgui.fw_get_value_from_file(variables_path + expected_value_file);
			System.out.println(test_data);
			String pid_bid[] = null;
			
			if(!(test_data.isEmpty()) && test_data != "" && test_data.contains("<>")) {
				pid_bid = test_data.split("<>");
				
				for (String pid_bid_value : pid_bid) {
					
					if(!(pid_bid_value.isEmpty()) && pid_bid_value != "" && pid_bid_value.contains("Promotion")) {
						
						String test_data_array[] = pid_bid_value.split("=");
						promotionID = test_data_array[1].trim();
					}
					else if(!(pid_bid_value.isEmpty()) && pid_bid_value != "" && pid_bid_value.contains("BusRef")) {
						
						String test_data_array[] = pid_bid_value.split("=");
						BusRefID = test_data_array[1].trim();
					}
				}
			}
			
			else if (!(test_data.isEmpty()) && test_data != "" && test_data.contains("=")) {

				if (!(test_data.isEmpty()) && test_data != "" && test_data.contains("Promotion")) {

					String test_data_array[] = test_data.split("=");
					promotionID = test_data_array[1].trim();
				} else if (!(test_data.isEmpty()) && test_data != "" && test_data.contains("BusRef")) {

					String test_data_array[] = test_data.split("=");
					BusRefID = test_data_array[1].trim();
				}
			}
			
			System.out.println("promotionID : " + promotionID);
			System.out.println("BusRefID : " + BusRefID);
		}
		
		// Searching confirmation number
		fwgui.fw_event(input_filename, worksheet_name, "EnterDataTextbox",
				"VALIDATESFDC_searchOrderLocator", confirmationNumber, "NA",
				"2000");

		fwgui.fw_event(input_filename, worksheet_name, "ClickButton",
				"VALIDATESFDC_sfdcgoSearchButton", "", "NA", "5000");

		fwgui.fw_event(input_filename, worksheet_name, "ClickJAVASCRIPT",
				"VALIDATESFDC_CustomerOrderLink", "", "NA", "5000");

		if (GUI.driver.findElements(By.id("01N39000000LM6N")).size() > 0) 
		{
			System.out.println("Frame 1 : 01N39000000LM6N");
			log.fw_writeLogEntry("Frame 1 : 01N39000000LM6N", "NA");
			GUI.driver.switchTo().frame(
					GUI.driver.findElement(By.id("01N39000000LM6N")));
		}
		
		if (GUI.driver.findElements(By.id("01N1800000004OU")).size() > 0)
		{
			System.out.println("Frame 2 : 01N1800000004OU");
			log.fw_writeLogEntry("Frame 2 : 01N1800000004OU", "NA");
			GUI.driver.switchTo().frame(
					GUI.driver.findElement(By.id("01N1800000004OU")));
		}

		LinkedHashMap<String, String> valueObtainedFromSFDC = new LinkedHashMap<String, String>();

		valueObtainedFromSFDC = Ecom_captureFromOrderSummary(input_filename,
				"Customer link", "Customer Info",
				"//*[@id='CustInfo']/div/table/tbody/tr", valueObtainedFromSFDC);

		valueObtainedFromSFDC = Ecom_captureFromOrderSummary(input_filename,
				"order info link", "Order Info [" + confirmationNumber + "]",
				"//*[@id='OrderInf']/div/table/tbody/tr", valueObtainedFromSFDC);
		
		/**
		 * @Irfan
		 * @since 3/22/2017
		 */
		if(BusRefID != null) {
			valueObtainedFromSFDC = Ecom_captureFromOrderSummary(input_filename,
					"order info link", "Order Info [" + confirmationNumber + "]",
						"//*[@id='OrderInf']/div/div/div2/table[1]/tbody", valueObtainedFromSFDC);
		}
		valueObtainedFromSFDC = Ecom_captureFromOrderSummary(input_filename,
				"Affiliate link", "Affiliate",
				"//*[@id='Affiliate']/div/table/tbody/tr",
				valueObtainedFromSFDC);

		valueObtainedFromSFDC = Ecom_captureFromOrderSummary(input_filename,
				"IT info link", "IT Info",
				"//*[@id='ITInfo']/div/table/tbody/tr", valueObtainedFromSFDC);

		String validateMessageFromSFDC = Ecom_validate(valueObtainedFromSFDC);
		System.out.println(validateMessageFromSFDC);
		
	}

	/**
	 * This function captures data from submit order page.
	 * 
	 * @param link
	 * @param xpath
	 * @param valueObtainedFromSFDC
	 * @return LinkedHashMap<String, String>
	 * @author Gaurav Kumar
	 * @throws Exception
	 * @since 1/24/2017
	 */
	public LinkedHashMap<String, String> Ecom_captureFromOrderSummary(
			String input_filename, String worksheet_name, String link,
			String xpath, LinkedHashMap<String, String> valueObtainedFromSFDC)
			throws Exception {

		fwgui.fw_click_element_using_javascript(worksheet_name, "NA", "link",
				link, "NA", 5000);

		Thread.sleep(5000);

		List<WebElement> rows = GUI.driver.findElements(By.xpath(xpath));

		int count = 0;
		Iterator<WebElement> iterator1 = rows.iterator();
		Iterator<WebElement> iterator2 = null;
		Iterator<WebElement> iterator3 = null;

		while (iterator1.hasNext()) {
			String key = null, value = null;
			WebElement row = iterator1.next();
			List<WebElement> rowsOfrows = row.findElements(By.tagName("tr"));
			iterator2 = rowsOfrows.iterator();
			while (iterator2.hasNext()) {

				WebElement rowofrow = iterator2.next();
				List<WebElement> cols = rowofrow.findElements(By.tagName("td"));
				iterator3 = cols.iterator();
				if (cols.size() >= 2) {
					while (iterator3.hasNext()) {
						WebElement col = iterator3.next();
						count++;
						if (count % 2 != 0) {
							key = col.getText();

						} else {
							value = col.getText();

						}

					}
					valueObtainedFromSFDC.put(key, value);

				}
			}
		}
		return valueObtainedFromSFDC;

	}

	/**
	 * This function validates data obtained from SFDC.
	 * 
	 * @param valueObtainedFromSFDC
	 * @return String
	 * @author Gaurav Kumar
	 * @throws Exception
	 * @since 1/24/2017
	 */
	public String Ecom_validate(LinkedHashMap<String, String> valueObtainedFromSFDC)
			throws Exception {
		String message = "";

		// System.out.println("valueObtained from sfdc:"+valueObtainedFromSFDC);
		// System.out.println("validation object
		// instance:"+validationObjectInstance);
		if (!AccountNumber.equals(valueObtainedFromSFDC.get("Account #")))
			message = message + "Account number from ui " + AccountNumber
					+ " doesnt match with SFDC account number "
					+ valueObtainedFromSFDC.get("Account #");
		else
			log.fw_writeLogEntry(
					" SalesForceValidation - Account Number matched", "NA");
		
		if(promotionID!=null){
			if(!promotionID.equals(valueObtainedFromSFDC.get("Promotion ID"))) {
				
				message = message + "Promotion ID from Sheet " + promotionID
				+ "doesnt match with SFDC account number "
				+ valueObtainedFromSFDC.get("Promotion ID");
				log.fw_writeLogEntry(
						" SalesForceValidation - Promotion ID Not matched", "NA");
			}
			else
				log.fw_writeLogEntry(
						" SalesForceValidation - Promotion ID matched", "NA");
		}
		
		else
			log.fw_writeLogEntry(
					" SalesForceValidation - Promotion ID Not Provided", "NA");
		
		if(BusRefID!=null){
			if(!(valueObtainedFromSFDC.get("Bus Ref ID / Name").contains(BusRefID)))
			{
				
				message = message + "BusRef from Sheet " + BusRefID
				+ "doesnt match with SFDC account number "
				+ valueObtainedFromSFDC.get("Bus Ref ID / Name");
				log.fw_writeLogEntry(
						" SalesForceValidation - Bus Ref ID / Name Not matched", "NA");
			}
			else
				log.fw_writeLogEntry(
						" SalesForceValidation - Bus Ref ID / Name matched", "NA");
		}
		else
			log.fw_writeLogEntry(
					" SalesForceValidation - Bus Ref Not Provided", "NA");
		
	
		if (!confirmationNumber.equalsIgnoreCase(valueObtainedFromSFDC
				.get("Confirmation #")))
			message = message + "Confirmation from from ui "
					+ confirmationNumber
					+ " doesnt match with SFDC confirmation number "
					+ valueObtainedFromSFDC.get("Confirmation #");
		else
			log.fw_writeLogEntry(
					" SalesForceValidation - Confirmation Number matched", "NA");

		if (!valueObtainedFromSFDC.get("Customer Phone:").contains(
				primaryNumber.replace("-", "")))
			message = message + "Primary number from ui " + primaryNumber
					+ " doesnt match with SFDC primary number "
					+ valueObtainedFromSFDC.get("Customer Phone:");
		else
			log.fw_writeLogEntry(
					" SalesForceValidation - Primary Number matched", "NA");

		if (!emailId
				.toLowerCase()
				.trim()
				.equals(valueObtainedFromSFDC.get("Customer Email:")
						.toLowerCase().trim()))
			message = message + "Email address from from ui " + emailId
					+ " doesnt match with SFDC email address "
					+ valueObtainedFromSFDC.get("Customer Email:");
		else
			log.fw_writeLogEntry(
					" SalesForceValidation - Customer Email matched", "NA");

		String nameAndAddress[] = valueObtainedFromSFDC.get(
				"Customer Name:\n" + "Customer Address:").split("\\r?\\n");

		if (!nameAndAddress[1].contains(addressLine.toUpperCase()))
			message = message + "Address from from ui " + addressLine
					+ " doesnt match with SFDC address " + nameAndAddress[1];
		else
			log.fw_writeLogEntry(
					" SalesForceValidation - Customer Address matched", "NA");

		if (!nameAndAddress[0].toLowerCase().equals(customerName.toLowerCase()))
			message = message + "Name from from ui " + customerName
					+ " doesnt match with SFDC name " + nameAndAddress[0];
		else
			log.fw_writeLogEntry(
					" SalesForceValidation - Customer Name matched", "NA");

		if (!valueObtainedFromSFDC.get("Affiliate SAID").equalsIgnoreCase(
				"17587"))
			message = message + "Affiliate SAID in SFDC is not equal to 17587";
		else
			log.fw_writeLogEntry(
					" SalesForceValidation - Affiliate SAID matched", "NA");

		if (!valueObtainedFromSFDC.get("Affiliate Name - ID").equalsIgnoreCase(
				"Charter.com - 118700"))
			message = message
					+ "Affiliate Name - ID in SFDC is not equal to Charter.com - 118700";
		else
			log.fw_writeLogEntry(
					" SalesForceValidation - Affiliate Name matched", "NA");

		if (!valueObtainedFromSFDC.get("Automation Path Result")
				.equalsIgnoreCase("Automation Successful"))
			message = message
					+ "Automation Path Result is not equal to Automation Successful";
		else
			log.fw_writeLogEntry(
					" SalesForceValidation - Automation Path Result is Automation Successful",
					"NA");

		if (!valueObtainedFromSFDC.get("G2B Tracking ID").equalsIgnoreCase(
				TrackingId))
			message = message
					+ "G2B tracking ID is not equal to Tracking ID obtained from debug window that is"
					+ TrackingId;
		else
			log.fw_writeLogEntry(
					" SalesForceValidation - G2B Tracking ID matched", "NA");

		return message;

	}
	
	/**
	 * This method is used to get ENV url for Retail Portal cases.
	 * @throws IOException
	 * @throws InterruptedException
	 * @author Dhaval D Parkhi
	 * @since 02/24/2017
	 */
	public void Ecom_GetRetailEnvURL(String configuration_map_fullpath, String tab_name, 
			int alm_test_id) throws InterruptedException, IOException {
		
		try{
			
			fwgui.fw_event(configuration_map_fullpath, tab_name, "GetText",
					"RETAIL_GetDebugUrl", "", "", "1000");
			
			RetailDebugURL = GUI.return_get_text.trim();
			
			String [] value_array = RetailDebugURL.split("\\{");
			System.out.println(value_array[0]);
			System.out.println(value_array[1]);
			
			if(value_array[1].endsWith("}")) {
				String [] url_array = value_array[1].split("\\}");
				System.out.println(url_array[0]);
				RetailDebugURL = url_array[0];
			}
			/*else if (tc_objectname.equals("VALIDATESAVECART")) 
			{

				if(tc_testdata.contains("COMPLETE_SAVECART"))
				{
					String userCredential[] = tc_testdata.split(",");
					ecomfunc.Ecom_validate_saveCart("", worksheet_name,
							userCredential[1], userCredential[2],
							userCredential[3],"COMPLETE_SAVECART");
				}
				else 
				{
					String userCredential[] = tc_testdata.split(",");
					ecomfunc.Ecom_validate_saveCart("", worksheet_name,
							userCredential[0], userCredential[1],
							userCredential[2]);
				}
			}*/
			
			Thread.sleep(5000);
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("URL not found");
		}
		
		log.fw_writeLogEntry("URL : " + RetailDebugURL, "0");
	}
	
	/**
	 * This method is used to handle pop-up related to an existing Saved Cart at the address.
	 * @throws IOException
	 * @throws InterruptedException
	 * @author Shadab Hasan
	 * @since 02/21/2017
	 */
	public void Ecom_CheckContinueSavedCart(String test_data) throws InterruptedException, IOException {
		
		WebDriverWait wait = new WebDriverWait(GUI.driver, 10);
		String test_data_array[] = test_data.split("<>");
		
		try{
			wait.until(ExpectedConditions.visibilityOfElementLocated(fwgui.fw_get_element_object(test_data_array[0], test_data_array[1])));
			wait.until(ExpectedConditions.visibilityOfElementLocated(fwgui.fw_get_element_object(test_data_array[0], test_data_array[1]))).click();
			Thread.sleep(5000);
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
			log.fw_writeLogEntry("Address has no Saved Cart!","0");
		}
		
		log.fw_writeLogEntry("Continue with the new saved cart handled", "0");	
	}

	/**
	 * This method is used to handle cases where the address already has a Saved Cart.
	 * @throws IOException
	 * @throws InterruptedException
	 * @author Shadab Hasan
	 * @since 02/21/2017
	 */
	public void Ecom_CheckExistingSavedCart() throws InterruptedException, IOException {
		
		WebDriverWait wait = new WebDriverWait(GUI.driver, 10);
		
		try {
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText("Proceed With a New Order")));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText("Proceed With a New Order"))).click();
						
		} catch(Exception e) {
			System.out.println(e.getMessage());
			log.fw_writeLogEntry("Address has no Saved Cart!","0");
		}
		
		log.fw_writeLogEntry("Existing Saved Cart handled", "0");
	}
	
	/**
	 * This method is used to handle the switch to calendar view link for RESI DSR Cases.
	 * @throws IOException
	 * @throws InterruptedException
	 * @author Dhaval D Parkhi
	 * @since 02/10/2017
	 */
	public void Ecom_handleCalendarView() throws InterruptedException, IOException {
		
		WebDriverWait wait = new WebDriverWait(GUI.driver, 10);
		
		try {
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText("Switch to calendar view")));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText("Switch to calendar view"))).click();
			Thread.sleep(5000);
						
		} catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Already in Calendar View!");
		}
		
		log.fw_writeLogEntry("Switch to Calendar View handled", "0");
	}
	
	
	/**
	 * This function validates performs saveCart validation.
	 * 
	 * @return void
	 * @author Gaurav Kumar
	 * @param password
	 * @param userName
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws ParseException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws Exception
	 * @since 1/24/2017
	 */

	public void Ecom_validate_saveCart(String configuration_map_fullpath,
			String tab_name, String userName, String password, String emailID, String... arg)
			throws InterruptedException, IOException, ParseException, KeyManagementException, NoSuchAlgorithmException, ClassNotFoundException, SQLException 
	{
		// to store cart elements selected previously

		List<String> selectedElementFromPreviousCartString = new ArrayList<String>();

		List<WebElement> previousCartElement = GUI.driver.findElements(By.cssSelector("input"));

		for (WebElement ele : previousCartElement) 
		{
			if (ele.isSelected()) 
			{

				selectedElementFromPreviousCartString.add(ele.getAttribute("id"));
			}
		}
		System.out.println(selectedElementFromPreviousCartString);

		if (GUI.driver.findElements(By.id("overridecart_continue_new_cart")).size() > 0)
			GUI.driver.findElement(By.id("overridecart_continue_new_cart")).click();

		Thread.sleep(6000);
		
		fwgui.fw_event("", "","TerminateWindowProcesses", "NA", "iexplore.exe,IEDriverServer.exe,chromedriver.exe,chrome.exe", "NA","6000");
		fwgui.fw_event("", "","LaunchBrowser", "NA", "CHROME", "NA","6000");

		GUI.driver.manage().window().maximize();

		String link = null;

		GUI.driver.get("https://outlook.charter.com");
		GUI.driver.findElement(By.id("username")).clear();
		GUI.driver.findElement(By.id("username")).sendKeys(userName);
		GUI.driver.findElement(By.id("password")).clear();
		GUI.driver.findElement(By.id("password")).sendKeys(password);
		GUI.driver.findElement(By.xpath("//span[@class='signinTxt']")).click();

		Thread.sleep(3000);
		GUI.driver.findElement(By.xpath("//button[@autoid='_n_a']")).click();

		GUI.driver.findElement(By.xpath("//input[@autoid='_n_7']")).sendKeys(
				"Complete your Charter Communications Order");
		
		Thread.sleep(3000);
		GUI.driver.findElement(By.xpath("//button[@autoid='_n_8']")).click();
		
		Thread.sleep(3000);
		GUI.driver.findElement(By.xpath("//div[@autoid='_rp_E']")).click();

		Thread.sleep(5000);
		
		String parentHandle = GUI.driver.getWindowHandle();
		System.out.println("parentHandle : " + parentHandle);
		
		List<WebElement> list = GUI.driver.findElements(By.tagName("a"));

		for (WebElement get : list) 
		{

			if (get.getAttribute("href") != null)
			{
				if (get.getAttribute("href").contains("cart")) 
				{

					link = get.getAttribute("href");
					System.out.println(link);
					get.click();
					break;
				}
			}
		}

		//fwgui.driver.get(link);
		
		Set<String> window_array = GUI.driver.getWindowHandles();
		
		for (String window : window_array)
		{
			System.out.println("window : " + window);
			if(!window.equals(parentHandle))
				GUI.driver.switchTo().window(window);
			else
				System.out.println("Don't switch!");
		}

		Thread.sleep(5000);

		fwgui.fw_event("", "",
				"EnterDataTextbox", "SAVECART_emailLocator", emailID, "NA",
				"2000");
		fwgui.fw_event("", "", "ClickJAVASCRIPT",
				"SAVECART_retriveSaveData", "NA", "NA", "2000");

		Thread.sleep(30000);
		
		if(arg[0].equalsIgnoreCase("COMPLETE_SAVECART"))
		{
			fwgui.fw_event("", "", "ClickJAVASCRIPT",
					"CARTSUMMARY_Complete", "NA", "NA", "2000");
			log.fw_writeLogEntry("SaveCart Validation " + "Clicked complete button" + ")", "0");

			return;
		}		

		fwgui.fw_event("", "", "ClickJAVASCRIPT",
				"SAVECART_EditCart", "NA", "NA", "2000");

		String message = "..";
		List<String> selectedElementFromNewLinkCart = new ArrayList<String>();

		Thread.sleep(10000);

		List<WebElement> newCartElement = GUI.driver.findElements(By.cssSelector("input"));

		for (WebElement ele : newCartElement) 
		{
			if (ele.isSelected()) 
			{

				selectedElementFromNewLinkCart.add(ele.getAttribute("id"));
			}
		}
		System.out.println("selected element from new link cart"
				+ selectedElementFromNewLinkCart);

		if ((selectedElementFromPreviousCartString == null && selectedElementFromNewLinkCart != null)
				|| selectedElementFromPreviousCartString != null
				&& selectedElementFromNewLinkCart == null
				|| selectedElementFromPreviousCartString.size() != selectedElementFromNewLinkCart
						.size()) 
		{
			System.out.println("previous cart:"
					+ selectedElementFromPreviousCartString);
			System.out.println("new cart:" + selectedElementFromNewLinkCart);
			message = "pages differ in number of values selected";

		}

		else 
		{
			for (int i = 0; i < selectedElementFromPreviousCartString.size(); i++)
			{
				if (!(selectedElementFromPreviousCartString.get(i)
						.equals(selectedElementFromNewLinkCart.get(i)))) 
				{
					message += selectedElementFromPreviousCartString.get(i)
							+ " differs from "
							+ selectedElementFromNewLinkCart.get(i);
				}
			}
		}

		if (!message.equalsIgnoreCase(".."))
		{
			log.fw_writeLogEntry("SaveCart Validation failed due to:" + message, "1");
		}
		else
		{
			log.fw_writeLogEntry("SaveCart Validation passed:" + message, "0");
		}
	}
}
