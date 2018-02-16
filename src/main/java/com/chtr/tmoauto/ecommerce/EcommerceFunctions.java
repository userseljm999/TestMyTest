package com.chtr.tmoauto.ecommerce;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.webbitserver.helpers.Base64;

import com.chtr.tmoauto.logging.Logging;
import com.chtr.tmoauto.webui.GUI;

public class EcommerceFunctions {

	Logging log = new Logging();
	GUI fwgui = new GUI();

	String TrackingId = null;
	String AccountNumber = null;
	String addressLine = null;
	String emailId = null;
	String customerName = null;
	String confirmationNumber = null;
	String primaryNumber = null;
	String portedTN = null;
	String portedTN2 = null;
	String portedTNNoDash = null;
	String portedTN2NoDash = "";
	String hostedTN = null;
	String hostedTNNoDash = null;
	String portedTN1NoDash = "";
	List<String> portedTNList = null;
	
	/**
	 * This function validates CSG information via webservice for a given
	 * account.
	 * 
	 * @param validation_string
	 * @param expected_total_charges
	 * @return
	 * @author Mark Elking
	 * @throws Exception
	 * @since 12/2/2016
	 */
	
	@SuppressWarnings("static-access")
	public String Ecom_validate_getcsgservices(
			String configuration_map_fullpath, String tab_name,
			String validation_string, int alm_test_id, String test_data) throws Exception 
	{
		
		String outputString = "";
		
		try 
		{
			if (test_data.contains("FILE_"))
			{
				String[] expected_value_arr = test_data.split("_");
				String expected_value_file = expected_value_arr[1];			
				test_data = fwgui.fw_get_variable(expected_value_file);
				log.fw_writeLogEntry("CSG Validation Test Data : " + test_data, "NA");
			}

			/*
			 * Getting ORDER Account Number
			 */
			
			String account_num = null;
			if (!test_data.contains("NONAUTO"))
			{
				
				if(test_data.contains("GISYELLOW")||test_data.contains("GISORANGE"))
				{
					AccountNumber = fwgui.fw_get_variable("AccountId").trim();
					System.out.println(AccountNumber);
					account_num = AccountNumber;
				}
				else if(test_data.contains("BAECSGValidation") && !(test_data.contains("GISYELLOW"))&&!(test_data.contains("GISORANGE")))
				{
					fwgui.fw_event("", "", "GetText", "ORDERCONFIRMATION_GetAccountNumber_BAE", "FILE--GUIAccountNumber", "", "0");
				} 
				else 
				{
					fwgui.fw_event("", "", "GetText", "ORDERCONFIRMATION_GetAccountNumber", "FILE--GUIAccountNumber", "", "0");
				}
				
				account_num = GUI.return_get_text.trim();
				
				if(test_data.contains("BAECSGValidation") && !(test_data.contains("GISYELLOW"))&& !(test_data.contains("GISORANGE"))) 
				{
					AccountNumber = account_num.split(":")[1].trim();
					account_num=AccountNumber;
				}
				else 
				{
					AccountNumber = account_num;
				}
				fwgui.fw_event("", "", "SetVariable", "NA", "GUIAccountNumber," + AccountNumber, "NA", "0");
			}
			//Getting ORDER CONFIRMATION Number
			
			if(test_data.contains("BAECSGValidation"))
			{
				fwgui.fw_event("", "", "GetText", "ORDERCONFIRMATION_GetConfirmationNumber_BAE", "FILE--GUIConfirmationNumber", "", "0");
			} 
			
			else if(test_data.contains("NONAUTO"))
			{
				fwgui.fw_event("", "", "GetText", "GetConfirmationNumber", "FILE--GUIConfirmationNumber", "", "0");
			}
			
			else
			{
				fwgui.fw_event("", "", "GetText", "ORDERCONFIRMATION_GetConfirmationNumber", "FILE--GUIConfirmationNumber", "", "0");
			}

			if(test_data.contains("BAECSGValidation")) 
			{
				confirmationNumber = GUI.return_get_text.split(":")[1].trim();
			}
			else
			{
				confirmationNumber = GUI.return_get_text.trim();
			}
			
			fwgui.fw_event("", "", "SetVariable", "NA", "GUIConfirmationNumber," + confirmationNumber, "NA", "0");
			
			
			// Getting ORDER Contact Info
			
			if(test_data.contains("BAECSGValidation")) 
			{
				fwgui.fw_event("", "", "GetText", "ORDERCONFIRMATION_GetContactInfo_BAE", "FILE--GUIContactInfo", "", "0");
			}
			
			else if(test_data.contains("NONAUTO"))
			{
				fwgui.fw_event("", "", "GetText", "ORDERCONFIRMATION_GetContactInfoNew", "FILE--GUIContactInfo", "", "0");
			}
			else
			{
				fwgui.fw_event("", "", "GetText", "ORDERCONFIRMATION_GetContactInfo", "FILE--GUIContactInfo", "", "0");
			}
					
			if (test_data.contains("BAECSGValidation") && test_data.contains("PORTED")) 
			{
				customerName = GUI.return_get_text.split("\\r?\\n")[1].trim();
				addressLine = GUI.return_get_text.split("\\r?\\n")[2].trim();
				primaryNumber = GUI.return_get_text.split("\\r?\\n")[4].split(":")[1].trim();
				emailId = GUI.return_get_text.split("\\r?\\n")[5].trim();
				
				//code for reading ported TN
				portedTNList = new ArrayList<>();
				String [] ported_array = test_data.split("<>");
				int ported_TN_count = 0;
				for (String string : ported_array)
				{
					if(string.contains("PORTED"))
					{
						String [] new_array = string.split("_");
						ported_TN_count = Integer.parseInt(new_array[1]);
						break;
					}
				}
				
				for (int i = 1; i <= ported_TN_count; i++) 
				{
					String xpathValue = "//h3[text()='Ported Phone Number(s):']/../ul[" + i + "]/li";
					fwgui.fw_get_text("Ported TN", "", "xpath", xpathValue, "", 2000);
					portedTNList.add(GUI.return_get_text.trim());
				}
			}
			else if (test_data.contains("BAECSGValidation") && !(test_data.contains("PORTED")))
			{
				customerName = GUI.return_get_text.split("\\r?\\n")[1].trim();
				addressLine = GUI.return_get_text.split("\\r?\\n")[2].trim();
				primaryNumber = GUI.return_get_text.split("\\r?\\n")[4].split(":")[1].trim();
				emailId = GUI.return_get_text.split("\\r?\\n")[5].trim();
			}
			
			else if(test_data.contains("PORTED"))
			{
				customerName = GUI.return_get_text.split("\\r?\\n")[0].trim();
				addressLine = GUI.return_get_text.split("\\r?\\n")[1].trim();
				primaryNumber = GUI.return_get_text.split("\\r?\\n")[3].trim();
				portedTN = GUI.return_get_text.split("\\r?\\n")[5].trim();
				emailId = GUI.return_get_text.split("\\r?\\n")[6].trim();			
				portedTN = portedTN.substring(1, 4) + portedTN.substring(5);
				portedTNNoDash = portedTN.substring(0,3) + portedTN.substring(4,7) + portedTN.substring(8);
				
				if(test_data.contains("PORTED_2"))
				{
					
					portedTN2 = GUI.return_get_text.split("\\r?\\n")[6].trim();
					portedTN2 = portedTN2.substring(1, 4) + portedTN2.substring(5);
					portedTN2NoDash = portedTN2.substring(0,3) + portedTN2.substring(4,7) + portedTN2.substring(8);
					emailId = GUI.return_get_text.split("\\r?\\n")[7].trim();
					
				}
			}
			else
			{
				customerName = GUI.return_get_text.split("\\r?\\n")[0].trim();
				addressLine = GUI.return_get_text.split("\\r?\\n")[1].trim();
				primaryNumber = GUI.return_get_text.split("\\r?\\n")[3].trim();
				emailId = GUI.return_get_text.split("\\r?\\n")[4].trim();
			}
			
			
			if (test_data.contains("HOSTED") && !(test_data.contains("BAECSGValidation"))) 
			{
				fwgui.fw_event("", "", "GetText", "ORDERCONFIRMATION_GetNewPhoneNumber", "FILE--GUIHostedTN", "", "0");
				
				hostedTN = GUI.return_get_text.trim();
				hostedTNNoDash = hostedTN.substring(0, 3) + hostedTN.substring(4, 7) + hostedTN.substring(8);
				
			}
			
			/*
			 * Getting Total Charges
			 */
			fwgui.fw_event("", "", "GetText", "ORDERCONFIRMATION_GETTOTALCHARGES", "FILE--GUITotalCharges", "", "0");

			String temp_expected_total_charges = GUI.return_get_text.trim().replace("$", "");
			DecimalFormat df = new DecimalFormat("#.00");
			
			if(test_data.contains("SPP_TV")) 
			{
				double temp_expected_total_double = Double.parseDouble(temp_expected_total_charges);
				
				//if(test_data.contains("BHN") || test_data.contains("TWC") || !test_data.contains("BAE"))
				//{
					temp_expected_total_double = temp_expected_total_double + 8.85;
				//}
				//else
				//{
				//	temp_expected_total_double = temp_expected_total_double + 7.50;
				//}
				temp_expected_total_charges = df.format(temp_expected_total_double);
			}
			
			if(test_data.contains("ADD")) 
			{
				Double add_value = 0.0;
				String [] test_data_array = test_data.split("<>");
				for (String test_data_value : test_data_array) 
				{	
					if(test_data_value.contains("ADD")) 
					{
						String [] add_value_array = test_data_value.split("_");
						add_value = Double.parseDouble(add_value_array[1]);
					}
				}
				
				double temp_expected_total_double = Double.parseDouble(temp_expected_total_charges);
				System.out.println("temp_expected_total double : " + temp_expected_total_double);
				temp_expected_total_double = temp_expected_total_double + add_value;
				System.out.println("Updated temp_expected_total_double : " + temp_expected_total_double);
				temp_expected_total_charges = df.format(temp_expected_total_double);
				System.out.println("Updated temp_expected_total_charges : " + temp_expected_total_charges);
			}
			
			if(test_data.contains("SUBTRACT")) 
			{
				Double subtract_value = 0.0;
				String [] test_data_array = test_data.split("<>");
				for (String test_data_value : test_data_array) 
				{	
					if(test_data_value.contains("SUBTRACT")) 
					{
						String [] subtract_value_array = test_data_value.split("_");
						subtract_value = Double.parseDouble(subtract_value_array[1]);
					}
				}
				
				double temp_expected_total_double = Double.parseDouble(temp_expected_total_charges);
				System.out.println("temp_expected_total double : " + temp_expected_total_double);
				temp_expected_total_double = temp_expected_total_double - subtract_value;
				System.out.println("Updated temp_expected_total_double : " + temp_expected_total_double);
				temp_expected_total_charges = df.format(temp_expected_total_double);
				System.out.println("Updated temp_expected_total_charges : " + temp_expected_total_charges);
			}
			
			if(test_data.contains("TollFree_")) 
			{
				Double toll_free_count = 0.0;
				String [] test_data_array = test_data.split("<>");
				for (String test_data_value : test_data_array) 
				{
					
					if(test_data_value.contains("TollFree")) 
					{
						String [] value_array = test_data_value.split("_");
						toll_free_count = Double.parseDouble(value_array[1]);
					}
				}
				double temp_expected_total_double = Double.parseDouble(temp_expected_total_charges);
				System.out.println(toll_free_count);
				double value_to_be_subtracted = toll_free_count * 2;
				System.out.println(value_to_be_subtracted);
				temp_expected_total_double = temp_expected_total_double - value_to_be_subtracted;
				temp_expected_total_charges = df.format(temp_expected_total_double);
				System.out.println("Updated temp_expected_total_charges_2 : " + temp_expected_total_charges);
			}
			
			fwgui.fw_event("", "", "SetVariable", "NA", "GUITotalCharges," + temp_expected_total_charges, "NA", "0");
			
			if(test_data.contains("TWC") || test_data.contains("BHN"))
			{
				log.fw_writeLogEntry("  No CSG Validations required", "NA");
				log.fw_writeLogEntry("  Data Validations performed in SOM", "NA");
			}
			else
			{
				if(TrackingId == null || TrackingId.isEmpty())
				{
					TrackingId = Ecom_getTrackingID(test_data);
				}
				
				/*String[] nodeValueFromGetCSGCustomerID = CancelCSGOrder.fw_get_custId_locId(account_num, alm_test_id);
				String customer_num = nodeValueFromGetCSGCustomerID[0];
				String location_id = nodeValueFromGetCSGCustomerID[1];*/
				
				String customer_num = "";
				String location_id = "";

				fwgui.fw_event("", "", "XMLExecute", "WEBSERVICE_GetAccountsByAccountId_GetAccounts", "XML_ACCOUNT_NUM,FILE_GUIAccountNumber", "NA", "1000");

				String text_to_look_for = "<ReturnCode>Success</ReturnCode>";
				String return_code_output = fwgui.fw_validate_text_in_xml_response("GetAccountsByAccountId", text_to_look_for, "NO", 0);

				if(return_code_output.contains("0--"))
				{
					log.fw_writeLogEntry("Account found in CSG","0");
					fwgui.fw_event("", "", "XMLGetValueByTagname", "WEBSERVICE_GetAccountsByAccountId_GetAccounts", "CustomerId", "NA", "1000");
					fwgui.fw_event("", "", "XMLGetValueByTagname", "WEBSERVICE_GetAccountsByAccountId_GetAccounts", "LocationId", "NA", "1000");
	
					customer_num = fwgui.fw_get_variable("CustomerId");
					location_id = fwgui.fw_get_variable("LocationId");
	
					log.fw_writeLogEntry("  Customer ID: " + customer_num, "NA");
					log.fw_writeLogEntry("  Location ID: " + location_id, "NA");
				}
	
				log.fw_writeLogEntry("", "NA");
	
				// Build & Execute Webservice Request to CSG to Get Service Info back to
				// validate Price
	
				String responseString = "";
				outputString = "";
				String wsURL = "https://ebs-uat.corp.chartercom.com/csg_cter/2.06/ServicesService.asmx";
				log.fw_writeLogEntry("     XML Endpoint: " + wsURL, "NA");
	
				URL url = new URL(wsURL);
				String creds = "chtr\\svc_tst_automation:H2i1fL9!";
				log.fw_writeLogEntry("     XML Credentials: " + creds, "NA");
	
				String encodeCreds = "Basic " + new String(new Base64().encode(creds.getBytes()));
				URLConnection connection = url.openConnection();
				HttpURLConnection httpConn = (HttpURLConnection) connection;
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				String xmlInput = null;
				
				if(test_data.contains("Pricing"))
				{
					xmlInput = "<soapenv:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
							+ "  <soapenv:Header>\n"
							+ "    <wsse:Security xmlns:S=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" S:mustUnderstand=\"1\">\n"
							+ "      <wsse:UsernameToken xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" wsu:id=\"UsernameToken-ORbTEPzNsEMDfzrI9sscVA22\">\n"
							+ "        <wsse:Username xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">svc_tst_automation</wsse:Username>\n"
							+ "        <wsse:Password xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">H2i1fL9!</wsse:Password>\n"
							+ "      </wsse:UsernameToken>\n"
							+ "    </wsse:Security>\n"
							+ "  </soapenv:Header>\n"
							+ "   <soapenv:Body>\n"
							+ "      <tns:GetCurrentServicesRequest xmlns:tns=\"http://charter.com/enterprise/billing/csg/services\">\n"
							+ "         <tns:CsgHeaderInfo>\n"
							+ "            <tns:Region>QAKA</tns:Region>\n"
							+ "            <tns:RoutingArea>83457800</tns:RoutingArea>\n"
							+ "            <tns:RealTime>true</tns:RealTime>\n"
							+ "         </tns:CsgHeaderInfo>\n"
							+ "         <tns:CustomerId>"
							+ customer_num
							+ "</tns:CustomerId>\n"
							+ "         <tns:SearchFilter>\n"
							+ "            <tns:AccountId>"
							+ account_num
							+ "</tns:AccountId>\n"
							+ "            <tns:LocationId>"
							+ location_id
							+ "</tns:LocationId>\n"
							+ "            <tns:CurrentItems>Pricing</tns:CurrentItems>\n"
							+ "            <tns:HistoryItems>False</tns:HistoryItems>\n"
							+ "            <tns:PendingItems>True</tns:PendingItems>\n"
							+ "         </tns:SearchFilter>\n"
							+ "      </tns:GetCurrentServicesRequest>\n"
							+ "   </soapenv:Body>\n" + "</soapenv:Envelope>";
				}
				else
				{
					xmlInput = "<soapenv:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
							+ "  <soapenv:Header>\n"
							+ "    <wsse:Security xmlns:S=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" S:mustUnderstand=\"1\">\n"
							+ "      <wsse:UsernameToken xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" wsu:id=\"UsernameToken-ORbTEPzNsEMDfzrI9sscVA22\">\n"
							+ "        <wsse:Username xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">svc_tst_automation</wsse:Username>\n"
							+ "        <wsse:Password xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">H2i1fL9!</wsse:Password>\n"
							+ "      </wsse:UsernameToken>\n"
							+ "    </wsse:Security>\n"
							+ "  </soapenv:Header>\n"
							+ "   <soapenv:Body>\n"
							+ "      <tns:GetCurrentServicesRequest xmlns:tns=\"http://charter.com/enterprise/billing/csg/services\">\n"
							+ "         <tns:CsgHeaderInfo>\n"
							+ "            <tns:Region>QAKA</tns:Region>\n"
							+ "            <tns:RoutingArea>83457800</tns:RoutingArea>\n"
							+ "            <tns:RealTime>true</tns:RealTime>\n"
							+ "         </tns:CsgHeaderInfo>\n"
							+ "         <tns:CustomerId>"
							+ customer_num
							+ "</tns:CustomerId>\n"
							+ "         <tns:SearchFilter>\n"
							+ "            <tns:AccountId>"
							+ account_num
							+ "</tns:AccountId>\n"
							+ "            <tns:LocationId>"
							+ location_id
							+ "</tns:LocationId>\n"
							+ "            <tns:CurrentItems>True</tns:CurrentItems>\n"
							+ "            <tns:HistoryItems>False</tns:HistoryItems>\n"
							+ "            <tns:PendingItems>True</tns:PendingItems>\n"
							+ "         </tns:SearchFilter>\n"
							+ "      </tns:GetCurrentServicesRequest>\n"
							+ "   </soapenv:Body>\n" + "</soapenv:Envelope>";
				}
	
				log.fw_writeLogEntry("     XML Request: " + xmlInput, "NA");
	
				byte[] buffer = new byte[xmlInput.length()];
				buffer = xmlInput.getBytes();
				bout.write(buffer);
				byte[] b = bout.toByteArray();
				httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
				httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
				httpConn.setRequestProperty("Authorization", encodeCreds);
				httpConn.setRequestMethod("POST");
				httpConn.setDoOutput(true);
				httpConn.setDoInput(true);
				OutputStream out = httpConn.getOutputStream();
				out.write(b);
				out.close();
	
				// Read the response.
				InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
				BufferedReader in = new BufferedReader(isr);
	
				// Write the SOAP message response to a String.
				while ((responseString = in.readLine()) != null) 
				{
					outputString = outputString + responseString;
				}
	
				log.fw_writeLogEntry("     XML Response: " + outputString, "NA");
				log.fw_writeLogEntry("", "NA");
	
				// Calculate Total Charges
	
				String orig_val = outputString;
				String temp_val = orig_val;
				String temp_val1 = outputString;
				int pos_open_carat = 0;
				int pos_closed_carat = 0;
				String out_charge = "";
				int cur_pos = 0;
				String new_temp_val = "";
				String cur_charge = "";
				double total_charges = 0;
				double temp_charge = 0;
	
				if(test_data.contains("Existing"))
				{
					while (temp_val.indexOf("</BillingIdentifier><ParentServiceIdentifier>") != -1) 
					{
						
						cur_pos = temp_val.indexOf("</BillingIdentifier><ParentServiceIdentifier>");
						new_temp_val = temp_val.substring(cur_pos + 2);
	
						cur_pos = new_temp_val.indexOf("<Rate>");
						new_temp_val = new_temp_val.substring(cur_pos + 2);
						pos_open_carat = new_temp_val.indexOf(">");
						pos_closed_carat = new_temp_val.indexOf("<");
						cur_charge = new_temp_val.substring(pos_open_carat + 1,pos_closed_carat);
						temp_charge = Double.parseDouble(cur_charge);
						total_charges = total_charges + temp_charge;
						out_charge = out_charge + "," + cur_charge;
						temp_val = new_temp_val.substring(pos_closed_carat + 1);
	
					}
				}
				else
				{
					while (temp_val.indexOf("</BillingIdentifier><ParentServiceIdentifier>") != -1) 
					{
						
						cur_pos = temp_val.indexOf("</BillingIdentifier><ParentServiceIdentifier>");
						new_temp_val = temp_val.substring(cur_pos + 2);
	
						cur_pos = new_temp_val.indexOf("<Charge>");
						new_temp_val = new_temp_val.substring(cur_pos + 2);
						pos_open_carat = new_temp_val.indexOf(">");
						pos_closed_carat = new_temp_val.indexOf("<");
						cur_charge = new_temp_val.substring(pos_open_carat + 1,pos_closed_carat);
						temp_charge = Double.parseDouble(cur_charge);
						total_charges = total_charges + temp_charge;
						out_charge = out_charge + "," + cur_charge;
						temp_val = new_temp_val.substring(pos_closed_carat + 1);
	
					}
				}
	
				out_charge = out_charge.substring(1);
				
				log.fw_writeLogEntry("     Charges (" + out_charge + ")", "NA");
	
				String temp_total_charges = df.format(total_charges);
				int pos_total_charges_decimal = temp_total_charges.indexOf(".");
				temp_total_charges = temp_total_charges.substring(0,pos_total_charges_decimal + 3);
				
				int pos_expected_total_charges_decimal = temp_expected_total_charges.indexOf(".");
				temp_expected_total_charges = temp_expected_total_charges.substring(0, pos_expected_total_charges_decimal + 3);
	
				int discount_rc_value = 0;
				if (test_data.contains("discount")) 
				{
					String discountStr = test_data.split("discount\\[")[1];
					String discountStr0 = discountStr.split("]")[0];
					String[] discountArr = discountStr0.split("<>");
	
					for (int i = 0; i < discountArr.length; i++) 
					{
						if (temp_val1.contains(discountArr[i])) 
						{
							discount_rc_value = 0;
							log.fw_writeLogEntry("Validate Discount Code " + discountArr[i], "0");
						} 
						else if (i == (discountArr.length - 1)) 
						{
							if (temp_val1.contains(discountArr[i].substring(0, discountArr[i].length() - 1))) 
							{
								log.fw_writeLogEntry("Validate Discount Code " + discountArr[i].substring(0, discountArr[i].length() - 1), "0");
								discount_rc_value = 0;
							}
							else 
							{
								log.fw_writeLogEntry("Validate Discount Code " + discountArr[i].substring(0, discountArr[i].length() - 1), "1");
								discount_rc_value = 1;
							}
						} 
						else 
						{
							log.fw_writeLogEntry("Validate Discount Code " + discountArr[i], "1");
							discount_rc_value = 1;
						}
	
					}
	
				}
				
				int serviceCode_rc_value=0;
				if (test_data.contains("serviceCode")) 
				{
					String serviceCodeStr = test_data.split("serviceCode\\[")[1];
					String serviceCodeStr0 = serviceCodeStr.split("]")[0];
					String[] serviceCodeArr = serviceCodeStr0.split("<>");
	
					for (int i = 0; i < serviceCodeArr.length; i++) 
					{
						if (temp_val1.contains(serviceCodeArr[i]))
						{
							serviceCode_rc_value = 0;
							log.fw_writeLogEntry("Validate service Code " + serviceCodeArr[i], "0");
						} 
						else if (i == (serviceCodeArr.length - 1)) 
						{
							if (temp_val1.contains(serviceCodeArr[i].substring(0, serviceCodeArr[i].length() - 1))) 
							{
								log.fw_writeLogEntry("Validate service Code " + serviceCodeArr[i].substring(0, serviceCodeArr[i].length() - 1), "0");
								serviceCode_rc_value = 0;
							} 
							else 
							{
								log.fw_writeLogEntry("Validate service Code " + serviceCodeArr[i].substring(0, serviceCodeArr[i].length() - 1), "1");
								serviceCode_rc_value = 1;
							}
						} 
						else 
						{
							log.fw_writeLogEntry("Validate service Code " + serviceCodeArr[i], "1");
							serviceCode_rc_value = 1;
						}
					}
				}
	
				if (temp_total_charges.equals(temp_expected_total_charges)) 
				{
					log.fw_writeLogEntry("Validate Total Charges (Expected Total Charges (GUI): " + temp_expected_total_charges + ", Actual Total Charges (CSG): " + temp_total_charges + ")", "0");
				} 
				else 
				{
					log.fw_writeLogEntry("Validate Total Charges (Expected Total Charges (GUI): " + temp_expected_total_charges + ", Actual Total Charges (CSG): " + temp_total_charges + ")", "1");
				}
	
				// Validate Each Service
	
				String validation_string_to_look_for = "";
				String[] validation_string_arr = validation_string.split(",");
				String rc_value = "";
				int overall_rc_value = 0;
	
				for (int x = 0; x < validation_string_arr.length; x++) 
				{
					validation_string_to_look_for = validation_string_arr[x];
					if (outputString.contains(validation_string_to_look_for))
					{
						rc_value = "0";
						log.fw_writeLogEntry("     Validate CSG Info XML Response (FOUND, Validation String: " + validation_string_to_look_for + ")", rc_value);
					} 
					else 
					{
						rc_value = "1";
						overall_rc_value++;
						log.fw_writeLogEntry("     Validate CSG Info XML Response (NOT FOUND, Validation String: " + validation_string_to_look_for + ")", rc_value);
					}
				}
	
				if (overall_rc_value == 0) 
				{
					log.fw_writeLogEntry("Validate CSG Info", "0");
				} 
				else 
				{
					log.fw_writeLogEntry("Validate CSG Info", "1");
				}
				if (discount_rc_value == 0 && test_data.contains("discount")) 
				{
					log.fw_writeLogEntry("Validate Discount Code", "0");
				} 
				else if (test_data.contains("discount")) 
				{
					log.fw_writeLogEntry("Validate Discount Code", "1");
				}
	
				if (serviceCode_rc_value == 0 && test_data.contains("serviceCode")) 
				{
					log.fw_writeLogEntry("Validate Service Code", "0");
				} 
				else if (test_data.contains("serviceCode")) 
				{
					log.fw_writeLogEntry("Validate Service Code", "1");
				}
			}
		}
		catch (Exception e) 
		{
			log.fw_writeLogEntry("CSG Validation FAILED", "1");
		}

		return outputString;
	}

	/**
	 * This function performs email validation.
	 * 
	 * @return String
	 * @author Gaurav Kumar
	 * @throws Exception
	 * @since 1/24/2017
	 */
	
	public String Ecom_validate_email(String configuration_map_fullpath,
			String tab_name, String test_data) throws IOException, InterruptedException 
	{
		String message = "";
		int count = 0;
		int unreadMsgCount = 0;
		List<String> localPortedTNList = new ArrayList<>();
		ArrayList<String> msgList = new ArrayList<String>();
		
		if(!(addressLine == null))
		{
			if(addressLine.contains("APT"))
			{
				String[] AL = addressLine.split("APT");
				addressLine = AL[0].trim();
			}
		}
		
		if (test_data.contains("FILE_"))
		{			
			String[] expected_value_arr = test_data.split("_");
			String expected_value_file = expected_value_arr[1];			
			test_data = fwgui.fw_get_variable(expected_value_file);
			log.fw_writeLogEntry("	Email Validation Test Data : " + test_data, "NA");
		}
		
		if(test_data.equals("CLEANUP"))
		{
			try 
			{
				log.fw_writeLogEntry("Clean Up Mailbox", "LOGHEADER");
				
				List<WebElement> all_Emails = GUI.driver.findElements(By.xpath("//div[@autoid='_lv_i']/div[contains(@id,'_ariaId_')]"));
				
				int all_Emails_Size = all_Emails.size();
				String xpath = null;
				WebElement elem = null;
				JavascriptExecutor js0 = (JavascriptExecutor) GUI.driver;
				
				if(all_Emails_Size <= 0)
				{
					log.fw_writeLogEntry("No Emails in Inbox to delete", "0");
				}
				else 
				{
					//Right Click on Inbox link
					xpath = "//span[@title='Inbox']"; 
					WebElement inboxElem = GUI.driver.findElement(By.xpath(xpath));
					Actions action = new Actions(GUI.driver);
					action.contextClick(inboxElem).build().perform();
					Thread.sleep(1000);			
					
					//xpath for Empty folder option and then click
					xpath = "//span[text()='Empty folder']";
					GUI.driver.findElement(By.xpath(xpath)).click();
					Thread.sleep(1000);
					
					//xpath for OK button and then click
					xpath = "//span[text()='OK']";
					GUI.driver.findElement(By.xpath(xpath)).click();
					Thread.sleep(1000);
					
					log.fw_writeLogEntry("Inbox emails moved to Deleted Items","0");
				}
				
				//Click on Deleted Items link
				xpath = "//span[@title='Deleted Items']";
				GUI.driver.findElement(By.xpath(xpath)).click();
				Thread.sleep(2000);
				
				all_Emails = GUI.driver.findElements(By.xpath
						("//div[@class='_lv_I3']//div[contains(@id,'_ariaId')]"));
				
				all_Emails_Size = all_Emails.size();
				
				if(all_Emails_Size <= 0)
				{
					log.fw_writeLogEntry("No Emails in Deleted Items to delete", "0");
				}
				else 
				{
					//xpath for Deleted Items link & Right Click on it 
					xpath = "//span[@title='Deleted Items']";
					WebElement deletedItemsElem = GUI.driver.findElement(By.xpath(xpath));
					Actions action = new Actions(GUI.driver);
					action.contextClick(deletedItemsElem).build().perform();
					Thread.sleep(1000);
					
					//xpath for Empty folder option and then click
					xpath = "//span[@autoid='_fce_4' and text()='Empty folder']";
					elem = GUI.driver.findElement(By.xpath(xpath));
					js0.executeScript("arguments[0].click();", elem);
					Thread.sleep(1000);
					
					//xpath for Empty folder option and then click
					xpath = "//span[contains(text(),'items and subfolders in Deleted items?')]/../../../../../../..//span[text()='OK']";
					elem = GUI.driver.findElement(By.xpath(xpath));
					js0.executeScript("arguments[0].click();", elem);
					Thread.sleep(1000);
					
					log.fw_writeLogEntry("All emails are Permanently Deleted","0");
				}				
			}
			catch(Exception e)
			{
				log.fw_writeLogEntry("FAILED to delete Emails","1");
			}
			
			return test_data;
		}
		
		try 
		{
			
			int agreementCount = 1;
			
			String affiliateName = fwgui.fw_get_variable("AffiliateNameID");
			
			if(test_data.contains("Commercial"))
			{
				msgList.add("thank you for your spectrum business order");
				
				if(affiliateName.contains("Business Direct Sales") || affiliateName.contains("OTM") 
						|| affiliateName.contains("Channel Partners"))
				{
					msgList.add("thank you for your spectrum business order");
				}
			}
			else
			{ 
				msgList.add("thank you for your spectrum order");
				
				if(affiliateName.contains("Direct Sales") || affiliateName.contains("Syntelesys") 
						|| test_data.contains("AGENT"))
				{
					msgList.add("thank you for your spectrum order");
				}
			}
			
			if ((!test_data.contains("BAE_CustomerPresentYes") && !test_data.contains("CharterVerify") && !test_data.contains("Commercial")) || (test_data.contains("OnlyE911AgreeOnUI")))
			{		
				msgList.add("your spectrum order - agreement information1");
			}
			else if (test_data.contains("Commercial") && !test_data.contains("BAE_CustomerPresentYes") && !test_data.contains("CharterVerify"))
			{
				msgList.add("your charter order - agreement information1");
			}
			
			if(test_data.contains("CharterVerify"))
			{
				if(test_data.contains("Commercial"))
				{
					msgList.add("important account information - spectrum business terms and conditions");
				}
				else
				{
					msgList.add("important account information - spectrum terms and conditions");
				}
			}
			
			if(!test_data.contains("TWC") && !test_data.contains("BHN"))
			{
				msgList.add("sandbox");	
			}
			
			if (!test_data.contains("BAE_CustomerPresentYes") && !test_data.contains("CharterVerify") && 
					!test_data.contains("NotToValidateE911Agreement") && !test_data.contains("Commercial"))
			{
				msgList.add("your spectrum order - agreement information2");
			}
			else if (test_data.contains("Commercial") && !test_data.contains("BAE_CustomerPresentYes") 
					&& !test_data.contains("CharterVerify") && !test_data.contains("NotToValidateE911Agreement"))
			{
				msgList.add("your charter order - agreement information2");
			}
			
			if(test_data.contains("GISYELLOW"))
			{
				msgList.clear();
				msgList.add("sandbox");
			}
			
			if(test_data.contains("GISORANGE"))
			{
				msgList.clear();
				msgList.add("sandbox");
				msgList.add("thank you for your spectrum business order");
			}
			
			if(test_data.contains("SAVECART"))
			{
				msgList.clear();
				msgList.add("thank you for your spectrum order");
			}
			
			if(test_data.contains("SAVEQUOTE"))
			{
				msgList.clear();
				
				if(test_data.contains("_OTM"))
				{
					msgList.add("your spectrum business cart");
				}
				else
				{
					msgList.add("your spectrum business quote");
				}	
				String fname = fwgui.fw_get_variable("FirstName");
				String lname = fwgui.fw_get_variable("LastName");
				String customer_name = fname + " " + lname;
				
				if (!test_data.contains("_SBQUOTE"))
				{
					msgList.add(customer_name.toLowerCase()+"'s quote has been sent");
				}
			}
			
			if(test_data.contains("Commercial") && test_data.contains("PORTED"))
			{
				for (String value : portedTNList) 
				{
					localPortedTNList.add(value);
				}
			}
			
			log.fw_writeLogEntry("	Expected emails are \n\t" + msgList, "NA");
			
			fwgui.fw_event("", "", "ClickButton","VALIDATE_EMAIL_UnreadTabLocator", "", "NA", "5000");  // click on unread link

			List<WebElement> listOfMessages = null;
			count = msgList.size();

			for(int k = 0; k < 120; k++)
			{
				// obtain list of messages from unread mail
				listOfMessages = GUI.driver.findElements(fwgui.fw_get_element_object("xpath", "//div[@autoid='_lv_3']"));

				unreadMsgCount = listOfMessages.size();
				
				if(unreadMsgCount >= count)
				{
					log.fw_writeLogEntry("Expected count of emails are in Inbox", "0");
					break;
				}
				else
				{
				    if  (k%24==0)
				    {
						log.fw_writeLogEntry("	Expected count of emails NOT in Inbox","NA");
						log.fw_writeLogEntry("	Waiting for 2 Mins...","NA");
					}		
					
					Thread.sleep(5000);
				}
			}

			// iterate through all unread messages
			for (WebElement msg : listOfMessages) 
			{
				String m = msg.getText().toLowerCase();
				String[] getMsgFromOutBox = m.split("\\n");

				if (!msgList.isEmpty()) 
				{
					String filterList = getMsgFromOutBox[1];
					
					if (filterList.contains("sandbox") && filterList.contains(confirmationNumber)) 
					{
						msgList.remove(filterList.substring(0, 7));
						log.fw_writeLogEntry("	Email Validation - Sandbox Email received", "NA");
					} 
					else 
					{
						if ((filterList.contains("your spectrum order - agreement information"))) 
						{
							msgList.remove(filterList + agreementCount);
							log.fw_writeLogEntry("	Email Validation - Agreement " + filterList + agreementCount + " Email received", "NA");
							agreementCount++;
						}
						else if ((filterList.contains("your charter order - agreement information"))) 
						{
							msgList.remove(filterList + agreementCount);
							log.fw_writeLogEntry("	Email Validation - Agreement " + filterList + agreementCount + " Email received", "NA");
							agreementCount++;
						}
						else if(filterList.contains("important account information"))
						{
							msgList.remove(filterList);
							log.fw_writeLogEntry("	Email Validation - Important Account Email received", "NA");
						}
						else 
						{
							msgList.remove(filterList);
							log.fw_writeLogEntry("	Email Validation - " + filterList + " Email received", "NA");
						}
					}
					if (msgList.isEmpty()) 
					{
						log.fw_writeLogEntry("Email Validation - Expected Emails Received in Inbox", "0");
						break;
					}
				}
			}
			
			if(test_data.contains("SAVEQUOTE") || test_data.contains("SAVECART"))
			{
				if(msgList.isEmpty())
					log.fw_writeLogEntry("Email Validation PASSED for Save Quote/Cart", "0");
				else
					log.fw_writeLogEntry("Email Validation FAILED for Save Quote/Cart", "1");
			}
			else
			{			
				// if received all expected emails in unread messages
				if(msgList.isEmpty())
				{
					//Order confirmation validation
					if(test_data.contains("Commercial"))
					{
						try
						{
							log.fw_writeLogEntry("Thank You Email Validation for Spectrum Business ","LOGHEADER");
							fwgui.fw_event("", "","ClickButton","VALIDATE_EMAIL_UnreadTabLocator", "", "NA", "5000");  // click on unread link
							
							/*
							fwgui.fw_event("", "","ClickButton","VALIDATE_EMAIL_emailSearchButtonLocator", "", "NA","5000");  // click on search button
							fwgui.fw_event("", "","EnterDataTextbox","VALIDATE_EMAIL_emailSearchBoxInput", "\"Thank you for your Spectrum Business Order\"", "NA","2000");  // input to search box
							fwgui.fw_event("", "","ClickButton", "VALIDATE_EMAIL_emailSearchSignLocator","", "NA", "5000");  // click on search sign
							*/
							
							List<WebElement> orderCMsg = GUI.driver.findElements(By.xpath("//*[text()='Thank you for your Spectrum Business Order']"));
							
							if (orderCMsg.size() >= 1)
							{
								orderCMsg.get(0).click();
							}
							else
							{
								log.fw_writeLogEntry("No Emails found for - Thank you for your Spectrum Business Order", "1");
								throw new Exception("No Emails found for - Thank you for your Spectrum Business Order");
							}
							
							Thread.sleep(2000);
							
							fwgui.fw_event("", "", "GetText","VALIDATE_EMAIL_emailMessageLocator", "FILE--BusinessThankYouEmail", "NA", "5000");  //getting email text
							String orderConfMsg = GUI.return_get_text;
							//fwgui.fw_event("", "","ClickButton","VALIDATE_EMAIL_emailClearButtonLocator", "", "NA","3000");  // click on clear button
							
							String[] splitOcMessg = orderConfMsg.split("\\n");
							int ocCountNum=0;
							for (String ms : splitOcMessg)
							{
								if (ms.contains("Order Number:"))
								{
									String messageData[] = ms.split("\\:");
									if (messageData[0].equalsIgnoreCase("Order Number")) 
									{
										ocCountNum++;
										if (ocCountNum == 1) 
										{
											if (!(messageData[1].trim().equals(confirmationNumber.trim()))) 
											{
												message += messageData[1] + " from email differs from " + confirmationNumber + " retrievd from UI  ";
												log.fw_writeLogEntry("    " + messageData[1] + " from email differs from " + confirmationNumber + " retrievd from UI ", "1");
											} 
											else
											{
												log.fw_writeLogEntry(" Thank You Email Validation - Confirmation Number For Business matched", "0");
											}
										}
									}
								}
							}
						}
						catch(Exception e)
						{
							message = message + "Thank You Email Validation -- FAILED";
							//fwgui.fw_event("", "","ClickButton","VALIDATE_EMAIL_emailClearButtonLocator", "", "NA","3000");
						}
					}
					else if(!test_data.contains("Commercial"))
					{	
						// Order confirmation for Residential
						try
						{
							log.fw_writeLogEntry("Thank You Email Validation for Residential ","LOGHEADER");
							fwgui.fw_event("", "","ClickButton","VALIDATE_EMAIL_UnreadTabLocator", "", "NA", "5000");  // click on unread link	
							
							/*
							fwgui.fw_event("", "","ClickButton","VALIDATE_EMAIL_emailSearchButtonLocator", "", "NA","5000");  // click on search button
							fwgui.fw_event("", "","EnterDataTextbox","VALIDATE_EMAIL_emailSearchBoxInput", "\"Thank you for your Spectrum Order\"", "NA","2000");  // input to search box - Thank you for your Charter Communications Order
							fwgui.fw_event("", "","ClickButton", "VALIDATE_EMAIL_emailSearchSignLocator","", "NA", "5000");  // click on search sign 
							*/
							
							// Retrieve all Emails with same subject
							// Thank you for your Charter Communications Order
							List<WebElement> orderCMsg = GUI.driver.findElements(By.xpath("//*[text()='Thank you for your Spectrum Order']"));
							
							if (orderCMsg.size() >= 1)
							{
								orderCMsg.get(0).click();
							}
							else
							{
								// Thank you for your Charter Communications order
								orderCMsg = GUI.driver.findElements(By.xpath("//*[text()='Thank you for your Spectrum order']"));
								
								if (orderCMsg.size() >= 1)
								{
									orderCMsg.get(0).click();
								}
								else
								{
									// Thank you for your Charter Communications Order/order
									log.fw_writeLogEntry("No Emails found for - Thank you for your Spectrum Order/order", "1");
									throw new Exception("No Emails found for - Thank you for your Spectrum Order/order");
								}
							}
							Thread.sleep(2000);
							
							fwgui.fw_event("", "", "GetText","VALIDATE_EMAIL_emailMessageLocator", "FILE--ResiThankYouEmail", "NA", "2000");  //getting email text
							
							String orderConfMsg = GUI.return_get_text;
							
							//fwgui.fw_event("", "","ClickButton","VALIDATE_EMAIL_emailClearButtonLocator", "", "NA","3000");  // click on clear button
							
							String[] splitOcMessg = orderConfMsg.split("\\n");
							int ocCountNum=0;
							for (String ms : splitOcMessg)
							{
								if (ms.contains("Order Number:"))
								{
									String messageData[] = ms.split("\\:");
									if (messageData[0].equalsIgnoreCase("Order Number")) 
									{
										ocCountNum++;
										if (ocCountNum == 1) 
										{
											if (!(messageData[1].trim().equals(confirmationNumber.trim()))) 
											{
												message += messageData[1] + " from email differs from " + confirmationNumber + " retrievd from UI  ";
												log.fw_writeLogEntry("    " + messageData[1] + " from email differs from " + confirmationNumber + " retrievd from UI ", "1");
											}
											else
											{
												log.fw_writeLogEntry(" Thank You Email Validation - Confirmation Number matched", "0");	
											}
										}
									}
								}
							}
						} 
						catch(Exception e)
						{
							message = message + "Thank You Email Validation -- FAILED";
							//fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailClearButtonLocator", "", "NA", "3000");
						}
					}
				}
				
				//General Terms & Condition Email Validation
				if(msgList.isEmpty())
				{
					//General T&C for commercial SB
					if(test_data.contains("Commercial") && (!test_data.contains("CharterVerify")) && (!test_data.contains("BAE_CustomerPresentYes")))
					{
						try
						{
							String termAndCondMsg = "";
							log.fw_writeLogEntry("Terms & Condition Validation for Business Spectrum", "LOGHEADER");
							fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_UnreadTabLocator", "", "NA", "5000"); // click on unread link
							
							/*
							fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailSearchButtonLocator", "", "NA", "5000"); // click on search button
							fwgui.fw_event("", "", "EnterDataTextbox", "VALIDATE_EMAIL_emailSearchBoxInput", "\"COMMERCIAL TERMS OF SERVICE\"", "NA", "2000"); // input to search box
							fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailSearchSignLocator", "", "NA", "5000"); // click on search sign
							*/
							
							List<WebElement> orderCMsg = GUI.driver.findElements(By.xpath("//*[text()='Your Charter Order - Agreement Information']"));
							int msgCount = orderCMsg.size();
							System.out.println(msgCount);
							
							if (orderCMsg.size() >= 1)
							{
								//orderCMsg.get(0).click();
								for(int i = 0; i < msgCount; i++)
								{
									orderCMsg.get(i).click();
									fwgui.fw_event("", "", "GetText", "VALIDATE_EMAIL_emailMessageLocator", "FILE--BusinessTnCEmail", "NA", "3000"); //getting email text
									termAndCondMsg = GUI.return_get_text;
									if(termAndCondMsg.contains("Commercial Terms of Service"))
									{
										break;
									}
								}
							}
							else
							{
								log.fw_writeLogEntry("No Emails found for - Terms & Condition", "1");
								throw new Exception("No Emails found for - Terms & Condition");
							}
							
							//Thread.sleep(5000);
							//fwgui.fw_event("", "", "GetText", "VALIDATE_EMAIL_emailMessageLocator", "FILE--BusinessTnCEmail", "NA", "5000"); //getting email text
							//fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailClearButtonLocator", "", "NA", "3000"); //click on clear button
							
							String[] splitOcMessg = termAndCondMsg.split("\\n");
							int nameCount=0, addressCount=0 ;
							for (String ms : splitOcMessg)
							{
	
								if (ms.contains("Name:") || ms.contains("Address:"))
								{
									String messageData[] = ms.split("\\:");
									if (messageData[0].equalsIgnoreCase("Name")) 
									{
										nameCount++;
										if (nameCount == 1) 
										{
											if (!(messageData[1].trim().toLowerCase().equals(customerName.trim().toLowerCase()))) 
											{
												message += messageData[1] + " from email differs from "+ customerName + " retrievd from UI  ";
												log.fw_writeLogEntry("    " + messageData[1] + " from email differs from " + customerName + " retrievd from UI ", "1");
	
											} 
											else
											{
												log.fw_writeLogEntry(" Spectrum Business Terms & Condition - Customer Name matched","0");	
											}
										}
									} 
									else if (messageData[0].equalsIgnoreCase("Address")) 
									{
										addressCount++;
										if (addressCount == 1) 
										{	
											if (messageData[1].contains(","))
											{
												messageData[1] = messageData[1].replace(",","");
											}
							
											if (!(messageData[1].trim().toLowerCase().contains(addressLine.trim().toLowerCase()))) 
											{
												message += messageData[1] + " from email differs from " + addressLine + " retrievd from UI  ";
												log.fw_writeLogEntry("    " + messageData[1] + " from email differs from " + addressLine + " retrievd from UI ", "1");
	
											}
	
											else
											{
												log.fw_writeLogEntry(" Spectrum Business Terms & Condition  - Customer Address matched", "0");
											}
										}
									}
								}
							}
							
						}
						catch(Exception e)
						{
							message = message + "Spectrum Business Terms & Condition Email -- FAILED\n";
							//fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailClearButtonLocator", "", "NA", "3000");
						}
					}
					else if ((!test_data.contains("CharterVerify")) && (!test_data.contains("BAE_CustomerPresentYes")))
					{
						//General T&C for Residential
						try
						{
							String orderConfMsg = "";
							
							log.fw_writeLogEntry("General T&C Validation for Spectrum Residential ", "LOGHEADER");
							fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_UnreadTabLocator", "", "NA", "5000"); // click on unread link
							
							/*
							fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailSearchButtonLocator", "", "NA", "5000"); // click on search button
							fwgui.fw_event("", "", "EnterDataTextbox", "VALIDATE_EMAIL_emailSearchBoxInput", "\"General Terms and Conditions for Charter Residential Services\"", "NA", "2000"); // input to search box
							fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailSearchSignLocator", "", "NA", "5000"); // click on search sign
							*/
							
							List<WebElement> orderCMsg = GUI.driver.findElements(By.xpath("//*[text()='Your Spectrum Order - Agreement Information']"));
							
							int msgCount = orderCMsg.size();
							System.out.println(msgCount);
							
							if (orderCMsg.size() >= 1)
							{
								//orderCMsg.get(0).click();
								
								for(int i = 0; i < msgCount; i++)
								{
									orderCMsg.get(i).click();
									fwgui.fw_event("", "", "GetText", "VALIDATE_EMAIL_emailMessageLocator", "FILE--ResiTnCEmail", "NA", "5000"); // getting email text
									orderConfMsg = GUI.return_get_text;
									if(orderConfMsg.contains("General Terms and Conditions for Charter Residential Services"))
									{
										break;
									}
								}
							}
							else
							{
								log.fw_writeLogEntry("No Emails found for - Terms & Condition", "1");
								throw new Exception("No Emails found for - Terms & Condition");
							}
							
							//Thread.sleep(5000);
							//fwgui.fw_event("", "", "GetText", "VALIDATE_EMAIL_emailMessageLocator", "FILE--ResiTnCEmail", "NA", "5000");
							//fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailClearButtonLocator", "", "NA", "3000"); // click on clear button
							
							String[] splitOcMessg = orderConfMsg.split("\\n");
							int addressCount=0, nameCount=0;
							for (String ms : splitOcMessg)
							{
								if (ms.contains("Name:") || ms.contains("Address:"))
								{
									String messageData[] = ms.split("\\:");
									if (messageData[0].equalsIgnoreCase("Name")) 
									{
										nameCount++;
										if (nameCount == 1) 
										{
											if (!(messageData[1].trim().toLowerCase().equals(customerName.trim().toLowerCase())))
											{
												message += messageData[1] + " from email differs from " + customerName + " retrievd from UI  ";
												log.fw_writeLogEntry("    " + messageData[1] + " from email differs from " + customerName + " retrievd from UI ", "1");
											} 
											else
											{
												log.fw_writeLogEntry(" General T&C Email Validation - Customer Name matched","0");
											}
										}
									} 
									else if (messageData[0].equalsIgnoreCase("Address")) 
									{
										addressCount++;
										if (addressCount == 1) 
										{
											if (!(messageData[1].trim().toLowerCase().contains(addressLine.trim().toLowerCase()))) 
											{
												message += messageData[1] + " from email differs from " + addressLine + " retrievd from UI  ";
												log.fw_writeLogEntry("    " + messageData[1] + " from email differs from " + addressLine + " retrievd from UI ", "1");
	
											}
											else
											{
												log.fw_writeLogEntry(" General T&C Email Validation - Customer Address matched", "0");
											}
										}
									}
								}
							}
						}
						catch(Exception e)
						{
							message = message + "General T&C Email Validation -- FAILED\n";
							//fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailClearButtonLocator", "", "NA", "3000");
						}
					}
				}
				
				//Email validation for Ported Number Spectrum Business
				if(msgList.isEmpty())
				{
					if(test_data.contains("Commercial") && test_data.contains("PORTED") && !(test_data.contains("_NOCHECK")))
					{
						try
						{
							log.fw_writeLogEntry("ELOA validation for Ported Number Business Spectrum", "LOGHEADER");
							
							// click on unread link
							fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_UnreadTabLocator", "", "NA", "5000");
							
							// click on search button
							fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailSearchButtonLocator", "", "NA", "5000");
	
							// input to search box
							fwgui.fw_event("", "", "EnterDataTextbox", "VALIDATE_EMAIL_emailSearchBoxInput", "\"Consent to Electronic Transaction\"", "NA", "2000");
							
							// click on search sign
							fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailSearchSignLocator", "", "NA", "5000");
							
							List<WebElement> orderCMsg = GUI.driver.findElements(By.xpath("//*[text()='Your Charter Order - Agreement Information']"));
							
							if (orderCMsg.size() >= 1)
							{
								orderCMsg.get(0).click();
							}
							else
							{
								log.fw_writeLogEntry("No Emails found for - Porting ELOA", "1");
								throw new Exception("No Emails found for - Porting ELOA");
							}
							
							Thread.sleep(5000);
							
							//getting email text
							fwgui.fw_event("", "", "GetText", "VALIDATE_EMAIL_emailMessageLocator", "FILE--BusinessELOAEmail", "NA", "5000");
							
							String ms = GUI.return_get_text;
							
							//click on clear button
							fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailClearButtonLocator", "", "NA", "3000");
							
							String [] splitPortMail = ms.split("\\n");
							int portCount=0;
							String emailPortedTNs = null;
							for (String portemail : splitPortMail)
							{
								if(portemail.contains("Telephone Number(s) (including area code):"))
								{
									String [] messageData = portemail.split("\\:");
									emailPortedTNs = messageData[1].trim();
									String [] portedNum = emailPortedTNs.split("\\,");
									String firstTN = portedNum[0];
									String firstTnWoutDash = firstTN.substring(0,3) + firstTN.substring(4,7) + firstTN.substring(8);
									
									if(messageData[0].equalsIgnoreCase("Telephone Number(s) (including area code)"))
									{
										portCount++;
										if(portCount==1)
										{
											//int TNcount = localPortedTNList.size();
											//for(int i=0;i<TNcount;i++)
											//{
												emailPortedTNs = localPortedTNList.get(0);
												emailPortedTNs = emailPortedTNs.substring(1, 4) + emailPortedTNs.substring(6,9) + emailPortedTNs.substring(10);
												System.out.println("emailPortedTN : " + emailPortedTNs);
											//}
											if (!(firstTnWoutDash.trim().equals(emailPortedTNs.trim()))) 
											{
												message += portedNum[0] + " from email differs from " + portedTNList.get(0).trim() + " retrievd from UI  ";
												log.fw_writeLogEntry("    " + portedNum[0] + " from email differs from " + portedTNList.get(0).trim() + " retrievd from UI ", "1");
											} 
											else
											{
												log.fw_writeLogEntry(" ELOA Spectrum Business Ported Number Validation - Ported Number matched", "0");	
											}
										}
									}
								}
							}
						}
						catch (Exception e)
						{
							message = message + "Porting ELOA Email Validation -- FAILED\n";
							fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailClearButtonLocator", "", "NA", "3000");
						}
					}
					else if((test_data.contains("PORTED") && (!test_data.contains("CharterVerify"))) && 
							(!test_data.contains("Commercial") || !test_data.contains("BAE_CustomerPresentYes")))
					{
						try
						{
							log.fw_writeLogEntry("ELOA validation for Ported Number Residential ", "LOGHEADER");
							
							// click on unread link
							fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_UnreadTabLocator", "", "NA", "5000");
							
							// click on search button
							fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailSearchButtonLocator", "", "NA", "5000");
	
							// input to search box
							fwgui.fw_event("", "", "EnterDataTextbox", "VALIDATE_EMAIL_emailSearchBoxInput", "\"Telephone Service Electronic Letter of Authorization\"", "NA", "2000");
							
							// click on search sign
							fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailSearchSignLocator", "", "NA", "5000");
							
							List<WebElement> orderCMsg = GUI.driver.findElements(By.xpath("//*[text()='Your Spectrum Order - Agreement Information']"));
							
							if (orderCMsg.size() >= 1)
							{
								orderCMsg.get(0).click();
							}
							else
							{
								log.fw_writeLogEntry("No Emails found for - Porting ELOA", "1");
								throw new Exception("No Emails found for - Porting ELOA");
							}
							
							Thread.sleep(5000);
							
							//getting email text
							fwgui.fw_event("", "", "GetText", "VALIDATE_EMAIL_emailMessageLocator", "FILE--ResiELOAEmail", "NA", "5000");
							
							String ms = GUI.return_get_text;
							
							//click on clear button
							fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailClearButtonLocator", "", "NA", "3000");
							
							//Added by Irfan - 01/22/2018
							if((test_data.contains("PORTED")) && (test_data.contains("NONAUTO")))
							{								
								log.fw_writeLogEntry(" ELOA Residential Validation - Email found", "0");
							}
							else
							{
							//End of Added by Irfan - 01/22/2018
							
								String [] splitPortMail = ms.split("\\n");
								int portCount=0;
								String emailPortedTNs = null;
								for (String portemail : splitPortMail)
								{
									if(portemail.contains("Telephone Numbers (including area code):"))
									
									{
										String [] messageData = portemail.split("\\:");
										emailPortedTNs = messageData[1].trim();
										String [] portedNum = emailPortedTNs.split("\\,");
										String firstTN = portedNum[0];
										//String firstTnWoutDash = firstTN.substring(0,3) + firstTN.substring(4,7) + firstTN.substring(8);
										System.out.println("Ported TN in Email : " + firstTN);
										
										
										if(messageData[0].equalsIgnoreCase("Telephone Numbers (including area code)"))
										{
											portCount++;
											if(portCount == 1)
											{
												//int TNcount = localPortedTNList.size();
												//for(int i=0;i<TNcount;i++)
												//{
													//emailPortedTNs = localPortedTNList.get(0);
													//emailPortedTNs = emailPortedTNs.substring(1, 4) + emailPortedTNs.substring(6,9) + emailPortedTNs.substring(10);
													//System.out.println("emailPortedTN : " + emailPortedTNs);
												//}
												if (!(firstTN.trim().equals(portedTN.trim()))) 
												{
													message += portedNum[0] + " from email differs from " + portedTN + " retrievd from UI  ";
													log.fw_writeLogEntry("    " + portedNum[0] + " from email differs from " + portedTN + " retrievd from UI ", "1");
												} 
												else
												{
													log.fw_writeLogEntry(" ELOA Residential Validation - Ported Number matched", "0");
												}
											}
										}
									}
								}
							}
						}
						catch (Exception e)
						{
							message = message + "Porting ELOA Email Validation -- FAILED\n";
							fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailClearButtonLocator", "", "NA", "3000");
						}
					}
				}
				
				//Salesforce Email Validation			
				if (msgList.isEmpty() && !test_data.contains("TWC") && !test_data.contains("BHN")) 
				{
	
					log.fw_writeLogEntry("Salesforce Email Validation", "LOGHEADER");
					fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_UnreadTabLocator", "", "NA", "3000");
					fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailSearchButtonLocator", "", "NA", "3000");
					fwgui.fw_event("", "", "EnterDataTextbox", "VALIDATE_EMAIL_emailSearchBoxInput", "Sandbox", "NA", "2000");
					fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailSearchSignLocator", "", "NA", "5000");
					fwgui.fw_event("", "", "GetText", "VALIDATE_EMAIL_emailMessageLocator", "FILE--SandboxEmail", "NA", "5000");
	
					String msg1 = GUI.return_get_text;
	
					fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_emailClearButtonLocator", "", "NA", "3000");
					fwgui.fw_event("", "", "ClickButton", "VALIDATE_EMAIL_unreadIconLocator", "", "NA", "3000");
	
					String[] splitMsg = msg1.split("\\n");
	
					int nameCount = 0, emailCount = 0, addressCount = 0, confNumCount = 0;
					Boolean flag = false;
					
					for (String ms : splitMsg) 
					{
	
						if (ms.contains("CSG Account #") || ms.contains("Tracking ID") 
								|| ms.contains("Address:") || ms.contains("Email Address:") 
								|| ms.contains("Name:") || ms.contains("Order Number:")) 
						{
	
							String messageData[] = ms.split("\\:");
	
							if (messageData[0].equalsIgnoreCase("CSG Account #")) 
							{
								if (messageData.length > 1) 
								{
									if (!(messageData[1].trim().equals(AccountNumber.trim()))) 
									{
										message += messageData[1] + " from email differs from " + AccountNumber + " retrievd from UI ";
										log.fw_writeLogEntry("    " + messageData[1] + " from email differs from " + AccountNumber + " retrievd from UI ", "1");
									} 
									else
									{
										log.fw_writeLogEntry(" Salesforce Email Validation - Account Number matched", "0");	
									}
								}
							} 
							else if (messageData[0].equalsIgnoreCase("G2B Tracking ID")) 
							{
								if (!(messageData[1].trim().equals(TrackingId.trim()))) 
								{
									message += messageData[1] + " from email differs from " + TrackingId + " retrievd from UI  ";
									log.fw_writeLogEntry("    " + messageData[1] + " from email differs from " + TrackingId + " retrievd from UI ", "1");
								} 
								else
								{
									log.fw_writeLogEntry(" Salesforce Email Validation - Tracking ID matched", "0");
								}
							}
							else if (messageData[0].equalsIgnoreCase("Order Number")) 
							{
								confNumCount++;
								if (confNumCount == 1) 
								{
									if (!(messageData[1].trim().equals(confirmationNumber.trim()))) 
									{
										message += messageData[1] + " from email differs from " + confirmationNumber + " retrievd from UI  ";
										log.fw_writeLogEntry("    " + messageData[1] + " from email differs from " + confirmationNumber + " retrievd from UI ", "1");
									} 
									else
									{
										log.fw_writeLogEntry(" Salesforce Email Validation - Confirmation Number matched", "0");
									}
								}
							}
							else if (messageData[0].equalsIgnoreCase("Name")) 
							{
								nameCount++;
								if (nameCount == 1) 
								{
									if (!(messageData[1].trim().toLowerCase().equals(customerName.trim().toLowerCase()))) 
									{
										message += messageData[1] + " from email differs from " + customerName + " retrievd from UI  ";
										log.fw_writeLogEntry("    " + messageData[1] + " from email differs from " + customerName + " retrievd from UI ", "1");
									} 
									else
									{
										log.fw_writeLogEntry(" Salesforce Email Validation - Customer Name matched","0");
									}
								}
							} 
							else if (messageData[0].equalsIgnoreCase("Address")) 
							{
								addressCount++;
								if (addressCount == 1) 
								{
									boolean addrMatchCondition  = false;									
									if(addressLine.contains(" APT") || addressLine.contains(" LOT"))
									{
										addrMatchCondition = !(addressLine.trim().toLowerCase()).contains(messageData[1].trim().toLowerCase());
									}
									else
									{
										addrMatchCondition = !(messageData[1].trim().toLowerCase().contains(addressLine.trim().toLowerCase()));
									}
									
									if (addrMatchCondition)
									//if(!(addressLine.trim().toLowerCase()).contains(messageData[1].trim().toLowerCase()))
									{
										message += messageData[1] + " from email differs from " + addressLine + " retrievd from UI  ";
										log.fw_writeLogEntry("    " + messageData[1] + " from email differs from " + addressLine + " retrievd from UI ", "1");
									}
									else
									{
										log.fw_writeLogEntry(" Salesforce Email Validation - Customer Address matched", "0");
									}
								}
							} 
							else if (messageData[0].equalsIgnoreCase("Email Address")) 
							{
								emailCount++;
								if (emailCount == 1) 
								{
									if (!(messageData[1].trim().toLowerCase().equals(emailId.trim().toLowerCase()))) 
									{
										message += messageData[1] + " from email differs from " + emailId + " retrievd from UI  ";
										log.fw_writeLogEntry("    " + messageData[1] + " from email differs from " + emailId + " retrievd from UI ", "1");
									} 
									else
									{
										log.fw_writeLogEntry(" Salesforce Email Validation - Customer Email matched", "0");	
									}
								}
							}
						}
						else if (ms.contains("Current Phone Number") || ms.contains("Ported Number"))
						{
							String [] messageData = ms.split("\\(");
							String getPortedTN = messageData[1].trim();
							String emailPortedTN = null;
														
							if(test_data.contains("Commercial") && test_data.contains("PORTED"))
							{								
								int TNcount = localPortedTNList.size();
								
								for(int i=0;i<TNcount;i++)
								{
									emailPortedTN = localPortedTNList.get(i);
									emailPortedTN = emailPortedTN.substring(1, 4) + emailPortedTN.substring(6,9) + emailPortedTN.substring(10);
									
									if(getPortedTN.endsWith(")"))
									{
										getPortedTN = getPortedTN.replace(")", "");
									}
									
									if (getPortedTN.equals(emailPortedTN))
									{
										log.fw_writeLogEntry(" Salesforce Email Validation - Ported TN " + emailPortedTN + " matched", "NA");
										localPortedTNList.remove(i);
										break;
									} 
									else
									{
										log.fw_writeLogEntry(" Salesforce Email Validation - Ported TN NOT matched", "NA");
									}
								}
							}
							else 
							{
								String PortedTNmatched="";
								if (flag == true)
								{
									emailPortedTN = portedTN2NoDash;
									PortedTNmatched = "Ported TN2 matched";
								}
								else
								{
									PortedTNmatched = "Ported TN matched";
								}
									
								if(getPortedTN.contains("-"))
								{
									emailPortedTN = portedTN;
								}
								else 
								{
									emailPortedTN = portedTNNoDash;
								}
								
								if(getPortedTN.endsWith(")"))
								{
									getPortedTN = getPortedTN.replace(")", "");
								}
								
								if (!(getPortedTN.equals(emailPortedTN)))
								{
									message += getPortedTN + " from email differs from " + emailPortedTN + " (actual : " + portedTN + " ) retrievd from UI";
									log.fw_writeLogEntry("    " + getPortedTN + " from email differs from " + emailPortedTN + " (actual : " + portedTN + " ) retrievd from UI ", "1");
								} 
								else
								{	
									log.fw_writeLogEntry(" Salesforce Email Validation - " + PortedTNmatched, "0");
								}
								
								if(!portedTN2NoDash.equals(""))
								{
								    //portedTN1NoDash = portedTNNoDash;             portedTN1NoDash has the original value of portedTNNoDash and can be use wherever it is required.
								    portedTNNoDash = portedTN2NoDash;
									flag = true;
								}
							}
						}
					}
				}
			}
			
			//Store all the NOT Received messages in a variable.
			if(!msgList.isEmpty())
			{
				for (String msg : msgList) 
				{
					if (msg.contains("information2"))
					{
						msg = "Charter E911 Agreement";
					}
					else if (msg.contains("information1"))
					{
						msg = "General Terms and Conditions Agreement";
					}
					
					message += msg + " not found\n";
				}
			}
		}
		catch (Exception e) 
		{
			message = message + "Salesforce Email Validation -- FAILED\n";
		}
		
		if(test_data.contains("Commercial") && test_data.contains("PORTED"))
		{
			if(localPortedTNList.isEmpty())
			{
				log.fw_writeLogEntry(" Salesforce Email Validation - Ported TNs " + portedTNList + " matched", "0");
			}
			else
			{
				message += localPortedTNList + " from email differs from " + portedTNList + " retrievd from UI";
				log.fw_writeLogEntry(" Salesforce Email Validation - Ported TNs " + portedTNList + " NOT matched", "1");
			}
		}
		
		ArrayList<String> emailContent = new ArrayList<String>();

		if (test_data.contains("BAE_HSD") || test_data.contains("BAE_PHONE") || test_data.contains("BAE_VIDEO")) 
		{
			try 
			{
				String windowHandle = fwgui.fw_get_window_handle();

				List<WebElement> element = GUI.driver.findElements(By.xpath("//*[text()='Important Account Information - Spectrum Business Terms and Conditions']"));

				if (element.size() == 1)
				{
					fwgui.fw_event("", "", "ClickButton","CHARTERVERIFY_OpenTermAndConditionEmail_BAE", "", "NA", "5000");
				}
				else
				{
					element.get(0).click();
				}
				
				Thread.sleep(5000);
				fwgui.fw_event("", "", "ClickButton","CHARTERVERIFY_EmailLinkTermAndCondition_BAE", "", "NA", "6000");

				// Switching to new window
				fwgui.fw_switch_to_new_window();

				if (test_data.contains("PHONE")) 
				{
					// Accepting E911 agreement
					fwgui.fw_event("", "", "ClickButton","CHARATERVERIFY_CheckBoxTermAndCondition", "", "NA", "2000");
					fwgui.fw_event("", "", "ClickButton", "CHARATERVERIFY_AcceptButton","", "NA", "6000");
				
					emailContent.add("Enjoy your Telephone Service – copy of agreement(s) attached");
				}

				emailContent.add("Enjoy your Commercial Service- copy of Terms and Conditions attached");
				
				// Accepting Commercial Terms Of Service
				fwgui.fw_event("", "", "ClickButton","CHARATERVERIFY_CheckBoxCommercialTermsOfService_BAE", "", "NA", "2000");
				fwgui.fw_event("", "", "ClickButton","CHARATERVERIFY_AcceptCommercialTermsOfService_BAE", "", "NA", "5000");

				
				// Closing current window
				GUI.driver.close();
				
				// Switching back to outlook window
				fwgui.fw_switch_to_window(windowHandle);

				// ***** Validating the charter verifying email *****
				int flag_BAE = 0;
				
				Thread.sleep(10000);
				
				GUI.driver.navigate().refresh();
				
				Thread.sleep(20000);

				// Get Webelement of all the mail
				List<WebElement> emailList = GUI.driver.findElements(By.xpath("//div[6]/div[2]/div[1]/div[1]//div[@role='heading']/../div"));

				for (int emailContentIndex = 0; emailContentIndex < emailContent.size(); emailContentIndex++) 
				{
					for (int emailClick = 1; emailClick < 5; emailClick++) 
					{
						emailList.get(emailClick).click();
						String emailHeading = GUI.driver.findElement(By.xpath("//span[@class='rpHighlightAllClass rpHighlightSubjectClass']")).getText();
						if (emailHeading.contains(emailContent.get(emailContentIndex))) 
						{
							flag_BAE = 1;
							break;
						}
					}
					if (flag_BAE == 1) 
					{
						log.fw_writeLogEntry("Email Validation passed for : " + emailContent.get(emailContentIndex), "0");
						flag_BAE = 0;
					} else 
					{
						log.fw_writeLogEntry("Email Validation failed for : " + emailContent.get(emailContentIndex), "1");
					}
				}
			} 
			catch (Exception e) 
			{
				log.fw_writeLogEntry("Commercial Charter Verify -- FAILED", "1");
				message = message + "BAE Charter Verify -- FAILED\n";
			}
		}
		
		if ((test_data.contains("RETAIL_"))||(test_data.contains("DIRECTCUSTNO_"))) 
		{
			try
			{

				String windowHandle = fwgui.fw_get_window_handle();

				List<WebElement> element = GUI.driver.findElements(By.xpath("//*[text()='Important Account Information - Spectrum Terms and Conditions']"));

				if (element.size() == 1)
				{
					fwgui.fw_event("", "", "ClickButton","CHARTERVERIFY_OpenTermAndCondition_RESI", "", "NA", "5000");
				}
				else
				{
					element.get(0).click();
				}
				
				Thread.sleep(5000);

				// Clicking the link in the mail
				fwgui.fw_event("", "", "ClickButton","CHARTERVERIFY_EmailLinkTermAndCondition_RESI", "", "NA", "5000");

				// Switching to new window
				fwgui.fw_switch_to_new_window();
				
				if(test_data.contains("PORTED"))
				{
					fwgui.fw_event("", "", "ClickButton","CHARTERVERIFY_RetailSvcProviderChgCheckbox", "", "NA", "3000");
					fwgui.fw_event("", "", "ClickButton", "CHARTERVERIFY_RetailE911ServicesCheckbox","", "NA", "3000");
					fwgui.fw_event("", "", "ClickButton", "CHARTERVERIFY_RetailElectronicSignCheckbox","", "NA", "3000");
				}
				else 
				{
					fwgui.fw_event("", "", "ClickButton","CHARATERVERIFY_CheckBoxTermAndCondition", "", "NA", "3000");
				}
				
				fwgui.fw_event("", "", "ClickButton", "CHARATERVERIFY_AcceptButton","", "NA", "5000");
				
				if(test_data.contains("PHONE") && !(test_data.contains("EXISTING")))
				{
					fwgui.fw_event("", "", "ClickButton","CHARATERVERIFY_CheckBoxTermAndCondition", "", "NA", "3000");
					fwgui.fw_event("", "", "ClickButton", "CHARATERVERIFY_AcceptButton","", "NA", "6000");		
					emailContent.add("Enjoy your Telephone Service – copy of agreement(s) attached");
				}
				
				emailContent.add("Enjoy your Residential Service- copy of Terms and Conditions attached");

				// Closing current window
				GUI.driver.close();
				
				// Switching back to outlook window
				fwgui.fw_switch_to_window(windowHandle);

				// ***** Validating the charter verifying email *****
				int flag_RESI = 0;
				
				Thread.sleep(15000);
				
				GUI.driver.navigate().refresh();
				
				Thread.sleep(20000);

				// Get Webelement of all the mail
				List<WebElement> emailList = GUI.driver.findElements(By.xpath("//div[6]/div[2]/div[1]/div[1]//div[@role='heading']/../div"));

				for (int emailContentIndex = 0; emailContentIndex < emailContent.size(); emailContentIndex++) 
				{
					for (int emailClick = 1; emailClick < 5; emailClick++) 
					{
						emailList.get(emailClick).click();
						String emailHeading = GUI.driver.findElement(By.xpath("//span[@class='rpHighlightAllClass rpHighlightSubjectClass']")).getText();
						if (emailHeading.contains(emailContent.get(emailContentIndex))) 
						{
							flag_RESI = 1;
							break;
						}
					}
					if (flag_RESI == 1) 
					{
						log.fw_writeLogEntry("Email Validation passed for : " + emailContent.get(emailContentIndex), "0");
						flag_RESI = 0;
					} 
					else 
					{
						log.fw_writeLogEntry("Email Validation failed for : " + emailContent.get(emailContentIndex), "1");
					}
				}
			} 
			catch (Exception e) 
			{
				log.fw_writeLogEntry("Resi Charter Verify -- FAILED", "1");
				message = message + "Resi Charter Verify -- FAILED\n";
			}
		}
		
		if (!message.isEmpty()) 
		{
			log.fw_writeLogEntry("Email Validation FAILED due to: " + message, "1");
		}
		else
		{
			log.fw_writeLogEntry("Email Validation - PASSED", "0");
		}

		return message;
	}
	
	
	/**
	 * This method is to get the Tracking ID & Session ID for current execution
	 * @param alm_test_id
	 * @return {@link String}
	 * @throws InterruptedException
	 * @author Dhaval D Parkhi
	 * @throws IOException 
	 * @since 02/10/2017
	 * 
	 */
	
	public String Ecom_getTrackingID(String test_data) throws Exception 
	{	
		if(test_data.contains("RETAILSLS")) 
		{
			fwgui.fw_event("", "Component", "NavigateToURL", "NA", "RETAILDEBUGURL", "NA", "2000");
		}
		else if (test_data.contains("CHPSLS"))
		{
			fwgui.fw_event("", "Component", "NavigateToURL", "NA", "CHPDEBUGURL", "NA", "2000");
		}
		else 
		{
			String url = GUI.driver.getCurrentUrl();
			String DebugURL = url.split(".com")[0]+".com/output_debug_info.jsp";
			fwgui.fw_event("", "", "SetVariable", "NA", "ENVDebugURL," + DebugURL, "NA", "0");
			fwgui.fw_event("", "", "NavigateToURL", "NA", "DebugURL", "NA", "3000");
		}
		
		String idValue = "";
		
		fwgui.fw_event("", "", "GetText", "DebugSessionID", "FILE--SessionId", "", "1000");
		fwgui.fw_event("", "", "GetText", "DebugTrackingID", "FILE--TrackingId", "", "1000");
		idValue = GUI.return_get_text.trim();
		
		TrackingId = idValue;
		
		if(GUI.driver.getCurrentUrl().contains("output_debug_info.jsp")) 
		{
			fwgui.fw_event("", "", "NavigateBack", "NA", "", "", "2000");
		}
		return idValue;
	}
	
	/**
	 * This method is used to click links if found ( in case of Switch to Calendar View, Proceed with New Order
	 *  and Continue with newly Saved Cart ).
	 * @param test_data
	 * @throws IOException
	 * @throws InterruptedException
	 * @author Dhaval D Parkhi
	 * @since 03/20/2017
	 * 
	 */
	
	public void Ecom_ClickOnObjectIfFound (String test_data) throws InterruptedException, IOException 
	{
		
		WebDriverWait wait = new WebDriverWait(GUI.driver, 10);
		String test_data_array[] = test_data.split("<>");
		
		if(test_data.contains("calendar") && !test_data.toLowerCase().contains("retail"))
		{
			for(int i = 0; i < 30 ; i++)
			{
				String currenturl = GUI.driver.getCurrentUrl();
				if(currenturl.toLowerCase().contains("schedule"))
				{
					break;
				}
				else
				{
					Thread.sleep(1000);
				}
			}
		}
		
		if(test_data.toLowerCase().contains("cart"))
		{
			wait = new WebDriverWait(GUI.driver, 30);
		}
		
		try 
		{
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(fwgui.fw_get_element_object(test_data_array[0], test_data_array[1])));
			wait.until(ExpectedConditions.visibilityOfElementLocated(fwgui.fw_get_element_object(test_data_array[0], test_data_array[1]))).click();
			Thread.sleep(2000);
			
		} 
		catch(Exception e) 
		{
			System.out.println(test_data_array[2] + " not found");
		}
		
		log.fw_writeLogEntry(test_data_array[2], "0");	
		
	}
	
	
	/**
	 * This method is used to cancel pending orders on an account in CSG or ICOMS
	 * @throws InterruptedException
	 * @throws IOException
	 * @author Dhaval Parkhi
	 * @since 10/25/2017
	 * 
	 */
	
	public void Ecom_CancelCSGOrdersPerAddress ()
	{
		String return_code_output = "";
		String csg_icoms_cancel_status = "";
		
		try 
		{
			String Address_Type = fwgui.fw_get_variable("AddressType");
			
			if(Address_Type.contains("CHTR"))
			{
				String Address1_Value = fwgui.fw_get_variable("Address1");
				String ZipCode_Value = fwgui.fw_get_variable("ZipCode");
				
				if (Address1_Value != null && ZipCode_Value != null)
				{
					fwgui.fw_event("", "", "XMLExecute", "WEBSERVICE_GetAccounts", "XML_ECOMM_STREET_ADDRESS,FILE_Address1--XML_ECOMM_ZIP_CODE,FILE_ZipCode", "NA", "2000");
					return_code_output = fwgui.fw_validate_text_in_xml_response("GetAccounts", "<AddressLine1>", "NO", 0);
					if (return_code_output.contains("0--"))
					{
						fwgui.fw_event("", "", "XMLGetValueByMultipleTagnames", "WEBSERVICE_GetAccounts", "SEARCHFORWARD--SysPrin&&SysPrin", "NA", "100");
						fwgui.fw_event("", "", "XMLGetValueByMultipleTagnames", "WEBSERVICE_GetAccounts", "SEARCHFORWARD--LocationId&&LocationId", "NA", "100");
						
						String SysPrin = fwgui.fw_get_variable("SysPrin");
						String LocationId = fwgui.fw_get_variable("LocationId");
						
						log.fw_writeLogEntry("  SysPrin: " + SysPrin, "NA");
						log.fw_writeLogEntry("  Location ID: " + LocationId, "NA");
						
						int cntval = 0;
						String continue_flag = "yes";
						
						do
						{
							cntval = cntval + 1;
							
							fwgui.fw_event("", "", "XMLExecute", "WEBSERVICE_GetOrders", "XML_ECOMM_ROUTING_AREA,FILE_SysPrin--XML_ECOMM_LOCATION_ID,FILE_LocationId", "NA", "3000");
							return_code_output = fwgui.fw_validate_text_in_xml_response("GetOrders", "<OrderId>", "NO", 0);
							if (return_code_output.contains("0--"))
							{
								fwgui.fw_event("", "", "XMLGetValueByMultipleTagnames", "WEBSERVICE_GetOrders", "SEARCHFORWARD--OrderId&&OrderId", "NA", "0");
		
								String orderid = fwgui.fw_get_variable("OrderId");
								log.fw_writeLogEntry("	Order ID: " + orderid, "NA");
								
								fwgui.fw_event("", "", "XMLExecute", "WEBSERVICE_CancelOrder", "XML_ECOMM_ROUTING_AREA,FILE_SysPrin--XML_ECOMM_ORDER_ID,FILE_OrderId", "NA", "3000");
								return_code_output = fwgui.fw_validate_text_in_xml_response("CancelOrder", "<ReturnCode>Success</ReturnCode>", "NO", 0);
								
								if (return_code_output.contains("0--"))
								{
									csg_icoms_cancel_status = csg_icoms_cancel_status + "CLEANED - CSG Cancel Order (" + orderid + ")---";
									log.fw_writeLogEntry("	CSG Order ID: " + orderid + " cancelled", "NA");
								}
								else
								{
									csg_icoms_cancel_status = csg_icoms_cancel_status + "FAILED - CSG Cancel Order (" + orderid + ") attempted but failed---";
									continue_flag = "no";
									
									String xml_endpoint = fwgui.fw_get_variable("XMLENDPOINT");
									String xml_request = fwgui.fw_get_variable("XMLREQUEST");
									String xml_response = fwgui.fw_get_variable("XMLRESPONSE");
									
									log.fw_writeLogEntry("", "NA");
									log.fw_writeLogEntry("ENDPOINT: " + xml_endpoint, "NA");
									log.fw_writeLogEntry("", "NA");
									log.fw_writeLogEntry("REQUEST: " + xml_request, "NA");
									log.fw_writeLogEntry("", "NA");
									log.fw_writeLogEntry("RESPONSE: " + xml_response, "NA");
									log.fw_writeLogEntry("", "NA");
								}
							}
							else
							{
								csg_icoms_cancel_status = csg_icoms_cancel_status + "SKIPPED - CSG Cancel Order no more Order IDs found";
							}
						}
						while(cntval < 10 && !csg_icoms_cancel_status.contains("SKIPPED") && continue_flag.equals("yes"));
						
						log.fw_writeLogEntry("	" + csg_icoms_cancel_status, "NA");
					}
					else
					{
						log.fw_writeLogEntry("SKIPPED - CSG Cancel Order billing address not found","1");
					}
				}
				else
				{
					log.fw_writeLogEntry(" ", "NA");
					log.fw_writeLogEntry(" ***** Stopping Execution because Address Line1 or Zip Code NOT found ****** ","1");
					log.fw_writeLogEntry(" ", "NA");
					System.exit(0);
				}
			}
			
			else if (Address_Type.contains("QAHA"))
			{
				//Logic for L-TWC
				String Address2_Value = "";
				String Address1_Value = fwgui.fw_get_variable("CurrentAddress");
				String ZipCode_Value = fwgui.fw_get_variable("ZipCode");
				String streetNum = "";
				String streetName = "";
				String xmlTemplate = "";
				String xmlReplaceValue = "";
				String addressLine2Text = "";
				String getAccountValue = "";
				String getDivisionId = "";
				String xmlFileName = "";
				String text_to_look_for = "";
				String temp_Address1_Value = "";
				
				if(Address_Type.contains("QAHA"))
				{
					fwgui.fw_event("", "", "SetVariable", "NA", "OrderStatusCode,O", "NA", "0");
				}
				else
				{
					fwgui.fw_event("", "", "SetVariable", "NA", "OrderStatusCode,OP", "NA", "0");
				}
				
				if (Address1_Value != null && ZipCode_Value != null)
				{
					if (Address1_Value.contains(","))
					{
						temp_Address1_Value = Address1_Value.split(",")[0];
						Address2_Value = Address1_Value.split(",")[1];
						fwgui.fw_event("", "", "SetVariable", "NA", "Address2," + Address2_Value, "NA", "0");
						
						String [] split_array = temp_Address1_Value.split(" ", 2);
						
						streetNum = split_array[0];
						streetName = split_array[1];
						
					}
					else
					{
						String [] split_array = Address1_Value.split(" ", 2);
						streetNum = split_array[0];
						streetName = split_array[1];
					}
					
					if(Address2_Value != "")
					{
						xmlTemplate = "APPXML_GetSPCAccountDivisionAPT_GetSPCAccountDivision";
						xmlFileName = "GetSPCAccountDivisionAPT";
						xmlReplaceValue = "XML_ECOMM_STREETNUMBER," + streetNum + "--XML_ECOMM_STREETNAME," + streetName + "--XML_ECOMM_APARTMENT,FILE_Address2--XML_ECOMM_ZIPCODE,FILE_ZipCode";
						addressLine2Text = "<addressLine2>" + Address2_Value;
						getAccountValue = "SEARCHFORWARD--<accountStatus>Active</accountStatus>,SEARCHBACKWARD(5)--" + addressLine2Text + ",SEARCHBACKWARD(9)--<accountNumber>&&ActiveAccountNumber";
						getDivisionId = "SEARCHFORWARD--<accountStatus>Active,SEARCHBACKWARD(6)--" + addressLine2Text + ",SEARCHBACKWARD(10)--<divisionID>&&DivisionID";
					}
					else
					{
						xmlTemplate = "APPXML_GetSPCAccountDivisionNoAPT_GetSPCAccountDivision";
						xmlFileName = "GetSPCAccountDivisionNoAPT";
						xmlReplaceValue = "XML_ECOMM_STREETNUMBER," + streetNum + "--XML_ECOMM_STREETNAME," + streetName + "--XML_ECOMM_ZIPCODE,FILE_ZipCode";
						getAccountValue = "SEARCHFORWARD--<accountStatus>Active</accountStatus>,SEARCHBACKWARD(11)--<accountNumber>&&ActiveAccountNumber";
						getDivisionId = "SEARCHFORWARD--<accountStatus>Active</accountStatus>,SEARCHBACKWARD(12)--<divisionID>&&DivisionID";
					}
					
					fwgui.fw_event("", "", "XMLExecute", xmlTemplate, xmlReplaceValue, "NA", "1000");
					text_to_look_for = "<accountStatus>Active</accountStatus>";
					return_code_output = fwgui.fw_validate_text_in_xml_response(xmlFileName, text_to_look_for, "NO", 0);
					if (return_code_output.contains("0--"))
					{
						fwgui.fw_event("", "", "XMLGetValueByMultipleTagnames", xmlTemplate, getAccountValue, "NA", "100");
						fwgui.fw_event("", "", "XMLGetValueByMultipleTagnames", xmlTemplate, getDivisionId, "NA", "100");
						
						String ActiveAccountNumber = fwgui.fw_get_variable("ActiveAccountNumber");
						
						log.fw_writeLogEntry("  ActiveAccountNumber: " + ActiveAccountNumber, "NA");
						
						String DivisionID = fwgui.fw_get_variable("DivisionID");
						log.fw_writeLogEntry("  DivisionID: " + DivisionID, "NA");
						
						fwgui.fw_event("", "", "SetVariable", "NA", "CancelCode,ZA", "NA", "0");
						
						int cntval = 0;
						String continue_flag = "yes";
						
						if(!ActiveAccountNumber.trim().isEmpty())
						{
							do
							{
								cntval = cntval + 1;
								
								fwgui.fw_event("", "", "XMLExecute", "APPXML_SPCGetOrderList", "XML_ECOMM_DIV_ID,FILE_DivisionID--XML_ECOMM_ACCT_NUM,FILE_ActiveAccountNumber--XML_ECOMM_ORD_STATUS_CD,FILE_OrderStatusCode", "NA", "1000");
								
								text_to_look_for = "<enterpriseCode>OPEN</enterpriseCode>";
								return_code_output = fwgui.fw_validate_text_in_xml_response("SPCGetOrderList", text_to_look_for, "NO", 0);
								if (return_code_output.contains("0--"))
								{
									fwgui.fw_event("", "", "XMLGetValueByMultipleTagnames", "APPXML_SPCGetOrderList", "SEARCHFORWARD--orderId&&orderId", "NA", "0");
			
									String orderid = fwgui.fw_get_variable("orderId");
									log.fw_writeLogEntry("  Order ID: " + orderid, "NA");
									
									if(!orderid.isEmpty())
									{
										fwgui.fw_event("", "", "SetVariable", "NA", "LegacyDivisionID,NYC.8150", "NA", "0");
										
										fwgui.fw_event("", "", "XMLExecute", "WEBSERVICE_SPCCancelOrder", "XML_ECOMM_DIV_ID,FILE_LegacyDivisionID--XML_ECOMM_ACCT_NUM,FILE_ActiveAccountNumber--XML_ECOMM_ORDER_NUM,FILE_orderId--XML_ECOMM_CANCEL_CODE,FILE_CancelCode", "NA", "1000");
										text_to_look_for = "<ns6:BillingOrderStatus>X</ns6:BillingOrderStatus>";
										return_code_output = fwgui.fw_validate_text_in_xml_response("SPCCancelOrder", text_to_look_for, "NO", 0);
										
										if (return_code_output.contains("0--"))
										{
											csg_icoms_cancel_status = csg_icoms_cancel_status + "CLEANED - CSG Cancel Order (" + orderid + ")---";
											log.fw_writeLogEntry("CSG Order ID: " + orderid + " cancelled", "NA");
										}
										else
										{
											csg_icoms_cancel_status = csg_icoms_cancel_status + "FAILED - CSG Cancel Order (" + orderid + ") attempted but failed---";
											continue_flag = "no";
											
											String xml_endpoint = fwgui.fw_get_variable("XMLENDPOINT");
											String xml_request = fwgui.fw_get_variable("XMLREQUEST");
											String xml_response = fwgui.fw_get_variable("XMLRESPONSE");
											
											log.fw_writeLogEntry("", "NA");
											log.fw_writeLogEntry("ENDPOINT: " + xml_endpoint, "NA");
											log.fw_writeLogEntry("", "NA");
											log.fw_writeLogEntry("REQUEST: " + xml_request, "NA");
											log.fw_writeLogEntry("", "NA");
											log.fw_writeLogEntry("RESPONSE: " + xml_response, "NA");
											log.fw_writeLogEntry("", "NA");
										}
									}
									else
									{
										csg_icoms_cancel_status = "SKIPPED - ICOMS Cancel Order no more Order IDs found";
										log.fw_writeLogEntry("  Order ID is NULL or EMPTY", "NA");
									}
								}
								else
								{
									csg_icoms_cancel_status = csg_icoms_cancel_status + "SKIPPED - CSG Cancel Order no more Order IDs found";
								}						
							}
							while(cntval < 10 && !csg_icoms_cancel_status.contains("SKIPPED") && continue_flag.equals("yes"));
							
							log.fw_writeLogEntry(csg_icoms_cancel_status, "NA");
						}
						else
						{
							csg_icoms_cancel_status = "SKIPPED - No Active accounts found at " + Address1_Value;
							log.fw_writeLogEntry("  Account Search criteria: " + getAccountValue, "NA");
						}
						
						if(csg_icoms_cancel_status.contains("FAILED"))
						{
							log.fw_writeLogEntry("  " + csg_icoms_cancel_status, "1");
						}
						else
						{
							log.fw_writeLogEntry("  " + csg_icoms_cancel_status, "NA");
						}
					}
					else
					{
						//log.fw_writeLogEntry("SKIPPED - CSG Cancel Order billing address not found","1");
						
						String xml_endpoint = fwgui.fw_get_variable("XMLENDPOINT");
						String xml_request = fwgui.fw_get_variable("XMLREQUEST");
						String xml_response = fwgui.fw_get_variable("XMLRESPONSE");
						
						log.fw_writeLogEntry("", "NA");
						log.fw_writeLogEntry("ENDPOINT: " + xml_endpoint, "NA");
						log.fw_writeLogEntry("", "NA");
						log.fw_writeLogEntry("REQUEST: " + xml_request, "NA");
						log.fw_writeLogEntry("", "NA");
						log.fw_writeLogEntry("RESPONSE: " + xml_response, "NA");
						log.fw_writeLogEntry("", "NA");
						
						if(Address2_Value != "")
						{
							text_to_look_for = "<addressLine1>" + temp_Address1_Value + "</addressLine1>";
						}
						else
						{
							text_to_look_for = "<addressLine1>" + Address1_Value + "</addressLine1>";
						}
						
						return_code_output = fwgui.fw_validate_text_in_xml_response(xmlFileName, text_to_look_for, "NO", 0);
						
						if(return_code_output.contains("0--"))
						{
							log.fw_writeLogEntry("SKIPPED - ICOMS Cancel Order - No Active account found", "0");
						}
						else
						{
							log.fw_writeLogEntry("SKIPPED - ICOMS Cancel Order billing address not found","1");
						}
					}					
				}
				else
				{
					log.fw_writeLogEntry(" ", "NA");
					log.fw_writeLogEntry(" ***** Stopping Execution because Address Line1 or Zip Code NOT found ****** ","1");
					log.fw_writeLogEntry(" ", "NA");
					System.exit(0);
				}
			}
			
			else if(Address_Type.contains("BHNU"))
			{
				//Logic for L-BHN
				String Address2_Value = "";
				String Address1_Value = fwgui.fw_get_variable("CurrentAddress");
				String ZipCode_Value = fwgui.fw_get_variable("ZipCode");
				String streetNum = "";
				String streetName = "";
				String xmlTemplate = "";
				String xmlReplaceValue = "";
				String addressLine2Text = "";
				String getAccountValue = "";
				String getDivisionId = "";
				String xmlFileName = "";
				String text_to_look_for = "";
				String temp_Address1_Value = "";
				
				if (Address1_Value != null && ZipCode_Value != null)
				{
					if (Address1_Value.contains(","))
					{
						temp_Address1_Value = Address1_Value.split(",")[0];
						Address2_Value = Address1_Value.split(",")[1];
						fwgui.fw_event("", "", "SetVariable", "NA", "Address2," + Address2_Value, "NA", "0");
						
						String [] split_array = temp_Address1_Value.split(" ", 2);
						streetNum = split_array[0];
						streetName = split_array[1];
					}
					else
					{
						String [] split_array = Address1_Value.split(" ", 2);
						streetNum = split_array[0];
						streetName = split_array[1];
					}
					
					if(Address2_Value != "")
					{
						xmlTemplate = "APPXML_GetSPCAccountDivisionAPT_GetSPCAccountDivision";
						xmlFileName = "GetSPCAccountDivisionAPT";
						xmlReplaceValue = "XML_ECOMM_STREETNUMBER," + streetNum + "--XML_ECOMM_STREETNAME," + streetName + "--XML_ECOMM_APARTMENT,FILE_Address2--XML_ECOMM_ZIPCODE,FILE_ZipCode";
						addressLine2Text = "<addressLine2>" + Address2_Value;
						getAccountValue = "SEARCHFORWARD--<accountStatus>Active,SEARCHBACKWARD(6)--" + addressLine2Text + ",SEARCHBACKWARD(9)--<accountNumber>&&ActiveAccountNumber";
						getDivisionId = "SEARCHFORWARD--<accountStatus>Active,SEARCHBACKWARD(6)--" + addressLine2Text + ",SEARCHBACKWARD(10)--<divisionID>&&DivisionID";
					}
					else
					{
						xmlTemplate = "APPXML_GetSPCAccountDivisionNoAPT_GetSPCAccountDivision";
						xmlFileName = "GetSPCAccountDivisionNoAPT";
						xmlReplaceValue = "XML_ECOMM_STREETNUMBER," + streetNum + "--XML_ECOMM_STREETNAME," + streetName + "--XML_ECOMM_ZIPCODE,FILE_ZipCode";
						getAccountValue = "SEARCHFORWARD--<accountStatus>Active</accountStatus>,SEARCHBACKWARD(11)--<accountNumber>&&ActiveAccountNumber";
						getDivisionId = "SEARCHFORWARD--<accountStatus>Active</accountStatus>,SEARCHBACKWARD(12)--<divisionID>&&DivisionID";
					}
					
					fwgui.fw_event("", "", "XMLExecute", xmlTemplate, xmlReplaceValue, "NA", "500");
					
					text_to_look_for = "<accountStatus>Active</accountStatus>";
					return_code_output = fwgui.fw_validate_text_in_xml_response(xmlFileName, text_to_look_for, "NO", 0);
					if (return_code_output.contains("0--"))
					{
						fwgui.fw_event("", "", "XMLGetValueByMultipleTagnames", xmlTemplate, getAccountValue, "NA", "100");
						fwgui.fw_event("", "", "XMLGetValueByMultipleTagnames", xmlTemplate, getDivisionId, "NA", "100");
						
						String ActiveAccountNumber = fwgui.fw_get_variable("ActiveAccountNumber");
						log.fw_writeLogEntry("  ActiveAccountNumber: " + ActiveAccountNumber, "NA");
						
						String DivsionID = fwgui.fw_get_variable("DivisionID");
						log.fw_writeLogEntry("  DivisionID: " + DivsionID, "NA");
						
						fwgui.fw_event("", "", "SetVariable", "NA", "OrderStatusCode,OP", "NA", "0");
						fwgui.fw_event("", "", "SetVariable", "NA", "CancelCode,Y0", "NA", "0");
						
						int cntval = 0;
						String continue_flag = "yes";
						
						if(!ActiveAccountNumber.trim().isEmpty())
						{
							String house_num = ActiveAccountNumber.substring(0, 7);
							fwgui.fw_event("", "", "SetVariable", "NA", "ICOMSHouseNumber," + house_num, "NA", "0");
							
							String occupant_cd = ActiveAccountNumber.substring(7);
							fwgui.fw_event("", "", "SetVariable", "NA", "ICOMSOccupantCode," + occupant_cd, "NA", "0");
							
							do
							{
								cntval = cntval + 1;
								
								fwgui.fw_event("", "", "XMLExecute", "APPXML_SPCGetOrderList", "XML_ECOMM_DIV_ID,FILE_DivisionID--XML_ECOMM_ACCT_NUM,FILE_ActiveAccountNumber--XML_ECOMM_ORD_STATUS_CD,FILE_OrderStatusCode", "NA", "1000");
								
								//text_to_look_for = "<billerCode>OP</billerCode>";
								text_to_look_for = "<enterpriseCode>OPEN</enterpriseCode>";
								return_code_output = fwgui.fw_validate_text_in_xml_response("SPCGetOrderList", text_to_look_for, "NO", 0);
								if (return_code_output.contains("0--"))
								{
									//fwgui.fw_event("", "", "XMLGetValueByMultipleTagnames", "APPXML_SPCGetOrderList", "SEARCHFORWARD--<billerCode>OP,SEARCHBACKWARD(11)--<orderId>&&OrderId", "NA", "0");
									fwgui.fw_event("", "", "XMLGetValueByMultipleTagnames", "APPXML_SPCGetOrderList", "SEARCHFORWARD--<enterpriseCode>OPEN,SEARCHBACKWARD(12)--<orderId>&&OrderId", "NA", "0");
			
									String orderid = fwgui.fw_get_variable("OrderId");
									log.fw_writeLogEntry("  Order ID: " + orderid, "NA");
									
									if(!orderid.isEmpty())
									{
										fwgui.fw_event("", "", "SetVariable", "NA", "LegacyDivisionID,BHNDLINCOLN.003", "NA", "0");
										
										//fwgui.fw_event("", "", "XMLExecute", "BPSWEBSERVICE_ICAPICancelOrder", "XML_ECOMM_USER_ID,FILE_WSUSERID--XML_ECOMM_PASSWORD,FILE_WSPASSID--XML_ECOMM_DIV_ID,FILE_DivisionID--XML_ECOMM_HOUSE_NUM,FILE_ICOMSHouseNumber--XML_ECOMM_ORDER_NUM,FILE_OrderId--XML_ECOMM_OCCUP_CD,FILE_ICOMSOccupantCode--XML_ECOMM_CANCEL_CODE,FILE_CancelCode", "NA", "1000");
										
										fwgui.fw_event("", "", "XMLExecute", "BPSWEBSERVICE_ICAPICancelOrder", "XML_ECOMM_DIV_ID,FILE_LegacyDivisionID--XML_ECOMM_HOUSE_NUM,FILE_ICOMSHouseNumber--XML_ECOMM_ORDER_NUM,FILE_OrderId--XML_ECOMM_OCCUP_CD,FILE_ICOMSOccupantCode--XML_ECOMM_CANCEL_CODE,FILE_CancelCode", "NA", "1000");
										text_to_look_for = "<cancel_1Output";
										return_code_output = fwgui.fw_validate_text_in_xml_response("ICAPICancelOrder", text_to_look_for, "NO", 0);
										
										if (return_code_output.contains("0--"))
										{
											csg_icoms_cancel_status = csg_icoms_cancel_status + "CLEANED - ICOMS Cancel Order (" + orderid + ")---";
											log.fw_writeLogEntry("ICOMS Order ID: " + orderid + " cancelled", "NA");
										}
										else
										{
											csg_icoms_cancel_status = csg_icoms_cancel_status + "FAILED - ICOMS Cancel Order (" + orderid + ") attempted but failed---";
											continue_flag = "no";
											
											String xml_endpoint = fwgui.fw_get_variable("XMLENDPOINT");
											String xml_request = fwgui.fw_get_variable("XMLREQUEST");
											String xml_response = fwgui.fw_get_variable("XMLRESPONSE");
											
											log.fw_writeLogEntry("", "NA");
											log.fw_writeLogEntry("ENDPOINT: " + xml_endpoint, "NA");
											log.fw_writeLogEntry("", "NA");
											log.fw_writeLogEntry("REQUEST: " + xml_request, "NA");
											log.fw_writeLogEntry("", "NA");
											log.fw_writeLogEntry("RESPONSE: " + xml_response, "NA");
											log.fw_writeLogEntry("", "NA");
										}
									}
									else
									{
										csg_icoms_cancel_status = "SKIPPED - ICOMS Cancel Order no more Order IDs found";
										log.fw_writeLogEntry("  Order ID is NULL or EMPTY", "NA");
									}
								}
								else
								{
									csg_icoms_cancel_status = csg_icoms_cancel_status + "SKIPPED - ICOMS Cancel Order no more Order IDs found";
								}						
							}
							while(cntval < 10 && !csg_icoms_cancel_status.contains("SKIPPED") && continue_flag.equals("yes"));
						}
						else
						{
							csg_icoms_cancel_status = "SKIPPED - No Active accounts found at " + Address1_Value;
							log.fw_writeLogEntry("  Account Search criteria: " + getAccountValue, "NA");
						}
						
						if(csg_icoms_cancel_status.contains("FAILED"))
						{
							log.fw_writeLogEntry("  " + csg_icoms_cancel_status, "1");
						}
						else
						{
							log.fw_writeLogEntry("  " + csg_icoms_cancel_status, "NA");
						}
					}
					else
					{
						
						String xml_endpoint = fwgui.fw_get_variable("XMLENDPOINT");
						String xml_request = fwgui.fw_get_variable("XMLREQUEST");
						String xml_response = fwgui.fw_get_variable("XMLRESPONSE");
						
						log.fw_writeLogEntry("", "NA");
						log.fw_writeLogEntry("ENDPOINT: " + xml_endpoint, "NA");
						log.fw_writeLogEntry("", "NA");
						log.fw_writeLogEntry("REQUEST: " + xml_request, "NA");
						log.fw_writeLogEntry("", "NA");
						log.fw_writeLogEntry("RESPONSE: " + xml_response, "NA");
						log.fw_writeLogEntry("", "NA");
						
						if(Address2_Value != "")
						{
							text_to_look_for = "<addressLine1>" + temp_Address1_Value + "</addressLine1>";
						}
						else
						{
							text_to_look_for = "<addressLine1>" + Address1_Value + "</addressLine1>";
						}
						
						return_code_output = fwgui.fw_validate_text_in_xml_response(xmlFileName, text_to_look_for, "NO", 0);
						
						if(return_code_output.contains("0--"))
						{
							log.fw_writeLogEntry("SKIPPED - ICOMS Cancel Order - No Active account found", "0");
						}
						else
						{
							log.fw_writeLogEntry("SKIPPED - ICOMS Cancel Order billing address not found","1");
						}
					}					
				}
				else
				{
					log.fw_writeLogEntry(" ", "NA");
					log.fw_writeLogEntry(" ***** Stopping Execution because Address Line1 or Zip Code NOT found ****** ","1");
					log.fw_writeLogEntry(" ", "NA");
					System.exit(0);
				}
			}
			
			else if(Address_Type.contains("ESTAADT") || Address_Type.contains("BHND"))
			{
				//Logic for L-TWC ICOMS
				String Address2_Value = "";
				String Address1_Value = fwgui.fw_get_variable("CurrentAddress");
				String ZipCode_Value = fwgui.fw_get_variable("ZipCode");
				String streetNum = "";
				String streetName = "";
				String xmlTemplate = "";
				String xmlReplaceValue = "";
				String addressLine2Text = "";
				String getHouseNumber = "";
				String getOccupantCode = "";
				String getAccountStatus = "";
				String xmlFileName = "";
				String text_to_look_for = "";
				String temp_Address1_Value = "";
				
				if (Address1_Value != null && ZipCode_Value != null)
				{
					if (Address1_Value.contains(","))
					{
						temp_Address1_Value = Address1_Value.split(",")[0];
						Address2_Value = Address1_Value.split(",")[1];
						fwgui.fw_event("", "", "SetVariable", "NA", "Address2," + Address2_Value, "NA", "0");
						
						String [] split_array = temp_Address1_Value.split(" ", 2);
						streetNum = split_array[0];
						streetName = split_array[1];
					}
					else
					{
						String [] split_array = Address1_Value.split(" ", 2);
						streetNum = split_array[0];
						streetName = split_array[1];
					}
					
					xmlTemplate = "BPSWEBSERVICE_ICAPISearchAddress";
					xmlFileName = "ICAPISearchAddress";
					xmlReplaceValue = "XML_ECOMM_STREETNUMBER," + streetNum + "--XML_ECOMM_STREETNAME," + streetName + "--XML_ECOMM_ZIPCODE,FILE_ZipCode--XML_ECOMM_DIVISION_ID,FILE_AddressType";
					
					if(Address2_Value != "")
					{
						addressLine2Text = "<ns1:apartmentNumber>" + Address2_Value;
						getAccountStatus = "SEARCHFORWARD--" + addressLine2Text + ",SEARCHFORWARD(15)--<ns1:status>&&AccountStatus";
						getHouseNumber = "SEARCHFORWARD--" + addressLine2Text + ",SEARCHFORWARD(12)--:houseNumber xmlns:&&ICOMSHouseNumber";
						getOccupantCode = "SEARCHFORWARD--" + addressLine2Text + ",SEARCHFORWARD(13)--:occupantCode xmlns:&&ICOMSOccupantCode";
					}
					else
					{
						getAccountStatus = "SEARCHFORWARD--<ns1:status>&&AccountStatus";
						getHouseNumber = "SEARCHFORWARD--<ns3:houseNumber&&ICOMSHouseNumber";
						getOccupantCode = "SEARCHFORWARD--<ns4:occupantCode&&ICOMSOccupantCode";
					}
					
					fwgui.fw_event("", "", "XMLExecute", xmlTemplate, xmlReplaceValue, "NA", "500");
					
					text_to_look_for = "<ns1:streetName>" + streetName + "</ns1:streetName>";
					return_code_output = fwgui.fw_validate_text_in_xml_response(xmlFileName, text_to_look_for, "NO", 0);
					if (return_code_output.contains("0--"))
					{
						fwgui.fw_event("", "", "XMLGetValueByMultipleTagnames", xmlTemplate, getAccountStatus, "NA", "100");
						fwgui.fw_event("", "", "XMLGetValueByMultipleTagnames", xmlTemplate, getHouseNumber, "NA", "100");
						fwgui.fw_event("", "", "XMLGetValueByMultipleTagnames", xmlTemplate, getOccupantCode, "NA", "100");
						
						String AccountStatus = fwgui.fw_get_variable("AccountStatus");
						log.fw_writeLogEntry("  AccountStatus: " + AccountStatus, "NA");
						
						String ICOMSHouseNumber = fwgui.fw_get_variable("ICOMSHouseNumber");
						log.fw_writeLogEntry("  ICOMSHouseNumber: " + ICOMSHouseNumber, "NA");
						
						String ICOMSOccupantCode = fwgui.fw_get_variable("ICOMSOccupantCode");
						log.fw_writeLogEntry("  ICOMSOccupantCode: " + ICOMSOccupantCode, "NA");
						
						if(Address_Type.contains("ESTAADT"))
						{
							fwgui.fw_event("", "", "SetVariable", "NA", "CancelCode,CU", "NA", "0");
						}
						else
						{
							fwgui.fw_event("", "", "SetVariable", "NA", "CancelCode,Y0", "NA", "0");
						}
						
						int cntval = 0;
						String continue_flag = "yes";
						
						if(!AccountStatus.trim().isEmpty() && !ICOMSOccupantCode.trim().equalsIgnoreCase("00"))
						{
							
							xmlTemplate = "BPSWEBSERVICE_ICAPISearchOrder_ICAPICancelOrder";
							xmlFileName = "ICAPISearchOrder";
							xmlReplaceValue = "XML_ECOMM_DIVISION_ID,FILE_AddressType--XML_ECOMM_HOUSE_NUM,FILE_ICOMSHouseNumber--XML_ECOMM_OCCUP_CD,FILE_ICOMSOccupantCode";
							
							do
							{
								cntval = cntval + 1;
								
								fwgui.fw_event("", "", "XMLExecute", xmlTemplate, xmlReplaceValue, "NA", "500");
								
								text_to_look_for = "<ns1:orderNumber>";
								return_code_output = fwgui.fw_validate_text_in_xml_response(xmlFileName, text_to_look_for, "NO", 0);
								if (return_code_output.contains("0--"))
								{
									fwgui.fw_event("", "", "XMLGetValueByMultipleTagnames", xmlTemplate, "SEARCHFORWARD--<ns1:status>8</,SEARCHBACKWARD(17)--<ns1:orderNumber>&&OrderId", "NA", "0");
			
									String orderid = fwgui.fw_get_variable("OrderId");
									log.fw_writeLogEntry("  Order ID: " + orderid, "NA");
									
									if(!orderid.isEmpty())
									{
										fwgui.fw_event("", "", "SetVariable", "NA", "LegacyDivisionID,FILE_AddressType", "NA", "0");
										
										fwgui.fw_event("", "", "XMLExecute", "BPSWEBSERVICE_ICAPICancelOrder", "XML_ECOMM_DIV_ID,FILE_LegacyDivisionID--XML_ECOMM_HOUSE_NUM,FILE_ICOMSHouseNumber--XML_ECOMM_ORDER_NUM,FILE_OrderId--XML_ECOMM_OCCUP_CD,FILE_ICOMSOccupantCode--XML_ECOMM_CANCEL_CODE,FILE_CancelCode", "NA", "1000");
										text_to_look_for = "<cancel_1Output";
										return_code_output = fwgui.fw_validate_text_in_xml_response("ICAPICancelOrder", text_to_look_for, "NO", 0);
										
										if (return_code_output.contains("0--"))
										{
											csg_icoms_cancel_status = csg_icoms_cancel_status + "CLEANED - ICOMS Cancel Order (" + orderid + ")---";
											log.fw_writeLogEntry("ICOMS Order ID: " + orderid + " cancelled", "NA");
										}
										else
										{
											csg_icoms_cancel_status = csg_icoms_cancel_status + "FAILED - ICOMS Cancel Order (" + orderid + ") attempted but failed---";
											continue_flag = "no";
											
											String xml_endpoint = fwgui.fw_get_variable("XMLENDPOINT");
											String xml_request = fwgui.fw_get_variable("XMLREQUEST");
											String xml_response = fwgui.fw_get_variable("XMLRESPONSE");
											
											log.fw_writeLogEntry("", "NA");
											log.fw_writeLogEntry("ENDPOINT: " + xml_endpoint, "NA");
											log.fw_writeLogEntry("", "NA");
											log.fw_writeLogEntry("REQUEST: " + xml_request, "NA");
											log.fw_writeLogEntry("", "NA");
											log.fw_writeLogEntry("RESPONSE: " + xml_response, "NA");
											log.fw_writeLogEntry("", "NA");
										}
									}
									else
									{
										csg_icoms_cancel_status = csg_icoms_cancel_status + "SKIPPED - ICOMS Cancel Order no more Order IDs found";
										log.fw_writeLogEntry("  Order ID is NULL or EMPTY", "NA");
									}
								}
								else
								{
									csg_icoms_cancel_status = csg_icoms_cancel_status + "SKIPPED - ICOMS Cancel Order no more Order IDs found";
								}						
							}
							while(cntval < 10 && !csg_icoms_cancel_status.contains("SKIPPED") && continue_flag.equals("yes"));
						}
						else
						{
							csg_icoms_cancel_status = "SKIPPED - No Active accounts found at " + Address1_Value;
							log.fw_writeLogEntry("  Account Search criteria: " + getHouseNumber, "NA");
						}
						
						if(csg_icoms_cancel_status.contains("FAILED"))
						{
							log.fw_writeLogEntry("  " + csg_icoms_cancel_status, "1");
						}
						else
						{
							log.fw_writeLogEntry("  " + csg_icoms_cancel_status, "NA");
						}
					}
					else
					{
						
						String xml_endpoint = fwgui.fw_get_variable("XMLENDPOINT");
						String xml_request = fwgui.fw_get_variable("XMLREQUEST");
						String xml_response = fwgui.fw_get_variable("XMLRESPONSE");
						
						log.fw_writeLogEntry("", "NA");
						log.fw_writeLogEntry("ENDPOINT: " + xml_endpoint, "NA");
						log.fw_writeLogEntry("", "NA");
						log.fw_writeLogEntry("REQUEST: " + xml_request, "NA");
						log.fw_writeLogEntry("", "NA");
						log.fw_writeLogEntry("RESPONSE: " + xml_response, "NA");
						log.fw_writeLogEntry("", "NA");
						
						if(Address2_Value != "")
						{
							text_to_look_for = "<addressLine1>" + temp_Address1_Value + "</addressLine1>";
						}
						else
						{
							text_to_look_for = "<addressLine1>" + Address1_Value + "</addressLine1>";
						}
						
						return_code_output = fwgui.fw_validate_text_in_xml_response(xmlFileName, text_to_look_for, "NO", 0);
						
						if(return_code_output.contains("0--"))
						{
							log.fw_writeLogEntry("SKIPPED - ICOMS Cancel Order - No Active account found", "0");
						}
						else
						{
							log.fw_writeLogEntry("SKIPPED - ICOMS Cancel Order billing address not found","1");
						}
					}					
				}
				else
				{
					log.fw_writeLogEntry(" ", "NA");
					log.fw_writeLogEntry(" ***** Stopping Execution because Address Line1 or Zip Code NOT found ****** ","1");
					log.fw_writeLogEntry(" ", "NA");
					System.exit(0);
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception !!!");
			e.printStackTrace();
		}
	}
}