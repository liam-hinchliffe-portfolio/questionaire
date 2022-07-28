import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Test implements Globals {
    private Integer ID;
    // The name of the test model
    private String name;
    // ID of the schoolClass model in the database
    private Integer schoolClass;
    // The list of question models that are assigned to the test
    private List<Question> questions = new ArrayList<Question>();
    // The index of the questions list that is currently being shown to the user
    private Integer currentQuestionIndex = 0;
    // Track total available points for the test
    private Integer totalPoints = 0;
    // Track total points the user has been awarded
    private Integer awardedPoints = 0;
    private Stage runTestDialog;

    Test (Integer ID, String name, Integer schoolClass, List<Question> questions) {
        // If an ID has not been passed in to the constructor then set the ID to be the next available ID in the database table
        if (ID == null) {
            // Get the ID of the new test
            ResultSet testRecords = database.executeQuery("SELECT ID from test ORDER BY ID DESC LIMIT 1");
            try {
                if (testRecords.next()) {
                    // Get the latest ID in the database table and increment by 1
                    setID(testRecords.getInt("ID") + 1);
                } else setID(1);
            } catch (SQLException e) {
                // If this is being caught, it is likely there are no tests in the database table
                setID(1);
                e.printStackTrace();
            } finally {
                try {
                    testRecords.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        } else {
            setID(ID);
        }

        setName(name);
        setSchoolClass(schoolClass);
        setQuestions(questions);
    }

    Test () {
        this(null, "New Test", 1, null);
    }

    /**
     * Set the name for this test model
     * @param name String the name for this test model
     */
    private void setName(String name) {
        this.name = name;
    }

    /**
     * Set the schoolClass that this test is being assigned for
     * @param schoolClass Integer the ID of the SchoolClass model which this test is to be set for
     */
    private void setSchoolClass(Integer schoolClass) {
        this.schoolClass = schoolClass;
    }

    /**
     * Set the questions that are a part of this test
     * @param questions List<Question> The list of Question models
     */
    private void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    /**
     * Set the ID for this test model
     * @param ID Integer the ID for this test model
     */
    private void setID(Integer ID) {
        this.ID = ID;
    }



    /**
     * Save the test model to the database
     */
    public void save () {
        // Save test model to the database
        String query = "INSERT INTO test (ID, name, schoolClass) VALUES (null, '" + getName() + "', " + getSchoolClass() + ")";
        database.updateStatement(query);

        // Save the questions to the database that are assigned to the test
        for (Question question : questions) {
            query = "INSERT INTO test_questions (ID, test_id, question_id) VALUES (null, " + getID() + ", " + question.getID() + ")";
            database.updateStatement(query);
        }

    }

    /**
     * Read the questions for this test from the database and assign to model variable
     */
    public void setQuestionsFromDB () {
        questions = new ArrayList<Question>();

        // Get the questions from the database that belongs to the test
        ResultSet testsQuestions = database.executeQuery("SELECT * FROM question INNER JOIN test_questions ON question.id = test_questions.question_id WHERE test_questions.test_id=" + getID());
        try {
            // Loop over the questions
            while (testsQuestions.next()) {
                Integer ID = testsQuestions.getInt("ID");
                String title = testsQuestions.getString("title");
                String type = testsQuestions.getString("questionType");
                Integer targetYearGroup = testsQuestions.getInt("targetYearGroup");
                Double answer = testsQuestions.getDouble("answer");
                String tags = testsQuestions.getString("tags");
                Integer points = testsQuestions.getInt("points");


                // Store the question model to the test model in a list
                switch (type) {
                    case "ArithmeticQuestion":
                        ArithmeticQuestion arithmeticQuestion = new ArithmeticQuestion(ID, title, points, targetYearGroup, answer, tags);
                        questions.add(arithmeticQuestion);
                        break;
                    case "MultipleChoiceQuestion":
                        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion(ID, title, points, targetYearGroup, tags, null);
                        questions.add(multipleChoiceQuestion);
                        break;
                }
            }
        } catch (SQLException exception) {
            Alert alert = new Alert("Test's Questions Error", "Error reading questions for test");
            exception.printStackTrace();
        }
    }

    /**
     * Get the questions that are assigned to the test
     * @return List<Question> the list of question models that are apart of the test model
     */
    public List<Question> getQuestions () {
        return this.questions;
    }

    /**
     * Run the question based on the currentQuestionIndex in the questions list of the test model
     */
    private void runQuestion () {
        // Instantiate the question model for the question that is being shown to the user
        Question currentQuestion = questions.get(currentQuestionIndex);
        totalPoints += currentQuestion.getPoints();

        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);

        Label questionLabel = new Label();
        questionLabel.setText(currentQuestion.title);
        layout.getChildren().add(questionLabel);

        Button checkAnswerBtn = new Button("Submit");

        // Check type of the question
        if (currentQuestion instanceof ArithmeticQuestion) {
            TextField answerInput = new TextField();
            answerInput.setPromptText("Answer...");
            layout.getChildren().add(answerInput);

            // Cast the question model to the specific child class appropriate for this question
            ArithmeticQuestion currentArithmeticQuestion = (ArithmeticQuestion) currentQuestion;
            // When the answer has been submitted
            checkAnswerBtn.setOnAction(e ->  {
                // Check if the submitted answer is correct
                Boolean isCorrect = currentArithmeticQuestion.checkAnswer(Double.parseDouble(answerInput.getText()));

                // Notify the user of the result for the question
                if (isCorrect) {
                    Alert alert = new Alert("Marked Question", "Correct Answer");
                    awardedPoints += currentQuestion.getPoints();
                } else {
                    Alert alert = new Alert("Marked Question", "Incorrect Answer");
                }

                // Convert true and false value to integer 1 and 0 so that it can be stored in database as integer
                // Store the submitted answer in the database
                recordAnswer(currentArithmeticQuestion, (isCorrect) ? 1 : 0);

                // Increment the currentQuestionIndex ready to show the next question in the questions list
                currentQuestionIndex++;
                // Check that there is a value in the questions list for the newly incremented currentQuestionIndex
                if (currentQuestionIndex < questions.size()) {
                    // Show the next question
                    runQuestion();
                } else {
                    endTest();
                }
            });

        } else if (currentQuestion instanceof MultipleChoiceQuestion) {
            // Cast the question model to the specific child class appropriate for this question
            MultipleChoiceQuestion currentMultipleChoiceQuestion = (MultipleChoiceQuestion) currentQuestion;
            currentMultipleChoiceQuestion.setOptionsFromDB();

            // Loop over the options for the multiple choice question
            for (MultipleChoiceOption option : currentMultipleChoiceQuestion.getChoices()) {
                // Create a button with the option value
                Button optionBtn = new Button(option.getValue());
                // When the option is chosen
                optionBtn.setOnAction(e -> {
                    // Check if teh answer is correct and notify the user
                    if (option.getAnswer()) {
                        Alert alert = new Alert("Marked Question", "Correct Answer");
                        awardedPoints += currentQuestion.getPoints();
                    } else {
                        Alert alert = new Alert("Marked Question", "Incorrect Answer");
                    }

                    // Convert true and false value to integer 1 and 0 so that it can be stored in database as integer
                    // Store the submitted answer in the database
                    recordAnswer(currentMultipleChoiceQuestion, (option.getAnswer()) ? 1 : 0);

                    // Increment the currentQuestionIndex ready to show the next question in the questions list
                    currentQuestionIndex++;
                    // Check that there is a value in the questions list for the newly incremented currentQuestionIndex
                    if (currentQuestionIndex < questions.size()) {
                        // Show the next question
                        runQuestion();
                    } else {
                        endTest();
                    }
                });
                layout.getChildren().add(optionBtn);
            }
        }

        layout.getChildren().add(checkAnswerBtn);

        Scene scene = new Scene(layout, 600, 600);
        runTestDialog.setScene(scene);

    }

    /**
     * End the test and show the user their score
     */
    private void endTest () {
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        Label heading = new Label();
        heading.setText("You have completed your test!");
        Label testPoints = new Label();
        testPoints.setText(String.valueOf(awardedPoints) + '/' + totalPoints);
        layout.getChildren().addAll(heading, testPoints);

        Scene scene = new Scene(layout, 600,600);
        runTestDialog.setScene(scene);
    }

    /**
     * Run the test for the user
     */
    public void run () {
        runTestDialog = new Stage();
        runTestDialog.initModality(Modality.APPLICATION_MODAL);
        runTestDialog.setTitle("Running Test");

        totalPoints = 0;
        awardedPoints = 0;
        // Show first question
        runQuestion();

        runTestDialog.show();
    }

    /**
     * Store the submitted answer in the database
     * @param question Question the question model that the answer is being submitted for
     * @param answer Integer boolean (0 or 1) if the answer was correct or not
     */
    private void recordAnswer (Question question, Integer answer) {
        // Create model and save to the database
        SubmittedAnswer submittedAnswer = new SubmittedAnswer(null, question.getID(), authenticatedUser.getID(), getID(), answer);
        submittedAnswer.save();
    }

    /**
     * Get the ID of the test model
     * @return Integer the ID of the test model
     */
    public Integer getID() {
        return ID;
    }

    /**
     * get the SchoolClass model that the test has been assigned to
     * @return SchoolClass the schoolClass model that the test has been assigned to
     */
    public SchoolClass getSchoolClass() {
        // Find the schoolClass details for this test from the database
        ResultSet schoolClassRecord = database.executeQuery("SELECT * FROM schoolClass WHERE ID=" + getSchoolClass());
        try {
            if (schoolClassRecord.next()) {
                // Instantiate the SchoolClass model with the details from the datbase
                SchoolClass schoolClass = new SchoolClass(schoolClassRecord.getInt("ID"), schoolClassRecord.getString("classCode"));
                return schoolClass;
            }
        } catch (SQLException exception) {
            Alert alert = new Alert("Class Error", "Error getting the class for this test");
            exception.printStackTrace();
        } finally {
            try {
                schoolClassRecord.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Get the name of the test
     * @return String the name of the test model
     */
    public String getName() {
        return name;
    }
}
