import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MultipleChoiceQuestion extends Question {
    // List the options that are available choices for this question
    private List<MultipleChoiceOption> choices = new ArrayList<MultipleChoiceOption>();

    public MultipleChoiceQuestion (Integer ID, String title, Integer points, Integer targetYearGroup, String tags, List<MultipleChoiceOption> choices) {
        super(ID, title, points, targetYearGroup, tags);
        if (getChoices() != null) setChoices(choices);
    }

    public MultipleChoiceQuestion () {
        super();
        setChoices(new ArrayList<MultipleChoiceOption>());
    }

    /**
     * Set the choices that are available for this multiple choice question
     * @param choices List<MultipleChoiceOption> List of the MultipleChoiceOption models available for this multiple choice question
     */
    private void setChoices(List<MultipleChoiceOption> choices) {
        this.choices = choices;
    }

    /**
     * Get the options that are available choices for the question
     * @return List<MultipleChoiceOption> The list of choice options of the question
     */
    public List<MultipleChoiceOption> getChoices () {
        return choices;
    }

    /**
     * Read the options from the database that are choices for this question
     */
    public void setOptionsFromDB () {
        ResultSet options = database.executeQuery("SELECT * FROM multipleChoiceOptions WHERE question_id=" + getID());
        try {
            // Loop over the options for the multiple choice question
            while (options.next()) {
                Integer optionID = options.getInt("ID");
                String value = options.getString("optionText");
                Boolean answer = Boolean.parseBoolean(options.getString("answer"));
                // Store the choices that are available for the multiple choice question
                choices.add(new MultipleChoiceOption(optionID, value, answer));
            }
        } catch (SQLException exception) {
            Alert alert = new Alert("Multiple Choice Question Error", "Error reading options for the multiple choice question");
            exception.printStackTrace();
        } finally {
            try {
                options.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Save the user model to the database
     */
    public void save () {
        // Save the question to the database
        database.updateStatement("INSERT INTO question " +
            "(ID, title, targetYearGroup, questionType, answer, points, tags) VALUES " +
            "(" + getID() + ", + '" + getTitle() + "', " + getTargetYearGroup() + ", 'MultipleChoiceQuestion', null, " + getPoints() + ", '" + getTags() + "')");

        // Save the options for the multiple choice questions to the database
        for (MultipleChoiceOption option : getChoices()) {
            option.setQuestionID(getID());
            option.save();
        }
    }

}
