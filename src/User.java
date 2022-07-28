import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User implements Globals {
    private Integer ID;
    // Date of birth for the user
    private String DOB;
    // Name of the user
    private String name;
    // User's username
    private String username;
    // User's password
    private String password;
    // ID of the schoolClass model in the database
    private Integer schoolClass;
    // The school year that the user is a part of
    private Integer year;
    // ID of the userType model in the database
    private Integer userType;
    // The user's student ID
    private Integer studentID;

    public User (Integer ID, Integer studentID, String DOB, String name, String username, String password, Integer schoolClass, Integer year, Integer userType) {
        setID((ID != null && ID > 0) ? ID : null);
        setStudentID(studentID);
        setDOB(DOB);
        setName(name);
        setUsername(username);
        setPassword(password);
        setYear(year);
        setUserType(userType);
        setSchoolClass(schoolClass);
    }

    /**
     * Set the ID of this User model
     * @param ID Integer the ID of this user model
     */
    private void setID(Integer ID) {
        this.ID = ID;
    }

    /**
     * Set the SchoolClass which this user belongs to
     * @param schoolClass Integer the ID of the SchoolClass model which this user belongs to
     */
    private void setSchoolClass(Integer schoolClass) {
        this.schoolClass = schoolClass;
    }

    /**
     * Set the name of the User
     * @param name String the name of the user
     */
    private void setName(String name) {
        this.name = name;
    }

    /**
     * Set the date of birth of the user
     * @param DOB String in format dd-mm-yyyy
     */
    private void setDOB(String DOB) {
        this.DOB = DOB;
    }

    /**
     * Set the user's StudentID
     * @param studentID Integer the user's ID
     */
    private void setStudentID(Integer studentID) {
        this.studentID = studentID;
    }

    /**
     * Set the password for the user account
     * @param password String the password for the user account
     */
    private void setPassword(String password) {
        this.password = password;
    }

    /**
     * Set the username for the user
     * @param username String the username
     */
    private void setUsername(String username) {
        this.username = username;
    }

    /**
     * Set the user's account type
     * @param userType Integer the ID of the userType model
     */
    private void setUserType(Integer userType) {
        this.userType = userType;
    }

    /**
     * Set the school year that the user belongs to
     * @param year Integer the year
     */
    private void setYear(Integer year) {
        this.year = year;
    }

    /**
     * Get the student ID of the user
     * @return Integer the student ID
     */
    private Integer getStudentID() {
        return studentID;
    }

    /**
     * Get the name of the user
     * @return String name of user
     */
    private String getName() {
        return name;
    }

    /**
     * Get the school year which the user belongs to
     * @return Integer user's school year
     */
    private Integer getYear() {
        return year;
    }

    /**
     * Get the user's date of birth
     * @return String user's date of birth in format dd-mm-yyyy
     */
    private String getDOB() {
        return DOB;
    }

    /**
     * Get the user's password
     * @return String user's password
     */
    private String getPassword() {
        return password;
    }

    /**
     * Get the ID of the userType model which is assigned to this user
     * @return Integer ID of userType model for user's account type
     */
    private Integer getUserTypeId () {
        return this.userType;
    }

    /**
     * Get the ID of the user model
     * @return Integer the user's ID
     */
    public Integer getID () {
        return this.ID;
    }

    /**
     * Get the ID of the schoolClass model that the user has been assigned to
     * @return the ID of the schoolClass model that the user has been assigned to
     */
    public Integer getSchoolClass () {
        return this.schoolClass;
    }

    public User () {
        this(null, null, "00-00-0000", "User's Name", "Username", "password", null, null, 1);
    }

    /**
     * Authenticate the user
     * @param usernameField The username that is being authenticated
     * @param passwordField The password that is being authenticated
     * @return String The authentication error message - returns 'Success' if no errors
     */
    public String login (TextField usernameField, PasswordField passwordField) {
        // Run a presence validation check on username
        if (usernameField.getText().equals("") ||
            usernameField.getText().isEmpty() ||
            usernameField.getText().isBlank()) {
            // Return error message to show the user
            return "A username is required";
        }

        // Run a presence validation check on password
        if (passwordField.getText().equals("") ||
            passwordField.getText().isEmpty() ||
            passwordField.getText().isBlank()) {
            // Return error message to show the user
            return "A password is required";
        }

        // Check if there is a user in the database with a matching username from the user's input
        // Also check that the user record found in the database has a matching password compared to user's input
        ResultSet userRecord = database.executeQuery("SELECT * FROM user WHERE username='" + usernameField.getText() + "' AND password='" + passwordField.getText() + "'");

        try {
            // Check a record has been returned from the query
            if (!userRecord.next()) {
                return "Invalid credentials";
            } else {
                setID(userRecord.getInt("ID"));
                setUsername(userRecord.getString("username"));
                setPassword(userRecord.getString("password"));
                setName(userRecord.getString("name"));
                setDOB(userRecord.getString("DOB"));
                setYear(userRecord.getInt("year"));
                setSchoolClass(userRecord.getInt("schoolClass"));
                setUserType(userRecord.getInt("userType"));

                setAuthUser();

                return "Success";
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try { if (userRecord != null) userRecord.close(); } catch (Exception e) {e.printStackTrace();};
        }

        return "Something went wrong";

    }

    /**
     * Set the user to be the current authenticated user of the system
     */
    private void setAuthUser () {
        // Update global variable
        authenticatedUser.ID = getID();
        authenticatedUser.username = getUsername();
        authenticatedUser.password = getPassword();
        authenticatedUser.name = getName();
        authenticatedUser.DOB = getDOB();
        authenticatedUser.year = getYear();
        authenticatedUser.schoolClass = getSchoolClass();
        authenticatedUser.userType = getUserTypeId();
        authenticatedUser.studentID = getStudentID();
    }

    /**
     * Retrieve the username for the user
     * @return String The user's username
     */
    public String getUsername () {
        return this.username;
    }

    /**
     * Retrieve the UserType model for the user
     * @return UserType The UserType model for the user
     */
    public UserType getUserType () {
        // Get the userType record from the database which matches the userType ID assigned to the user
        ResultSet userTypeRecord = database.executeQuery("SELECT * FROM userType WHERE ID=" + getUserType());

        try {
            // Check that the query from the database returned a record
            if (userTypeRecord.next()) {
                // Instantiate the userType object based on the record details from the database
                return new UserType(userTypeRecord.getInt("ID"), userTypeRecord.getString("name"));
            } return null;
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            try { if (userTypeRecord != null) userTypeRecord.close(); } catch (Exception e) {e.printStackTrace();};
        }

        return null;
    }

    /**
     * Save the user model to the database
     */
    public void save () {
        database.updateStatement("INSERT INTO user " +
            "(ID, DOB, name, username, year, schoolClass, userType, password) VALUES " +
            "(null, '" + getDOB() + "','" + getName() + "', '" + getUsername() + "', " + getYear() + "," + getSchoolClass() + "," + getUserType() + ",'" + getPassword() + "')");
    }

}