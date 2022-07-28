import java.sql.ResultSet;
import java.sql.SQLException;

public class Question implements Globals {
    protected Integer ID;
    // The title of question eg "10 + 10"
    protected String title;
    // The points that the question is worth
    protected Integer points;
    // The year group that the question is targeted at
    protected Integer targetYearGroup;
    // Comma separated list of strings
    protected String tags;

    public Question (Integer ID, String title, Integer points, Integer targetYearGroup, String tags) {
        // If an ID has not been passed in to the constructor then set the ID to be the next available ID in the database table
        if (ID == null) {
            // Get the ID of the new question
            ResultSet questionRecords = database.executeQuery("SELECT ID from question ORDER BY ID DESC LIMIT 1");
            try {
                if (questionRecords.next()) {
                    // Get the latest ID in the database table and increment by 1
                    setID(questionRecords.getInt("ID") + 1);
                }
            } catch (SQLException e) {
                // If this is being caught, it is likely there are no questions in the database table
                setID(1);
                e.printStackTrace();
            } finally {
                try {
                    questionRecords.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        } else {
            setID(ID);
        }

        setTitle(title);
        setPoints(points);
        setTargetYearGroup(targetYearGroup);
        setTags(tags);
    }

    /**
     * Set ID of question model
     * @param ID Integer ID of question model
     */
    private void setID(Integer ID) {
        this.ID = ID;
    }

    /**
     * Set number of points that the question is worth
     * @param points Integer the number of points the question is worth
     */
    private void setPoints(Integer points) {
        this.points = points;
    }

    /**
     * Set the searchable tags for the question
     * @param tags String a list of comma separated tags
     */
    private void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * Set the target year group for the question
     * @param targetYearGroup Integer the target year group for the question
     */
    private void setTargetYearGroup(Integer targetYearGroup) {
        this.targetYearGroup = targetYearGroup;
    }

    /**
     * Set the title of the question
     * @param title String the title of the question
     */
    private void setTitle(String title) {
        this.title = title;
    }

    public Question () {
        this(null, "New Question", 10, null, "");
    }

    /**
     * Get the ID of the question model
     * @return Integer the ID of the model
     */
    protected Integer getID() {
        return ID;
    }

    /**
     * Get the title of the question model
     * @return String the title of the model
     */
    protected String getTitle() {
        return title;
    }

    /**
     * Get points the question is worth
     * @return Integer points that the question is worth
     */
    protected Integer getPoints() {
        return points;
    }

    /**
     * Get the target year group for this question
     * @return Integer the target year group for this question
     */
    protected Integer getTargetYearGroup() {
        return targetYearGroup;
    }

    /**
     * Get the search tags for the question
     * @return String a string of comma separated tags
     */
    protected String getTags() {
        return tags;
    }
}
