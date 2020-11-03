package com.blz.addressbookjdbc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class AddressBookSystemTest {
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
		Assert.assertEquals(4, contactList.size());
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
		addressBookService.addContactToDatabase("Shreyas", "Iyer", "Sarojini Nagar", "Delhi", "Delhi", 503125, 72024874,
				"shreyas@gmail.com", "Personal", "Family", date);
		boolean result = addressBookService.checkContactInSyncWithDB("Shreyas");
		Assert.assertTrue(result);
	}
}
