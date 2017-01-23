

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;



/**
 * Handles all database-related actions. Uses singleton design pattern. Modified
 * by Prof. Karpenko from the original example of Prof. Engle.
 * 
 * @see RegisterServer
 */
public class DatabaseHandler {

	/** Makes sure only one database handler is instantiated. */
	private static DatabaseHandler singleton = new DatabaseHandler();

	/** Used to determine if login_users table exists. */
	private static final String TABLES_SQL = "SHOW TABLES LIKE 'login_users';";

	/** Used to create login_users table for this example. */
	private static final String CREATE_SQL = "CREATE TABLE login_users ("
			+ "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " + "username VARCHAR(32) NOT NULL UNIQUE, "
			+ "password CHAR(64) NOT NULL, " + "usersalt CHAR(32) NOT NULL);";

	/** Used to insert a new user's info into the login_users table */
	private static final String REGISTER_SQL = "INSERT INTO login_users (username, password, usersalt) "
			+ "VALUES (?, ?, ?);";

	/** Used to determine if a username already exists. */
	private static final String USER_SQL = "SELECT username FROM login_users WHERE username = ?";

	// ------------------ constants below will be useful for the login operation
	// once you implement it
	/** Used to retrieve the salt associated with a specific user. */
	private static final String SALT_SQL = "SELECT usersalt FROM login_users WHERE username = ?";

	/** Used to authenticate a user. */
	private static final String AUTH_SQL = "SELECT username FROM login_users " + "WHERE username = ? AND password = ?";

	/** Used to remove a user from the database. */
	private static final String DELETE_SQL = "DELETE FROM login_users WHERE username = ?";
	
	/** Used to insert into HotelData Table */
	private static final String INSERT_HOTELDATA_SQL = "INSERT into hotelDataTable (hotelId,hotelName,streetAddress,city,state,lng,lat,country)"+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String INSERT_REVIEW_SQL = "INSERT into reviews (reviewId,hotelID,reviewTitle,reviewText,userName,date,overallRating)"+ "VALUES(?, ?, ?, ?, ?, ?, ?)";
	private static final String HOTEL_SQL = "SELECT * FROM hotelDataTable";
	private static final String REVIEW_SQL = "SELECT * from reviews where hotelId = ?";
	private static final String ADD_REVIEW = "INSERT into reviews (reviewId,hotelID,reviewTitle,reviewText,userName,date,overallRating)"+ "VALUES(?, ?, ?, ?, ?, ?, ?)";
	private static final String AVG_RATING = "SELECT AVG(overallRating) as average from reviews where hotelId=?";
	private static final String KEYWORD_SEARCH = "SELECT * from hotelDataTable where hotelName LIKE ? ESCAPE '!'";
	private static final String PARTICULAR_HOTEL = "SELECT * FROM hotelDataTable where hotelId=?";
	private static final  String SEARCH_BY_PLACE = "SELECT * FROM hotelDataTable WHERE city = ? OR state = ? OR country = ? ";
	private static final String CHECK_FOR_EXISTING_REVIEW = "SELECT * from reviews where userName = ? AND hotelID = ?";
	private static final String UPDATE_REVIEW = "UPDATE reviews SET reviewTitle = ?,reviewText = ?,overAllRating = ?,date=? WHERE userName=? AND hotelID=? ";
	private static final String SAVE_HOTEL = "INSERT INTO savedHotels(userName,hotelID,hotelName)"+"VALUES(?,?,?)";
	private static final String GET_SAVED_HOTELS = "SELECT * from savedHotels where userName = ?";
	private static final String GET_LAST_LOGIN = "SELECT * from lastLogin where userName=?";
	private static final String INSERT_LAST_LOGIN = "INSERT into lastLogin(userName,lastLoginDate)"+"VALUES(?,?)";
	private static final String UPDATE_LAST_LOGIN ="UPDATE lastLogin SET lastLoginDate=? where userName=?";
	private static final String SORT_REVIEWS_BY_DATE = "SELECT * FROM reviews where hotelID=? ORDER BY date DESC";
	private static final String SORT_REVIEWS_BY_RATING = "SELECT * FROM reviews where hotelID=? ORDER BY overAllRating DESC";
	private static final String CLEAR_SAVED_HOTELS = "DELETE FROM savedHotels where userName=? ";
	private static final String SAVE_EXPEDIA_LINK = "INSERT INTO expediaLinks(userName,hotelID,link)"+"VALUES(?,?,?)";
	private static final String GET_SAVED_LINKS = "SELECT * from expediaLinks where userName = ?";
	private static final String CLEAR_EXPEDIA_LINKS = "DELETE FROM expediaLinks where userName=? ";
	private static final String PAGINATED_REVIEWS = "SELECT * FROM reviews where hotelID=? LIMIT ?,5";
	private static final String REVIEWS_COUNT = "SELECT COUNT(reviewID) from reviews where hotelID=?";
	/** Used to configure connection to database. */
	private DatabaseConnector db;

	/** Used to generate password hash salt for user. */
	private Random random;

	/**
	 * This class is a singleton, so the constructor is private. Other classes
	 * need to call getInstance()
	 */
	public DatabaseHandler() {
		Status status = Status.OK;
		random = new Random(System.currentTimeMillis());

		try {
			db = new DatabaseConnector("database.properties");
			status = db.testConnection() ? setupTables() : Status.CONNECTION_FAILED;
		} catch (FileNotFoundException e) {
			status = Status.MISSING_CONFIG;
		} catch (IOException e) {
			status = Status.MISSING_VALUES;
		}

		if (status != Status.OK) {
			System.out.println("Error while obtaining a connection to the database: " + status);
		}
	}

	/**
	 * Gets the single instance of the database handler.
	 *
	 * @return instance of the database handler
	 */
	public static DatabaseHandler getInstance() {
		return singleton;
	}

	/**
	 * Checks to see if a String is null or empty.
	 * 
	 * @param text
	 *            - String to check
	 * @return true if non-null and non-empty
	 */
	public static boolean isBlank(String text) {
		return (text == null) || text.trim().isEmpty();
	}

	/**
	 * Checks if necessary table exists in database, and if not tries to create
	 * it.
	 *
	 * @return {@link Status.OK} if table exists or create is successful
	 */
	private Status setupTables() {
		Status status = Status.ERROR;

		try (Connection connection = db.getConnection(); Statement statement = connection.createStatement();) {
			if (!statement.executeQuery(TABLES_SQL).next()) {
				// Table missing, must create
				statement.executeUpdate(CREATE_SQL);

				// Check if create was successful
				if (!statement.executeQuery(TABLES_SQL).next()) {
					status = Status.CREATE_FAILED;
				} else {
					status = Status.OK;
				}
			} else {
				status = Status.OK;
			}
		} catch (Exception ex) {
			status = Status.CREATE_FAILED;
		}

		return status;
	}

	/**
	 * Tests if a user already exists in the database. Requires an active
	 * database connection.
	 *
	 * @param connection
	 *            - active database connection
	 * @param user
	 *            - username to check
	 * @return Status.OK if user does not exist in database
	 * @throws SQLException
	 */
	private Status duplicateUser(Connection connection, String user) {

		assert connection != null;
		assert user != null;

		Status status = Status.ERROR;

		try (PreparedStatement statement = connection.prepareStatement(USER_SQL);) {
			statement.setString(1, user);

			ResultSet results = statement.executeQuery();
			status = results.next() ? Status.DUPLICATE_USER : Status.OK;
		} catch (SQLException e) {
			status = Status.SQL_EXCEPTION;
			System.out.println("Exception occured while processing SQL statement:" + e);
		}

		return status;
	}

	/**
	 * Returns the hex encoding of a byte array.
	 *
	 * @param bytes
	 *            - byte array to encode
	 * @param length
	 *            - desired length of encoding
	 * @return hex encoded byte array
	 */
	public static String encodeHex(byte[] bytes, int length) {
		BigInteger bigint = new BigInteger(1, bytes);
		String hex = String.format("%0" + length + "X", bigint);

		assert hex.length() == length;
		return hex;
	}

	/**
	 * Calculates the hash of a password and salt using SHA-256.
	 *
	 * @param password
	 *            - password to hash
	 * @param salt
	 *            - salt associated with user
	 * @return hashed password
	 */
	public static String getHash(String password, String salt) {
		String salted = salt + password;
		String hashed = salted;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salted.getBytes());
			hashed = encodeHex(md.digest(), 64);
		} catch (Exception ex) {
			System.out.println("Unable to properly hash password." + ex);
		}

		return hashed;
	}

	/**
	 * Registers a new user, placing the username, password hash, and salt into
	 * the database if the username does not already exist.
	 *
	 * @param newuser
	 *            - username of new user
	 * @param newpass
	 *            - password of new user
	 * @return {@link Status.OK} if registration successful
	 */
	public Status registerUser(String newuser, String newpass) {
		Status status = Status.ERROR;
		System.out.println("Registering " + newuser + ".");

		// make sure we have non-null and non-emtpy values for login
		if (isBlank(newuser) || isBlank(newpass)) {
			status = Status.INVALID_LOGIN;
			System.out.println("Invalid regiser info");
			return status;
		}

		// try to connect to database and test for duplicate user
		try (Connection connection = db.getConnection();) {
			status = duplicateUser(connection, newuser);

			// if okay so far, try to insert new user
			if (status == Status.OK) {
				// generate salt
				byte[] saltBytes = new byte[16];
				random.nextBytes(saltBytes);

				String usersalt = encodeHex(saltBytes, 32); // hash salt
				String passhash = getHash(newpass, usersalt); // combine
																// password and
																// salt and hash
																// again

				// add user info to the database table
				try (PreparedStatement statement = connection.prepareStatement(REGISTER_SQL);) {
					statement.setString(1, newuser);
					statement.setString(2, passhash);
					statement.setString(3, usersalt);
					statement.executeUpdate();

					status = Status.OK;
				}
			}
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}

		return status;
	}

	/**
	 * Gets the salt for a specific user.
	 *
	 * @param connection
	 *            - active database connection
	 * @param user
	 *            - which user to retrieve salt for
	 * @return salt for the specified user or null if user does not exist
	 * @throws SQLException
	 *             if any issues with database connection
	 */
	private String getSalt(Connection connection, String user) throws SQLException {
		assert connection != null;
		assert user != null;

		String salt = null;

		try (PreparedStatement statement = connection.prepareStatement(SALT_SQL);) {
			statement.setString(1, user);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				salt = results.getString("usersalt");
			}
		}

		return salt;
	}
	/**
	 * This Method is for user login
	 * @param newuser
	 * @param newpass
	 * @return
	 */
	public Status loginUser(String newuser, String newpass) {
		Status status=Status.ERROR;
		System.out.println("Registering " + newuser + ".");

		// make sure we have non-null and non-emtpy values for login
		if (isBlank(newuser) || isBlank(newpass)) {
			
			status = Status.INVALID_LOGIN;
			System.out.println("Invalid regiser info");
			
			return status;
		}

		// try to connect to database and test for duplicate user
		try (Connection connection = db.getConnection();) {
			//status = duplicateUser(connection, newuser);
			String salt2 = getSalt(connection, newuser);
			System.out.println(salt2);
			String hash2 = getHash(newpass, salt2);
			System.out.println(hash2);
				try (PreparedStatement statement = connection.prepareStatement(AUTH_SQL);) {
					statement.setString(1, newuser);
					statement.setString(2, hash2);
					//statement.setString(3, usersalt);
					ResultSet results = statement.executeQuery();
					status = results.next() ?Status.OK:Status.INVALID_USER;
					//status = Status.OK;
				}
			//}
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}

		return status;
	} //This is what I need to edit
	/**
	 * This method inserts all Hotel data in hotelDataTable in the Database
	 * @param hotelId
	 * @param hotelName
	 * @param streetAddress
	 * @param city
	 * @param state
	 * @param lng
	 * @param lat
	 * @param country
	 * @return
	 */
	public Status createHotelDataTable(String hotelId,String hotelName, String streetAddress, String city, String state, double lng, double lat, String country){
		Status status=Status.OK;
		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(INSERT_HOTELDATA_SQL);) {
				statement.setString(1, hotelId);
				statement.setString(2, hotelName);
				statement.setString(3, streetAddress);
				statement.setString(4, city);
				statement.setString(5, state);
				statement.setDouble(6, lng);
				statement.setDouble(7, lat);
				statement.setString(8, country);
				//statement.setString(3, usersalt);
				statement.executeUpdate();
				
				status = Status.OK;
			}
			
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		}
		return status;
		
	}
	/**
	 * This method is for populating the Reviews Table
	 * @param reviewId
	 * @param hotelId
	 * @param reviewTitle
	 * @param reviewText
	 * @param userName
	 * @param date
	 * @param overallRating
	 * @return
	 */
	public Status createReviewsTable(String reviewId, String hotelId, String reviewTitle, String reviewText, String userName, String date, double overallRating ){
		Status status=Status.OK;
		
		try (Connection connection = db.getConnection();) {
			String dateSQL = date.toString();
			try (PreparedStatement statement = connection.prepareStatement(INSERT_REVIEW_SQL);) {
				statement.setString(1, reviewId);
				statement.setString(2, hotelId);
				statement.setString(3, reviewTitle);
				statement.setString(4, reviewText);
				statement.setString(5, userName);
				statement.setString(6, dateSQL );
				statement.setDouble(7, overallRating);
				statement.executeUpdate();
				status = Status.OK;
			}
			
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println("Error while connecting to the database: " + ex);
		} 
		return status;
	}
	/**
	 * Prints a list of hotels
	 * @param out
	 */
	public void printHotels(PrintWriter out){
		Map<String,String> hotelsDisplayMap = new HashMap<String,String>();
		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(HOTEL_SQL);) {
				ResultSet results = statement.executeQuery();
				
				while(results.next()){					
					String hotelName = results.getString(2);
					String hid = results.getString(1);
					hotelsDisplayMap.put(hid, hotelName);					
				}	
				/*  first, get and initialize an engine  */
		        VelocityEngine ve = new VelocityEngine();
		        ve.init();
		        /*  next, get the Template  */
		        Template t = ve.getTemplate( "HTML_PAGES/showHotels.html" );
		        /*  create a context and add data */
		        VelocityContext context = new VelocityContext();
		        context.put("hotels", hotelsDisplayMap);
		        t.merge( context, out );
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	/**
	 * Print reviews of a particular Hotel 
	 * @param out
	 * @param hotelId
	 */
	 public void printReviews(PrintWriter out,String hotelId,int start){
		 Map<String,String> reviewUserNames = new HashMap<String, String>();
		 Map<String,String> reviewTexts = new HashMap<String, String>();
		 Map<String,String> reviewTitles = new HashMap<String,String>(); 
		 Map<String,Double> reviewRatings = new HashMap<String,Double>();
		 Map<String,String> reviewDates = new HashMap<String,String>(); 
			try (Connection connection = db.getConnection();) {
				try (PreparedStatement statement = connection.prepareStatement(PAGINATED_REVIEWS);) {
					statement.setInt(2, start);
					statement.setString(1,hotelId);
					ResultSet results = statement.executeQuery();
					while(results.next()){
						String reviewId = results.getString(1);
						String reviewTitle = results.getString(3);
						String userName = results.getString(5);
						String reviewText = results.getString(4);
						String date = results.getString(6);
						double overAllRating = results.getDouble(7);
						reviewUserNames.put(reviewId, userName);
						reviewTexts.put(reviewId, reviewText);
						reviewTitles.put(reviewId, reviewTitle);
						reviewDates.put(reviewId, date);
						reviewRatings.put(reviewId, overAllRating);					
					}
					
					int aPCount = paginatedReviews(out, hotelId);
					/*  first, get and initialize an engine  */
			        VelocityEngine ve = new VelocityEngine();
			        ve.init();
			        /*  next, get the Template  */
			        Template t = ve.getTemplate( "HTML_PAGES/showReviews.html" );
			        /*  create a context and add data */
			        VelocityContext context = new VelocityContext();
			      context.put("hotelId",hotelId);
			       context.put("reviewUserNames", reviewUserNames);
			       context.put("reviewTexts", reviewTexts);
			       context.put("reviewTitles", reviewTitles);
			       context.put("reviewRatings", reviewRatings);
			       context.put("reviewDates", reviewDates);
			       context.put("actualPageCount", aPCount);
			        t.merge( context, out );
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
	 }
	 
	/**
	 * Method to calculate the average rating of a class
	 * @param hotelId
	 * @return
	 */
	 public double printAverageRating(String hotelId){
		 System.out.println(hotelId);
		 try (Connection connection = db.getConnection();) {
				try (PreparedStatement statement2 = connection.prepareStatement(AVG_RATING);) {
					statement2.setString(1,hotelId);
					ResultSet results2 = statement2.executeQuery();
					while(results2.next()){
					double rating = results2.getDouble("average");
					System.out.println(rating);
					return Math.round(rating);
					}
				}
		 } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
		 
	 }
	
	
/**
 * Method to display hotels by City, State or Country
 * @param out
 * @param place
 */
 public void displayHotelsByCityStateCountry(PrintWriter out, String place){
	 Map<String,String> hotelsDisplayMap = new HashMap<String,String>();
	 place = place.replaceAll("%","");
	 try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(SEARCH_BY_PLACE);) {
				statement.setString(1, place);
				statement.setString(2, place);
				statement.setString(3, place);
				ResultSet results = statement.executeQuery();
				while(results.next()){
					String hotelName = results.getString(2);
					String hid = results.getString(1);
					hotelsDisplayMap.put(hid, hotelName);
					
				}
				/*  first, get and initialize an engine  */
		        VelocityEngine ve = new VelocityEngine();
		        ve.init();
		        /*  next, get the Template  */
		        Template t = ve.getTemplate( "HTML_PAGES/showHotels.html" );
		        /*  create a context and add data */
		        VelocityContext context = new VelocityContext();
		        context.put("hotels", hotelsDisplayMap);
		        t.merge( context, out );
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 } catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	 
	
 }
 /**
  * Look up if user has already entered a review
  * @param userName
  * @param hotelID
  * @return
  */
 public Status lookUpExistingUserReview(String userName,String hotelID){
		Status status=Status.OK;
		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(CHECK_FOR_EXISTING_REVIEW);) {
				statement.setString(1, userName);
				statement.setString(2, hotelID);
				ResultSet results = statement.executeQuery();
				status = results.next() ? Status.OK: Status.ERROR;
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}
 
 /**
  * save a hotel 
  * @param userName
  * @param hotelId
  * @param hotelName
  */
 public void saveHotel(String userName, String hotelId, String hotelName){
	 try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(SAVE_HOTEL);) {
				statement.setString(1, userName);
				statement.setString(2, hotelId);
				statement.setString(3, hotelName);
				statement.executeUpdate();
			}
 } catch (SQLException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
 }
 
 /**
  * Retrieve Saved hotels
  * @param userName
  * @param out
  * @param hotelId
  */
 
 public void getSavedHotel(String userName, PrintWriter out, String hotelId){
	 Map<String,String> savedHotels = new HashMap<String,String>();
	 Map<String,String> savedExpediaLinks = new HashMap<String,String>();
	 try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(GET_SAVED_HOTELS);) {
				statement.setString(1, userName);
				ResultSet result = statement.executeQuery();
				if(!result.next()){
					savedHotels.put("default", "Nothing to Show here");
				}
				else{
					ResultSet resultnew = statement.executeQuery();
				while(resultnew.next()){
					String hId = resultnew.getString(2);
					String hName = resultnew.getString(3);
					savedHotels.put(hId, hName);
				}
				}
				
				
					try (PreparedStatement statement8 = connection.prepareStatement(GET_SAVED_LINKS);) {
						statement8.setString(1, userName);
						ResultSet result8 = statement8.executeQuery();
						if(!result8.next()){
							savedExpediaLinks.put("default", "Nothing to Show here");
						}
						else{
							ResultSet resultnew1 = statement8.executeQuery();
						while(resultnew1.next()){
							String hId = resultnew1.getString(2);
							String hlink = resultnew1.getString(3);
							savedExpediaLinks.put(hId, hlink);
						}
						}
					}
						
				
				/*  first, get and initialize an engine  */
		        VelocityEngine ve = new VelocityEngine();
		        ve.init();
		        /*  next, get the Template  */
		        Template t = ve.getTemplate( "HTML_PAGES/DisplaySavedHotelsExpediaLinks.html" );
		        /*  create a context and add data */
		        VelocityContext context = new VelocityContext();
		        context.put("userName", userName);
		        context.put("hotels", savedHotels);
		        context.put("hotelId", hotelId);
		        context.put("savedExpediaLinks", savedExpediaLinks);
		        t.merge( context, out );
			}
 } catch (SQLException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
 }
 /**
  * Update last login time
  * @param userName
  */
 public void updateLogin(String userName){
	 String date = getDate();
	 try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(GET_LAST_LOGIN);) {
				statement.setString(1, userName);
				ResultSet results = statement.executeQuery();
				if(results.next()){
					try (PreparedStatement statement3 = connection.prepareStatement(UPDATE_LAST_LOGIN);){
						statement3.setString(1, date);
						statement3.setString(2, userName);
						statement3.executeUpdate();
					}
				}
				else{
						try (PreparedStatement statement2 = connection.prepareStatement(INSERT_LAST_LOGIN);) {
							statement2.setString(1, userName);
							statement2.setString(2,date);
							statement2.executeUpdate();
						}
						
				}
				
			}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 
 }
 /**
  * function that returns current date
  * @return
  */
 public String getDate() {
		String format = "hh:mm a 'on' EEE, MMM dd, yyyy";
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(Calendar.getInstance().getTime());
	}
 /**
  * Get last login time of a particular user
  * @param userName
  * @return
  */
 public String getLastLogin(String userName){
	 String date="";
	 try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(GET_LAST_LOGIN);) {
				statement.setString(1, userName);
				ResultSet results = statement.executeQuery();
				if(results.next())
				{ 
				 date = results.getString(2);
				 return date;
				}
			}
	 } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 return "never";
	 
 }
 /**
  * Display reviews by date
  * @param out
  * @param hotelId
  */
 public void displayreviewsByDate(PrintWriter out,String hotelId){
	 Map<String,String> reviewUserNames = new HashMap<String, String>();
	 Map<String,String> reviewTexts = new HashMap<String, String>();
	 List<String> reviewIds = new LinkedList<String>();
		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement4 = connection.prepareStatement(SORT_REVIEWS_BY_DATE);) {
				statement4.setString(1,hotelId);
				ResultSet results4 = statement4.executeQuery();
				while(results4.next()){
					String reviewId = results4.getString(1);
					String userName = results4.getString(5);
					String reviewText = results4.getString(4);
					System.out.println(userName);
					reviewIds.add(reviewId);
					reviewUserNames.put(reviewId, userName);
					reviewTexts.put(reviewId, reviewText);
				}
				/*  first, get and initialize an engine  */
		        VelocityEngine ve = new VelocityEngine();
		        ve.init();
		        /*  next, get the Template  */
		        Template t = ve.getTemplate( "HTML_PAGES/showReviewsByOrder.html" );
		        /*  create a context and add data */
		        VelocityContext context = new VelocityContext();
		        context.put("hotelId",hotelId);
		       context.put("reviewUserNames", reviewUserNames);
		       context.put("reviewTexts", reviewTexts);
		       context.put("reviewIds", reviewIds);
		        t.merge( context, out );
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 }
 /**
  * Dsiplay highest review first
  * @param out
  * @param hotelId
  */
 public void displayreviewsByRating(PrintWriter out,String hotelId){
	 Map<String,String> reviewUserNames = new HashMap<String, String>();
	 Map<String,String> reviewTexts = new HashMap<String, String>();
	 List<String> reviewIds = new LinkedList<String>();
		try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(SORT_REVIEWS_BY_RATING);) {
				statement.setString(1,hotelId);
				ResultSet results = statement.executeQuery();
				while(results.next()){
					String reviewId = results.getString(1);
					String userName = results.getString(5);
					String reviewText = results.getString(4);
					reviewIds.add(reviewId);
					reviewUserNames.put(reviewId, userName);
					reviewTexts.put(reviewId, reviewText);
				}
				/*  first, get and initialize an engine  */
		        VelocityEngine ve = new VelocityEngine();
		        ve.init();
		        /*  next, get the Template  */
		        Template t = ve.getTemplate( "HTML_PAGES/showReviewsByORDER.html" );
		        /*  create a context and add data */
		        VelocityContext context = new VelocityContext();
		        context.put("hotelId",hotelId);
		       context.put("reviewUserNames", reviewUserNames);
		       context.put("reviewTexts", reviewTexts);
		       context.put("reviewIds", reviewIds);
		        t.merge( context, out );
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 }
 /**
  * clear hotels list
  * @param out
  * @param userName
  */
 public void clearHotels(PrintWriter out, String userName){
	 try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement5 = connection.prepareStatement(CLEAR_SAVED_HOTELS);) {
				statement5.setString(1,userName);
				statement5.executeUpdate();
			}
			
	 } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
 }
 /**
  * save visited expedia links
  * @param userName
  * @param hotelId
  * @param link
  */
 public void saveExpediaLinks(String userName, String hotelId, String link){
	 
	 try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(SAVE_EXPEDIA_LINK);) {
				statement.setString(1, userName);
				statement.setString(2, hotelId);
				statement.setString(3, link);
				statement.executeUpdate();
			}
} catch (SQLException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
 }
 /**
  * clear visited expedia links
  * @param out
  * @param userName
  */
 public void clearExpediaLinks(PrintWriter out, String userName){
	 try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement6 = connection.prepareStatement(CLEAR_EXPEDIA_LINKS);) {
				statement6.setString(1,userName);
				statement6.executeUpdate();
			}
			
	 } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
 }
 /**
  * paginated reviews, returns count
  * @param out
  * @param hotelId
  * @return
  */
 public int paginatedReviews(PrintWriter out, String hotelId){
	 double reviewCount = 0;
	 try (Connection connection = db.getConnection();) {
			try (PreparedStatement statement = connection.prepareStatement(REVIEWS_COUNT);) {
				statement.setString(1, hotelId);
				ResultSet results = statement.executeQuery();
				while(results.next()){
					reviewCount = results.getDouble(1);
					System.out.println("reviewCount is :"+reviewCount);
				}
			}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 int actualPageCount = (int) Math.ceil(reviewCount/5);
	 System.out.println("No. of pages are: "+(int) Math.ceil(reviewCount/5));
	 
	 return actualPageCount;
 }
 
}
