package com.blz.addressbookjdbc;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import com.blz.addressbookjdbc.AddressBookService.IOService;

public class AddressBookSystemTest {
	private static Logger log = Logger.getLogger(AddressBookService.class.getName());

	@Test
	public void contactsWhenRetrievedFromDB_ShouldMatchCount() {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> contactList = addressBookService.readContactData();
		Assert.assertEquals(3, contactList.size());
	}

	@Test
	public void contactsWhenUpdatedUsingPreparedStatement_ShouldSyncWithDB() {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> contactList = addressBookService.readContactData();
		addressBookService.updateContactDetails("Rohit", "Karol Bagh");
		boolean result = addressBookService.checkContactInSyncWithDB("Rohit");
		Assert.assertTrue(result);
	}

	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate endDate = LocalDate.now();
		List<Contact> contactList = addressBookService.readContactDataForGivenDateRange(startDate, endDate);
		Assert.assertEquals(7, contactList.size());
	}

	@Test
	public void givenContacts_RetrieveNumberOfContacts_ByCityOrState() {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		Map<String, Integer> contactByCityOrStateMap = addressBookService.readContactByCityOrState();
		Assert.assertEquals(true, contactByCityOrStateMap.get("Karnataka").equals(1));
		Assert.assertEquals(true, contactByCityOrStateMap.get("Mumbai").equals(1));
	}

	@Test
	public void givenNewContact_WhenAdded_ShouldSyncWithDB() {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		LocalDate date = LocalDate.of(2020, 11, 02);
		addressBookService.addContactToDB("Shreyas", "Iyer", "Sarojini Nagar", "Delhi", "Delhi", 503125, 72024874,
				"shreyas@gmail.com", "Personal", "Family", date);
		boolean result = addressBookService.checkContactInSyncWithDB("Shreyas");
		Assert.assertTrue(result);
	}

	@Test
	public void givenContacts_WhenAddedToDB_ShouldMatchEmployeeEntries() {
		Contact[] arrayOfEmployee = {
				new Contact("David", "Warner", "Alwal", "Hyderabad", "Telangana", 507012, 7325331,
						"david@gmail.com", "Casual", "Family",LocalDate.now()),
				new Contact("Shubham", "Gill", "Durga Nagar", "Kolkata", "West Bengal", 500050, 96763129,
						"gill@gmail.com", "Personal","Family", LocalDate.now()),
				new Contact("A B", "de Villiers", "Miyapur", "Jaipur", "Rajasthan", 600010, 87655433,
						"devilliers@gmail.com", "Corporate", "Family" , LocalDate.now()) };
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readData(IOService.DB_IO);
		Instant start = Instant.now();
		addressBookService.addContact(Arrays.asList(arrayOfEmployee));
		Instant end = Instant.now();
		log.info("Duration without thread : " + Duration.between(start, end));
		Instant threadStart = Instant.now();
		addressBookService.addEmployeeToPayrollWithThreads(Arrays.asList(arrayOfEmployee));
		Instant threadEnd = Instant.now();
		log.info("Duartion with Thread : " + Duration.between(threadStart, threadEnd));
		Assert.assertEquals(10, addressBookService.countEntries(IOService.DB_IO));
	}
}
