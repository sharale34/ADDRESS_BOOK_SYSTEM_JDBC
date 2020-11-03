package com.blz.addressbookjdbc;

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
}
