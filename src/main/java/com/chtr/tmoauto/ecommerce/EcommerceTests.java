package com.chtr.tmoauto.ecommerce;

import com.chtr.tmoauto.logging.Logging;
import com.chtr.tmoauto.util.CancelCSGOrder;
import com.chtr.tmoauto.webui.GUI;

public class EcommerceTests {

	static EcommerceFunctions ecomfunc = new EcommerceFunctions();
	static Logging log = new Logging();
	static GUI fwgui = new GUI();
	static CancelCSGOrder fwws = new CancelCSGOrder(log);

	public static String alm_test_id = "";
	public static String address_value = "";
	public static String zip_value = "";
	
	/**
	 * This function is main entry into the execution of all ECOMMERCE test cases.
	 * 
	 * @param args
	 * @author Dhaval, Irfan & Shadab
	 * @throws Exception
	 * @since 02/09/2017
	 */

	public static void main(String[] args) throws Exception 
	{	
		String exit_on_fail_flag = "yes";
		String appname = "ECOMMERCE";
        String username = fwgui.fw_get_user_name(appname);
        String test_cases_to_execute_list = fwgui.fw_get_test_execparms(appname, username);
        fwgui.fw_get_environment_to_execute_tests("");
		
		String[] test_case_to_execute_list_arr = test_cases_to_execute_list.split(",");
		
		for (int x = 0; x < test_case_to_execute_list_arr.length; x++) 
		{
			
			String worksheet_name = fwgui.fw_get_worksheet_list(test_case_to_execute_list_arr[x]);
            alm_test_id = test_case_to_execute_list_arr[x].substring(2);
			
			log.fw_create_output_log_file(alm_test_id, "");

			String tc_eventname = "";
			String tc_objectname = "";
			String tc_testdata = "";
			String millisecondstowaitafterobjectevent = "";
			String objecttolookforafterobjectevent = "";

			String tc_eventname_comp = "";
			String tc_objectname_comp = "";
			String tc_testdata_comp = "";
			String objecttolookforafterobjectevent_comp = "";
			String millisecondstowaitafterobjectevent_comp = "";
			
			fwgui.fw_get_test_case(appname, worksheet_name);
            int Row = Integer.parseInt(fwgui.fw_get_variable("total_test_case_steps"));
            fwgui.fw_get_test_object(appname);
            
			for (int y = 1; y < Row + 1; y++) 
			{
				
                tc_eventname = fwgui.fw_get_test_step("out_event_name", y);
                tc_objectname = fwgui.fw_get_test_step("out_object_name", y);
                tc_testdata = fwgui.fw_get_test_step("out_testdata_val", y);
                objecttolookforafterobjectevent = fwgui.fw_get_test_step("out_objecttolookfor_val", y);
                millisecondstowaitafterobjectevent = fwgui.fw_get_test_step("out_waittime_val", y);

				if (tc_objectname.equals("NA_GetAddress")) 
				{
					
					String[] get_address_array = tc_testdata.split(",");
					String tc_testdata_value = get_address_array[1];
					
					fwgui.fw_event("", "", "SetVariable", "NA_GetAddress", tc_testdata, objecttolookforafterobjectevent, millisecondstowaitafterobjectevent);
					address_value = tc_testdata_value;
					
				}
				
				else if (tc_objectname.equals("NA_ZipCode")) 
				{
					
					String[] get_zipcode_array = tc_testdata.split(",");
					String tc_testdata_value = get_zipcode_array[1];
					
					fwgui.fw_event("", "", "SetVariable", "NA_ZipCode", tc_testdata, objecttolookforafterobjectevent, millisecondstowaitafterobjectevent);
					zip_value = tc_testdata_value;
					
					fwgui.fw_event("", "", "SetVariable", "NA", "GUIAccountNumber,NULL", objecttolookforafterobjectevent, millisecondstowaitafterobjectevent);
					fwgui.fw_event("", "", "SetVariable", "NA", "GUIConfirmationNumber,NULL", objecttolookforafterobjectevent, millisecondstowaitafterobjectevent);
					fwgui.fw_event("", "", "SetVariable", "NA", "GUIContactInfo,NULL", objecttolookforafterobjectevent, millisecondstowaitafterobjectevent);
					fwgui.fw_event("", "", "SetVariable", "NA", "GUITotalCharges,NULL", objecttolookforafterobjectevent, millisecondstowaitafterobjectevent);
					
					ecomfunc.Ecom_CancelCSGOrdersPerAddress();
					
					/*for (int z=1;z<2;z++)
					{
						fwws.fw_cancel_csg_orders_perAddress(address_value, zip_value, Integer.parseInt(alm_test_id));
					}*/
				}
				
				else if (tc_eventname.equals("Component"))
				{
					fwgui.fw_get_test_component_byname(appname,tc_testdata);
					int RowComponent = Integer.parseInt(fwgui.fw_get_variable("total_test_case_steps_comp"));
					
					for (int w = 1; w < RowComponent + 1; w++) 
					{
						tc_eventname_comp = fwgui.fw_get_test_step("out_event_name_comp", w);
                        tc_objectname_comp = fwgui.fw_get_test_step("out_object_name_comp", w);
                        tc_testdata_comp = fwgui.fw_get_test_step("out_testdata_val_comp", w);
                        objecttolookforafterobjectevent_comp = fwgui.fw_get_test_step("out_objecttolookfor_val_comp", w);
                        millisecondstowaitafterobjectevent_comp = fwgui.fw_get_test_step("out_waittime_val_comp", w);
						
                        if (tc_objectname_comp.contains("--"))
                        {
                        	String tc_comp_name = "";
    						String [] tc_objectname_comp_arr = tc_objectname_comp.split("--");
    						tc_comp_name = tc_objectname_comp_arr[0];
    						tc_objectname_comp = tc_objectname_comp_arr[1];
    						
    						if(tc_comp_name.equals(tc_testdata))
    						{
    							fwgui.fw_event("", "",
    									tc_eventname_comp, tc_objectname_comp,
    									tc_testdata_comp, objecttolookforafterobjectevent_comp,
    									millisecondstowaitafterobjectevent_comp);
    						}
    					}
						
						else if (tc_objectname_comp.contains("ClickOnObjectIfFound") && tc_objectname_comp.contains(tc_testdata + "_")) 
						{
							
							ecomfunc.Ecom_ClickOnObjectIfFound(tc_testdata_comp);
							Thread.sleep(Long.parseLong(millisecondstowaitafterobjectevent_comp));
							
						}
						
						else if (tc_objectname_comp.toUpperCase().contains("VALIDATEEMAIL") && tc_objectname_comp.contains(tc_testdata + "_")) 
						{
							
							ecomfunc.Ecom_validate_email("", worksheet_name, tc_testdata_comp);
							
						}
						
						else if (tc_objectname_comp.toUpperCase().contains("VALIDATECSG") && tc_objectname_comp.contains(tc_testdata + "_")) 
						{
							
							ecomfunc.Ecom_validate_getcsgservices("", "", GUI.out_object_extrainfo, 
									Integer.parseInt(alm_test_id),tc_testdata_comp);
							
						}
						
						else if (tc_objectname_comp.contains("GetTrackingID") && tc_objectname_comp.contains(tc_testdata + "_")) 
						{

							ecomfunc.TrackingId = ecomfunc.Ecom_getTrackingID(tc_testdata_comp);
							Thread.sleep(Long.parseLong(millisecondstowaitafterobjectevent_comp));

						}
						
						else if (tc_eventname_comp.contains("GetAttribute") && tc_objectname_comp.contains("CPUNCHECK") && tc_objectname_comp.contains(tc_testdata + "_")) 
						{
							String test_data_array[] = tc_testdata_comp.split("<>");
							String locator = test_data_array[0];
							String locatorValue = test_data_array[1];
							String attributeName = test_data_array[2];
							String identifier = test_data_array[3];
							
							String attribute_value = fwgui.fw_get_attribute_value(locator, locatorValue, attributeName, 0);
							if(attribute_value != null && attribute_value.equalsIgnoreCase("true"))
							{
								fwgui.fw_click_button(identifier, "INPUT", locator, locatorValue, "", Long.parseLong(millisecondstowaitafterobjectevent_comp));
								fwgui.fw_click_button("Save Preferences", "BUTTON", "xpath", "//button[text()='Save']", "", Long.parseLong(millisecondstowaitafterobjectevent_comp));
							}
							else
							{
								log.fw_writeLogEntry(identifier + " already unchecked", "0");
							}
						}
						
						else if (tc_objectname_comp.contains(tc_testdata + "_")) 
						{
							
							if (tc_objectname_comp.contains("WEBSERVICE_"))
							{
								
								String[] tc_objectname_comp_arr = tc_objectname_comp.split("_");
								
								int arr_length = tc_objectname_comp_arr.length;
								if (arr_length==4)
								{
									tc_objectname_comp = tc_objectname_comp_arr[1]+"_"+tc_objectname_comp_arr[2]+"_"+tc_objectname_comp_arr[3];
								}
								else
								{
									tc_objectname_comp = tc_objectname_comp_arr[1]+"_"+tc_objectname_comp_arr[2];
								}								
							}
							
							fwgui.fw_event("", "",
									tc_eventname_comp, tc_objectname_comp,
									tc_testdata_comp, objecttolookforafterobjectevent_comp,
									millisecondstowaitafterobjectevent_comp);
						}
                        
                        if(Logging.step_failed_cnt > 0 && exit_on_fail_flag.equalsIgnoreCase("yes"))
                        {
                        	log.fw_writeLogEntry("There is a FAILED step... breaking loop", "NA");
                        	fwgui.fw_event("", "", "UpdateEmail", "NA", "", "NA", "0");
                        	break;
                        }
					}
				}
				else 
				{
					
					fwgui.fw_event("", "", tc_eventname, 
							tc_objectname, tc_testdata, objecttolookforafterobjectevent,
							millisecondstowaitafterobjectevent);
				}
				
				 if(Logging.step_failed_cnt > 0 && exit_on_fail_flag.equalsIgnoreCase("yes"))
                 {
					 log.fw_writeLogEntry("There is a FAILED step... breaking loop", "NA");
					 fwgui.fw_event("", "", "UpdateEmail", "NA", "", "NA", "0");
					 break;
                 }
			}

			//fwws.fw_cancel_csg_orders_perAddress(address_value, zip_value, Integer.parseInt(alm_test_id));
			ecomfunc.Ecom_CancelCSGOrdersPerAddress();
			GUI.out_object_extrainfo = "";
			
			log.fw_closedown_test();

		}
		
	}
	
}