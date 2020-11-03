package com.blz.addressbookjdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class AddressBookDBService {
	private static AddressBookDBService addressBookDBService;
	private static Logger log = Logger.getLogger(AddressBookDBService.class.getName());
	private PreparedStatement ContactDataStatement;

	private AddressBookDBService() {
	}

	public static AddressBookDBService getInstance() {
		if (addressBookDBService == null)
			addressBookDBService = new AddressBookDBService();
		return addressBookDBService;
	}

	public List<Contact> readData() {
		String sql = "SELECT c.firstName, c.lastName,c.Address_Book_Name,c.Address,c.City,"
				+ "c.State,c.Zip,c.Phone_Number,c.Email,a.Address_Book_Type "
				+ "FROM contacts c INNER JOIN Address_Book_Dictionary a "
				+ "ON c.Address_Book_Name=a.Address_Book_Name; ";
		return this.getContactDetailsUsingSqlQuery(sql);
	}

	private List<Contact> getContactDetailsUsingSqlQuery(String sql) {
		List<Contact> ContactList = null;
		try (Connection connection = addressBookDBService.getConnection();) {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet result = preparedStatement.executeQuery(sql);
			ContactList = this.getAddressBookData(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ContactList;
	}

	private List<Contact> getAddressBookData(ResultSet resultSet) {
		List<Contact> contactList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				String firstName = resultSet.getString("firstName");
				String lastName = resultSet.getString("lastName");
				String addressBookName = resultSet.getString("Address_Book_Name");
				String address = resultSet.getString("Address");
				String city = resultSet.getString("City");
				String state = resultSet.getString("State");
				int zip = resultSet.getInt("zip");
				int phoneNumber = resultSet.getInt("Phone_Number");
				String email = resultSet.getString("email");
				String addressBookType = resultSet.getString("Address_Book_Type");
				contactList.add(new Contact(firstName, lastName, address, city, state, zip, phoneNumber, email,
						addressBookName, addressBookType));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactList;
	}

	public int updateEmployeeData(String name, String address) {
		return this.updateContactDataUsingPreparedStatement(name, address);
	}

	private int updateContactDataUsingPreparedStatement(String firstName, String address) {
		try (Connection connection = addressBookDBService.getConnection();) {
			String sql = "UPDATE contacts SET Address=? WHERE firstName=?;";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, address);
			preparedStatement.setString(2, firstName);
			int status = preparedStatement.executeUpdate();
			return status;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<Contact> getContactDataByName(String name) {
		List<Contact> contactList = null;
		if (this.ContactDataStatement == null)
			this.prepareStatementForContactData();
		try {
			ContactDataStatement.setString(1, name);
			ResultSet resultSet = ContactDataStatement.executeQuery();
			contactList = this.getAddressBookData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactList;
	}

	public List<Contact> getContactForGivenDateRange(LocalDate startDate, LocalDate endDate) {
		String sql = String.format(
				"SELECT c.firstName, c.lastName,c.Address_Book_Name,c.Address,c.City,"
						+ "c.State,c.Zip,c.Phone_Number,c.Email,a.Address_Book_Type "
						+ "FROM contacts c INNER JOIN Address_Book_Dictionary a "
						+ "ON c.Address_Book_Name=a.Address_Book_Name WHERE startDate BETWEEN '%s' AND '%s'; ",
				Date.valueOf(startDate), Date.valueOf(endDate));
		return this.getContactDetailsUsingSqlQuery(sql);
	}

	public Map<String, Integer> getContactsByCityOrState() {
		Map<String, Integer> contactByCityOrStateMap = new HashMap<>();
		ResultSet resultSet;
		String sqlCity = "SELECT city, COUNT(firstName) as count FROM contacts GROUP BY City; ";
		String sqlState = "SELECT state, COUNT(firstName) as count FROM contacts GROUP BY State; ";
		try (Connection connection = addressBookDBService.getConnection()) {
			Statement statement = connection.createStatement();
			resultSet = statement.executeQuery(sqlCity);
			while (resultSet.next()) {
				String city = resultSet.getString("city");
				Integer count = resultSet.getInt("count");
				contactByCityOrStateMap.put(city, count);
			}
			resultSet = statement.executeQuery(sqlState);
			while (resultSet.next()) {
				String state = resultSet.getString("state");
				Integer count = resultSet.getInt("count");
				contactByCityOrStateMap.put(state, count);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactByCityOrStateMap;
	}

	public Contact addContact(String firstName, String lastName, String address, String city, String state, int zip,
			int phone, String email, String addressBookName, String addressBookType, LocalDate startDate) {
		Connection connection = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			Statement statement = connection.createStatement();
			String sql = String.format(
					"INSERT INTO contacts(firstName,lastName,startDate, Address_Book_Name,Address_Book_Type,Address,City,State,Zip,Phone_Number,Email) values ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')",
					firstName, lastName,startDate, addressBookName, addressBookType, address, city, state, zip, phone, email);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e.printStackTrace();
			}
		}

		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return new Contact(firstName, lastName, address, city, state, zip, phone, email, addressBookName,
				addressBookType, startDate);
	}

	private void prepareStatementForContactData() {
		try {
			Connection connection = addressBookDBService.getConnection();
			String sql = "SELECT c.firstName, c.lastName,c.Address_Book_Name,c.Address,c.City,"
					+ "c.State,c.Zip,c.Phone_Number,c.Email,a.Address_Book_Type "
					+ "FROM contacts c INNER JOIN Address_Book_Dictionary a "
					+ "ON c.Address_Book_Name=a.Address_Book_Name WHERE firstName=?; ";
			ContactDataStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/address_book_service?useSSL=false";
		String userName = "root";
		String password = "Sourabhharale@143";
		Connection connection;
		log.info("Connecting to database: " + jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		log.info("Connection successful: " + connection);
		return connection;
	}
}
