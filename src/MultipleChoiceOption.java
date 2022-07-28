public class MultipleChoiceOption implements Globals {
    private Integer ID;
    // This is the option text displayed to the user
    private String value;
    // Is the choice the correct answer for the multiple choice question
    private Boolean answer;
    // What question is this an option to
    private Integer QuestionID;

    MultipleChoiceOption (Integer ID, String value, Boolean answer) {
        setID(ID);
        setValue(value);
        setAnswer(answer);
    }

    MultipleChoiceOption () {
        this(null, null, null);
    }

    /**
     * Set the ID of the MultipleChoiceOption model
     * @param ID Integer the MultipleChoiceOption model's ID
     */
    private void setID(Integer ID) {
        this.ID = ID;
    }

    /**
     * Set the value of the option
     * @param value String the option value
     */
    private void setValue (String value) {
        this.value = value;
    }

    /**
     * Set if this option is a correct answer to the question
     * @param answer Boolean Is the option the correct answer to the question
     */
    private void setAnswer (Boolean answer) { this.answer = answer; }

    /**
     * Get the value of the option
     * @return String the value of the option
     */
    public String getValue () {
        return this.value;
    }

    /**
     * Get the checkbox field that shows if this option is a correct answer to the question
     * @return Boolean is this option the correct answer to the question
     */
    public Boolean getAnswer () { return this.answer; }

    /**
     * Set the ID of the question that this is an option for
     * @param questionID Integer for the question model in the database that this is an option for
     */
    public void setQuestionID (Integer questionID) {
        this.QuestionID = questionID;
    }

    /**
     * Saves the model to the database
     */
    public void save () {
        String optionVal = getValue();
        Boolean answerVal = getAnswer();

        database.updateStatement("INSERT INTO multipleChoiceOptions " +
            "(ID, question_id, answer, optionText) VALUES " +
            "(null, " + QuestionID + ", '" + answerVal + "', '" + optionVal + "')");
    }
}
