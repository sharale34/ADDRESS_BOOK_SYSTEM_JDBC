package com.blz.addressbookjdbc;

import java.time.LocalDate;
import java.util.List;

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
		Assert.assertEquals(3, contactList.size());
	}
}
