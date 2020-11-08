package com.blz.addressbookjdbc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.blz.addressbookjdbc.AddressBookService.IOService;
import com.bridgelabz.payrollrestapi.EmployeePayrollData;
import com.bridgelabz.payrollrestapi.EmployeePayrollService;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AddressBookRestApiTest {
	private static Logger log = Logger.getLogger(AddressBookRestApiTest.class.getName());

	@Before
	public void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	public Contact[] getContactList() {
		Response response = RestAssured.get("/AddressBook");
		log.info("Contact entries in JSON Server :\n" + response.asString());
		Contact[] arrayOfContacts = new Gson().fromJson(response.asString(), Contact[].class);
		return arrayOfContacts;
	}

	public Response addContactToJsonServer(Contact contactData) {
		String contactJson = new Gson().toJson(contactData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(contactJson);
		return request.post("/AddressBook");
	}

	@Test
	public void givenNewContact_WhenAdded__ShouldMatch() {
		AddressBookService addressBookService;
		Contact[] arrayOfContacts = getContactList();
		addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
		Contact contactData = null;
		contactData = new Contact("Manish", "Pandey", "Sainikpuri", "Patna", "Bihar", 700012, 99084874,
				"manish@gmail.com", "Casual", "Friends", LocalDate.now());
		Response response = addContactToJsonServer(contactData);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(201, statusCode);

		contactData = new Gson().fromJson(response.asString(), Contact.class);
		addressBookService.addContactToJSONServer(contactData, IOService.REST_IO);
		long entries = addressBookService.countEntries(IOService.REST_IO);
		Assert.assertEquals(3, entries);
	}
	
	@Test
	public void givenListOfNewContacts_WhenAdded__ShouldMatchCount() {
		AddressBookService addressBookService;
		Contact[] arrayOfContacts = getContactList(); // population the employeePayroll List
		addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
		Contact[] arrayOfContactsList = {
				new Contact("Kuldeep", "Yadav", "Bandra", "Kolkata", "West Bengal", 700012, 99084874,
						"kuldeep@gmail.com", "Casual", "Friends", LocalDate.now()),
				new Contact("Chris", "Gayle", "Ladak", "Chandigarh", "Punjab", 700012, 99084874,
						"gayle@gmail.com", "Casual", "Friends", LocalDate.now()) };
		// Recursively calling each added contact and checking the statusCode
		for (Contact contactData : arrayOfContactsList) {
			Response response = addContactToJsonServer(contactData);
			int statusCode = response.getStatusCode();
			Assert.assertEquals(201, statusCode);
			// converting the added ones into objects from the json file
			contactData = new Gson().fromJson(response.asString(), Contact.class);
			// adding objects into the employee payroll
			addressBookService.addContactToAddressBook(contactData, IOService.REST_IO);
		}
		long entries = addressBookService.countEntries(IOService.REST_IO);
		Assert.assertEquals(4, entries);
	}
}
