import java.sql.*;

public class Database {
    private static Connection conn = null;
    private static Statement stmt = null;

    /**
     * Drop all of the tables in the database
     */
    private void dropTables () {
        updateStatement("DROP TABLE multipleChoiceOptions");
        updateStatement("DROP TABLE question");
        updateStatement("DROP TABLE schoolClass");
        updateStatement("DROP TABLE user");
        updateStatement("DROP TABLE userType");
        updateStatement("DROP TABLE test");
        updateStatement("DROP TABLE submittedAnswer");
        updateStatement("DROP TABLE test_questions");
    }

    /**
     * Drop the tables and recreate them in the database
     */
    public void freshMigrate () {
        dropTables();

        updateStatement(
            "CREATE TABLE IF NOT EXISTS" +
            " question" +
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            " title STRING not NULL," +
            " targetYearGroup INTEGER," +
            " questionType STRING," +
            " answer STRING," +
            " tags STRING not NULL," +
            " points INTEGER default 10)"
        );

        updateStatement(
    "CREATE TABLE IF NOT EXISTS" +
            " multipleChoiceOptions" +
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            " question_id INTEGER not NULL," +
            " answer INTEGER DEFAULT FALSE," +
            " optionText STRING not NULL)"
        );

        updateStatement(
    "CREATE TABLE IF NOT EXISTS" +
            " user" +
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            " studentID INTEGER default NUll," +
            " DOB DATETIME not NULL," +
            " name STRING NOT NULL," +
            " username STRING NOT NULL UNIQUE," +
            " year INTEGER," +
            " schoolClass INTEGER," +
            " userType INTEGER NOT NULL," +
            " password STRING NOT NULL)"
        );

        updateStatement(
    "CREATE TABLE IF NOT EXISTS" +
            " submittedAnswer" +
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            " question INTEGER not NULL," +
            " student INTEGER not NULL," +
            " test INTEGER not NULL," +
            " correct INTEGER not NULL)"
        );

        updateStatement(
    "CREATE TABLE IF NOT EXISTS" +
            " schoolClass" +
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            " classCode STRING not NULL UNIQUE)"
        );

        updateStatement(
    "CREATE TABLE IF NOT EXISTS" +
            " userType" +
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            " name STRING not NULL UNIQUE)"
        );

        updateStatement(
    "CREATE TABLE IF NOT EXISTS" +
            " test" +
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            " name STRING not NULL," +
            " schoolClass INTEGER not NULL)"
        );

        updateStatement(
    "CREATE TABLE IF NOT EXISTS" +
            " test_questions" +
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            " test_id INTEGER not NULL," +
            " question_id INTEGER not NULL)"
        );
    }

    /**
     * Seed the database with boilerplate data
     */
    public void seed () {
        UserType studentType = new UserType(null, "student");
        studentType.save();
        UserType teacherType = new UserType(null, "teacher");
        teacherType.save();
        UserType sysadminType = new UserType(null, "sysadmin");
        sysadminType.save();

        User sysadmin = new User(null, null, "00-00-0000", "sysadmin", "sysadmin", "sysadmin", null, null, 3);
        sysadmin.save();

        User teacher = new User(null, null, "00-00-0000", "teacher", "teacher", "teacher", null, null, 2);
        teacher.save();

        User student = new User(null, null, "00-00-0000", "student", "student", "student", null, null, 1);
        student.save();

        SchoolClass schoolClass = new SchoolClass(null, "classCode");
        schoolClass.save();
    }

    /**
     * Create the connection to the sqllite database
     */
    public void createConnection () {
        try {
            Class.forName("org.sqlite.JDBC");

            conn = DriverManager.getConnection("jdbc:sqlite:sqlite_db1.db");
            stmt = conn.createStatement();

            System.out.println("Database connection has been created");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the connection to the database
     */
    public void closeConnection () {
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Close the connection
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute an update statement on the query
     * @param query String The query that is being queried against the database
     */
    public void updateStatement (String query) {
        try {
            stmt.executeUpdate(query);
        }
        catch (SQLException e) {
            Alert alert = new Alert("Database Error", "Could not execute update statement");
            e.printStackTrace();
        }
        catch (Exception e) {
            Alert alert = new Alert("Database Error", "Could not execute update statement");
            e.printStackTrace();
        }
    }

    /**
     * Execute a query against the database
     * @param query The query that is being run on the database
     * @return ResultSet the results of the query
     */
    public ResultSet executeQuery (String query) {
        try {
            ResultSet resultSet = stmt.executeQuery(query);
            return resultSet;
        }
        catch (SQLException e) {
            Alert alert = new Alert("Database Error", "Could not execute query");
            e.printStackTrace();
        }
        catch (Exception e) {
            Alert alert = new Alert("Database Error", "Could not execute query:");
            e.printStackTrace();
        }
        return null;
    }
}