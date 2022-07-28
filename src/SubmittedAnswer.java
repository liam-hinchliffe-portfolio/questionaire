public class SubmittedAnswer implements Globals {
    private Integer ID;
    // The ID of the question model that the answer was submitted for
    private Integer questionID;
    // The ID of the user that submitted the answer
    private Integer studentID;
    // The ID of the test model that the answer was submitted for
    private Integer testID;
    // Boolean (0 or 1) for if the answer was correct for the question
    private Integer correct;

    SubmittedAnswer (Integer ID, Integer questionID, Integer studentID, Integer testID, Integer correct) {
        setID(ID);
        setQuestionID(questionID);
        setStudentID(studentID);
        setTestID(testID);
        setCorrect(correct);
    }

    SubmittedAnswer () {
        this(null, 0, 0, 0, 0);
    }

    /**
     * Set the ID of the SubmittedAnswer model
     * @param ID Integer the ID of SubmittedAnswer model
     */
    private void setID(Integer ID) {
        this.ID = ID;
    }

    /**
     * Set if this submitted answer was submitted with the correct answer
     * @param correct Integer (0 or 1) boolean for if the submitted answer was correcy
     */
    private void setCorrect(Integer correct) {
        this.correct = correct;
    }

    /**
     * Set the ID of the question model which this SubmittedAnswer is related to
     * @param questionID Integer the ID of the question model which this SubmittedAnswer is related to
     */
    private void setQuestionID(Integer questionID) {
        this.questionID = questionID;
    }

    /**
     * Set the ID of the student model which submitted this answer
     * @param studentID Integer the ID of the student model who submitted this answer
     */
    private void setStudentID(Integer studentID) {
        this.studentID = studentID;
    }

    /**
     * Set the ID of the test model which this submitted answer is in relation to
     * @param testID Integer the ID of the test model which this submitted answer is in relation to
     */
    private void setTestID(Integer testID) {
        this.testID = testID;
    }

    /**
     * Get the SubmittedAnswer model ID
     * @return Integer the ID of this submittedAnswer model
     */
    private Integer getID() {
        return ID;
    }

    /**
     * Find if this SubmittedAnswer was submitted with the correct answer for the question
     * @return Integer (0 or 1) boolean for if the answer is correct
     */
    private Integer getCorrect() {
        return correct;
    }

    /**
     * Get the ID of the question model which this answer was submitted for
     * @return Integer ID of the question model which this answer was submitted for
     */
    private Integer getQuestionID() {
        return questionID;
    }

    /**
     * Get the ID of the student model who submitted this answer
     * @return Integer the ID of the student model which submitted this answer
     */
    private Integer getStudentID() {
        return studentID;
    }

    /**
     * Get the ID of the test model which this answer was submitted for
     * @return Integer the ID of the test model which this answer was submitted for
     */
    public Integer getTestID() {
        return testID;
    }

    /**
     * Save the submittedAnswer model to the database
     */
    public void save () {
        database.updateStatement("INSERT INTO submittedAnswer " +
            "(ID, question, student, test, correct) VALUES " +
            "(null, " + getQuestionID() + ", " + getStudentID() + ", " + getTestID() + ", " + getCorrect() + ")");
    }
}
