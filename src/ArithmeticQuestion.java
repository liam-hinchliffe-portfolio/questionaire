public class ArithmeticQuestion extends Question {
    // The correct answer for the question
    private Double answer;

    public ArithmeticQuestion (Integer ID, String title, Integer points, Integer targetYearGroup, Double answer, String tags) {
        super(ID, title, points, targetYearGroup, tags);
        setAnswer(answer);
    }

    public ArithmeticQuestion () {
        super();
        setAnswer(null);
    }

    /**
     * Compare the user submitted answer to the question's defined answer
     * @param submittedAnswer Double the submitted answer
     * @return Boolean if the submitted answer matches the defined answer
     */
    public boolean checkAnswer (Double submittedAnswer) {
        return (answer.equals(submittedAnswer)) ? true : false;
    }

    /**
     * Set the answer of the arithmetic question
     * @param answer Double the correct numeric answer of the question
     */
    private void setAnswer(Double answer) {
        this.answer = answer;
    }

    /**
     * Get the answer for the arithmetic question
     * @return Double the numeric answer for the arithmetic question
     */
    public Double getAnswer() {
        return answer;
    }

    /**
     * Save the user model to the database
     */
    public void save () {
        Globals.database.updateStatement("INSERT INTO question " +
            "(ID, title, targetYearGroup, questionType, answer, points, tags) VALUES " +
            "(" + getID() + ", + '" + getTitle() + "', " + getTargetYearGroup() + ", 'ArithmeticQuestion', " + getAnswer() + ", " + getPoints() + ", '" + getTags() + "')");
    }
}
