package com.blz.addressbookjdbc;

import java.time.LocalDate;
import java.util.Objects;

public class Contact {
	public String firstName;
	public String lastName;
	public String address;
	public String city;
	public String state;
	public int zip;
	public int phoneNumber;
	public String email;
	public String addressBookName;
	public String addressBookType;
	public LocalDate startDate;

	public Contact(String firstName, String lastName, String address, String city, String state, int zip,
			int phoneNumber, String email, String addressBookName, String addressBookType) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.addressBookName = addressBookName;
		this.addressBookType = addressBookType;
	}

	public Contact(String firstName, String lastName, String address, String city, String state, int zip,
			int phoneNumber, String email, String addressBookName, String addressBookType, LocalDate startDate) {
		this(firstName, lastName, address, city, state, zip, phoneNumber, email, addressBookName, addressBookType);
		this.startDate = startDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, addressBookName, addressBookType, city, email, firstName, lastName, phoneNumber,
				startDate, state, zip);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contact other = (Contact) obj;
		return Objects.equals(address, other.address) && Objects.equals(addressBookName, other.addressBookName)
				&& Objects.equals(addressBookType, other.addressBookType) && Objects.equals(city, other.city)
				&& Objects.equals(email, other.email) && Objects.equals(firstName, other.firstName)
				&& Objects.equals(lastName, other.lastName) && phoneNumber == other.phoneNumber
				&& Objects.equals(startDate, other.startDate) && Objects.equals(state, other.state) && zip == other.zip;
	}
}