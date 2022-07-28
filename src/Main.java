import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application implements Globals {
    // Track which stage is the current active window
    private Stage activeWindow;
    // Store the choices for the multiple choice question
    private List<MultipleChoiceOption> multipleChoiceOptions = new ArrayList<MultipleChoiceOption>();
    // Store the questions that are being added to a test
    private List<Question> testQuestions = new ArrayList<Question>();
    public Main () {
        database.createConnection();
    }

    /**
     * Attempt to login to the system with the credentials that the user enters
     * @param usernameInput TextField the text input field that contains the username
     * @param passwordInput PasswordField the input field that contains the user's password
     */
    private void attemptLogin (TextField usernameInput, PasswordField passwordInput) {
        // Validate the user's credentials and attempt login
        String loginResponse = authenticatedUser.login(usernameInput, passwordInput);
        // Show any error messages to user
        if (loginResponse != "Success") {
            Alert alert = new Alert("Login Error", loginResponse);
        } else {
            showHomeScene();
        }
    }

    /**
     * Set the current JavaFX stage window that is in focus
     * @param activeWindow Stage the JavaFX window that is in focus
     */
    private void setActiveWindow(Stage activeWindow) {
        this.activeWindow = activeWindow;
    }

    /**
     * Show the login screen to the user
     */
    private void loginScene () {
        activeWindow.setTitle("Login");

        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Username...");

        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Password...");

        // Button that validates the user's credentials
        Button loginBtn = new Button("Login");
        // Attach event listener to the login button
        loginBtn.setOnAction(e -> attemptLogin(usernameInput, passwordInput));

        // Create the layout for the scene
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        layout.getChildren().addAll(usernameInput, passwordInput, loginBtn);

        // Create the scene
        Scene scene = new Scene(layout, 300, 300);

        // Show the scene
        activeWindow.setScene(scene);
        activeWindow.show();
    }

    @Override
    public void start(Stage primaryStage) {
        setActiveWindow(primaryStage);

        // When the main window is closed, the application stops running so close the database connection
        activeWindow.setOnCloseRequest(e -> database.closeConnection());

        loginScene();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Show a form which allows the user to create a new arithmetic question
     */
    private void newArithmeticQuestionScene () {
        TextField questionInput = new TextField();
        questionInput.setPromptText("Question...");

        TextField pointsInput = new TextField();
        pointsInput.setText("10");
        pointsInput.setPromptText("Points...");

        TextField yearGroupInput = new TextField();
        yearGroupInput.setPromptText("Target year...");

        TextField answerInput = new TextField();
        answerInput.setPromptText("Answer...");

        TextField tagsInput = new TextField();
        tagsInput.setPromptText("Tags (Comma separated tags)");

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            ArithmeticQuestion arithmeticQuestion = new ArithmeticQuestion(null, questionInput.getText(), Integer.parseInt(pointsInput.getText()), Integer.parseInt(yearGroupInput.getText()), Double.parseDouble(answerInput.getText()), tagsInput.getText());
            arithmeticQuestion.save();
            activeWindow.hide();
            // Show an dialogue to notify the user that the question has been created
            Alert alert = new Alert("New Question", "The new question has been created");
        });

        // Create the layout for the scene
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        layout.getChildren().addAll(questionInput, pointsInput, yearGroupInput, answerInput, tagsInput, saveBtn);

        // Switch scene
        Scene newQuestionTypeScene = new Scene(layout, 600, 600);
        activeWindow.setTitle("New Arithmetic Question");
        activeWindow.setScene(newQuestionTypeScene);
    }

    /**
     * Add a new row to the user interface to allow another option to be defined to be multiple choice questions
     * @param layout VBox the layout that the row will be appended to
     */
    private void addChoice (VBox layout) {
        // Create a row to append to the layout
        HBox hBox = new HBox();
        TextField choiceInput = new TextField();
        choiceInput.setPromptText("Choice value...");
        CheckBox checkBox = new CheckBox();
        // Append the input fields required to define an option for a multiple choice question
        hBox.getChildren().addAll(choiceInput, checkBox);
        hBox.setAlignment(Pos.CENTER);

        // Append the row for option to the layout currently being shown to the user
        layout.getChildren().add(hBox);
        // Store all of the options that have been defined for the multiple choice question
        multipleChoiceOptions.add(new MultipleChoiceOption(null, choiceInput.getText(), checkBox.isSelected()));
    }

    /**
     * Show a scene where a user can create a new multiple choice question
     */
    private void newMultipleChoiceQuestionScene () {
        TextField questionInput = new TextField();
        questionInput.setPromptText("Question...");

        TextField pointsInput = new TextField();
        pointsInput.setText("10");
        pointsInput.setPromptText("Points...");

        TextField yearGroupInput = new TextField();
        yearGroupInput.setPromptText("Target year...");

        TextField tagsInput = new TextField();
        tagsInput.setPromptText("Tags (Comma separated tags)");

        // Create border pane to embed different layouts into 1 scene
        BorderPane borderPane = new BorderPane();
        // Create the layout for the scene
        VBox layout = new VBox();

        Button addChoice = new Button("Add Choice +");
        addChoice.setOnAction(e -> addChoice(layout));

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion(null, questionInput.getText(), Integer.parseInt(pointsInput.getText()), Integer.parseInt(yearGroupInput.getText()), tagsInput.getText(), multipleChoiceOptions);
            multipleChoiceQuestion.save();
            activeWindow.hide();
            // Show an dialogue to notify the user that the question has been created
            Alert alert = new Alert("New Question", "The new question has been created");
        });

        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        layout.getChildren().addAll(questionInput, pointsInput, yearGroupInput, tagsInput, saveBtn);

        // Position the save button at the bottom of the scene layout
        HBox bottomLayout = new HBox(saveBtn);
        bottomLayout.setAlignment(Pos.CENTER);
        borderPane.setBottom(bottomLayout);
        // Position the form input fields needed to create a new multiple choice question
        borderPane.setCenter(layout);

        // Add a button to the top of the scene which allows options to be defined for multiple choice question
        HBox topLayout = new HBox(addChoice);
        topLayout.setAlignment(Pos.CENTER);
        borderPane.setTop(topLayout);

        // Switch scene
        Scene newQuestionTypeScene = new Scene(borderPane, 600, 600);
        activeWindow.setTitle("New Multiple Choice Question");
        activeWindow.setScene(newQuestionTypeScene);
    }

    /**
     * Let the user pick which type of new question they would like to create
     */
    private void newQuestionScene () {
        // Create new window for the user can decide what type of questions they want to make
        Stage chooseQuestionTypeDialog = new Stage();
        // Only allow the current window to be in focus
        chooseQuestionTypeDialog.initModality(Modality.APPLICATION_MODAL);
        chooseQuestionTypeDialog.setTitle("New Question Type");

        Label choice = new Label();
        choice.setText("Choose the question type");

        Button arithmeticQuestionBtn = new Button("Maths Question");
        arithmeticQuestionBtn.setOnAction(e -> newArithmeticQuestionScene());

        Button multipleChoiceQuestionBtn = new Button("Multiple Choice Question");
        multipleChoiceQuestionBtn.setOnAction(e -> newMultipleChoiceQuestionScene());

        // Create the layout for the scene
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        layout.getChildren().addAll(choice, arithmeticQuestionBtn, multipleChoiceQuestionBtn);

        // Show the window
        Scene newQuestionTypeScene = new Scene(layout, 600, 600);
        chooseQuestionTypeDialog.setScene(newQuestionTypeScene);
        chooseQuestionTypeDialog.show();
        setActiveWindow(chooseQuestionTypeDialog);
    }

    /**
     * Allow the user to enter the details for a new test and save to database
     */
    private void newTestScene () {
        // Create new window for the user can decide what type of questions they want to make
        Stage newTestDialog = new Stage();
        // Only allow the current window to be in focus
        newTestDialog.initModality(Modality.APPLICATION_MODAL);
        newTestDialog.setTitle("New Test");

        // Create border pane to embed different layouts into 1 scene
        BorderPane borderPane = new BorderPane();

        TextField testNameInput = new TextField();
        testNameInput.setPromptText("Test name...");

        Label classLbl = new Label();
        classLbl.setText("Class");
        ComboBox<String> schoolClassInput = new ComboBox();
        ResultSet resultSet = database.executeQuery("SELECT * FROM schoolClass");
        try {
            while (resultSet.next()) {
                schoolClassInput.getItems().add(resultSet.getString("classCode"));
            }
        } catch (SQLException exception) {
            Alert alert = new Alert("Class Error", "Cannot read schoolClass table");
            exception.printStackTrace();
        } finally {
            try {
                resultSet.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        // Create the layout for the scene
        VBox newTestForm = new VBox();
        newTestForm.setAlignment(Pos.CENTER);
        newTestForm.setSpacing(10);
        Label questionHeading = new Label("All Questions:");

        newTestForm.getChildren().addAll(testNameInput, classLbl, schoolClassInput, questionHeading);

        HBox searchQuestions = new HBox();
        searchQuestions.setAlignment(Pos.CENTER);
        searchQuestions.setMaxHeight(300);
        TextField searchQuery = new TextField();
        searchQuery.setPromptText("Search...");
        Button searchBtn = new Button("Search");

        // Create the layout for the scene
        VBox questionsList = new VBox();
        questionsList.setAlignment(Pos.CENTER);
        questionsList.setSpacing(10);

        // Create functionality to search the questions by the tags assigned to them
        searchBtn.setOnAction(e -> {
            // If the search query is empty, list all questions
            // If the search query is not empty, collect the questions from the database that have a tag which matches the search query
            ResultSet matchingQuestions = (searchQuery.getText().isEmpty() || searchQuery.getText().isBlank() || searchQuery.getText() == null) ? database.executeQuery("SELECT * FROM question") : database.executeQuery("SELECT * FROM question WHERE tags LIKE '%," + searchQuery.getText() + ",%' OR tags LIKE '%, " + searchQuery.getText() + ",%'");
            // Clear the list before populating it with questions
            questionsList.getChildren().clear();
            try {
                // Loop through the questions
                while (matchingQuestions.next()) {
                    HBox question = new HBox();
                    question.setAlignment(Pos.CENTER);

                    Integer ID = matchingQuestions.getInt("ID");
                    String title = matchingQuestions.getString("title");
                    String type = matchingQuestions.getString("questionType");
                    Integer targetYearGroup = matchingQuestions.getInt("targetYearGroup");
                    Double answer = matchingQuestions.getDouble("answer");
                    String tags = matchingQuestions.getString("tags");
                    Integer points = matchingQuestions.getInt("points");

                    Label questionLbl = new Label("Question: " + title + ", Type: " + type + ", Year Group: " + targetYearGroup);

                    Button selectBtn = new Button("Select");
                    Button unselectBtn = new Button("Unselect");
                    unselectBtn.setVisible(false);

                    // The question has been selected to add to the test
                    selectBtn.setOnAction(event -> {
                        if (type == "ArithmeticQuestion") {
                            ArithmeticQuestion questionModel = new ArithmeticQuestion(ID, title, points, targetYearGroup, answer, tags);
                            // Store the selected question for when the test is being saved to database
                            testQuestions.add(questionModel);
                        } else {
                            MultipleChoiceQuestion questionModel = new MultipleChoiceQuestion(ID, title, points, targetYearGroup, tags, null);
                            questionModel.setOptionsFromDB();
                            // Store the selected question for when the test is being saved to database
                            testQuestions.add(questionModel);
                        }
                        // Hide the select button and show the unselect button
                        selectBtn.setVisible(false);
                        unselectBtn.setVisible(true);
                    });

                    // The question is being unselected and removed from the test
                    unselectBtn.setOnAction(event -> {
                        Question questionModel;
                        if (type == "ArithmeticQuestion") {
                            questionModel = new ArithmeticQuestion(ID, title, points, targetYearGroup, answer, tags);
                        } else {
                            questionModel = new MultipleChoiceQuestion(ID, title, points, targetYearGroup, tags, null);
                        }
                        // Remove the question from the stored questions for this test
                        testQuestions.remove(questionModel);
                        // Show the select button and hide the unselect button
                        selectBtn.setVisible(true);
                        unselectBtn.setVisible(false);
                    });

                    question.getChildren().addAll(questionLbl, selectBtn, unselectBtn);
                    questionsList.getChildren().add(question);
                }
            } catch (SQLException exception) {
                Alert alert = new Alert("Question Error", "Could not find questions to list");
                exception.printStackTrace();
            } finally {
                try {
                    matchingQuestions.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        });

        searchQuestions.getChildren().addAll(searchQuery, searchBtn);
        newTestForm.getChildren().addAll(searchQuestions, questionsList);

        // Get all questions from the database
        ResultSet questionRecords = database.executeQuery("SELECT * FROM question");

        try {
            // Loop the question records in the database
            while (questionRecords.next()) {
                HBox question = new HBox();
                question.setAlignment(Pos.CENTER);
                Integer ID = questionRecords.getInt("ID");
                String title = questionRecords.getString("title");
                String type = questionRecords.getString("questionType");
                Integer targetYearGroup = questionRecords.getInt("targetYearGroup");
                Double answer = questionRecords.getDouble("answer");
                String tags = questionRecords.getString("tags");
                Integer points = questionRecords.getInt("points");
                Label questionLbl = new Label("Question: " + title + ", Type: " + type + ", Year Group: " + targetYearGroup);

                Button selectBtn = new Button("Select");
                Button unselectBtn = new Button("Unselect");
                // Hide the unselect button by default
                unselectBtn.setVisible(false);

                // Execute this when question has been selected for test
                selectBtn.setOnAction(e -> {
                    Question questionModel;
                    if (type == "ArithmeticQuestion") {
                        questionModel = new ArithmeticQuestion(ID, title, points, targetYearGroup, answer, tags);
                    } else {
                        questionModel = new MultipleChoiceQuestion(ID, title, points, targetYearGroup, tags, null);
                    }
                    // Save the question for the list of questions attached to the test for when the test is saved to the database
                    testQuestions.add(questionModel);
                    // Hide the select button
                    selectBtn.setVisible(false);
                    // Show the unselect button
                    unselectBtn.setVisible(true);
                });

                // Execute this when question has been unselected for test
                unselectBtn.setOnAction(e -> {
                    Question questionModel;
                    if (type == "ArithmeticQuestion") {
                        questionModel = new ArithmeticQuestion(ID, title, points, targetYearGroup, answer, tags);
                    } else {
                        questionModel = new MultipleChoiceQuestion(ID, title, points, targetYearGroup, tags, null);
                    }
                    // Remove the question for the list of questions attached to the test for when the test is saved to the database
                    testQuestions.remove(questionModel);
                    // Show the select button
                    selectBtn.setVisible(true);
                    // Hide the select button
                    unselectBtn.setVisible(false);
                });

                question.getChildren().addAll(questionLbl, selectBtn, unselectBtn);
                questionsList.getChildren().add(question);
            }
        } catch (SQLException exception) {
            Alert alert = new Alert("Database Error", "Could not read questions from database");
            exception.printStackTrace();
        } finally {
            try {
                questionRecords.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        borderPane.setCenter(newTestForm);

        Button saveBtn = new Button("Save");
        // Execute when the test is being saved
        saveBtn.setOnAction(e -> {
            ResultSet schoolClassId = database.executeQuery("SELECT ID FROM schoolClass WHERE classCode='" + schoolClassInput.getValue() + "'");
            try {
                if (schoolClassId.next()) {
                    // Create the test model and save it to the database
                    Test test = new Test(null, testNameInput.getText(), schoolClassId.getInt("ID"), testQuestions);
                    test.save();
                    newTestDialog.close();
                    Alert alert = new Alert("Saved Test", "The new test (" + testNameInput.getText() + ") has been saved");
                }
            } catch (SQLException exception) {
                Alert alert = new Alert("Class Error", "Cannot find class in database with matching classCode(" + schoolClassInput.getValue() + ")");
                exception.printStackTrace();
            } finally {
                try {
                    schoolClassId.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        });
        borderPane.setBottom(saveBtn);

        // Show the window
        Scene newQuestionTypeScene = new Scene(borderPane, 600, 600);
        newTestDialog.setScene(newQuestionTypeScene);
        newTestDialog.show();
        setActiveWindow(newTestDialog);
    }


    /**
     * List the tests to the user from the database
     */
    private void listTestsScene () {
        // Create new window for the tests to be listed
        Stage testsDialog = new Stage();
        // Only allow the current window to be in focus
        testsDialog.initModality(Modality.APPLICATION_MODAL);
        testsDialog.setTitle("Tests");

        // Create border pane to embed different layouts into 1 scene
        BorderPane borderPane = new BorderPane();

        Button newTestBtn = new Button("New Test +");
        newTestBtn.setOnAction(e -> newTestScene());
        borderPane.setTop(newTestBtn);

        VBox testsList = new VBox();
        testsList.setAlignment(Pos.CENTER);
        testsList.setSpacing(10);
        Label testsHeading = new Label("All Tests:");
        testsList.getChildren().add(testsHeading);
        ResultSet allTests = database.executeQuery("SELECT * FROM test");

        try {
            // Loop through the test records from the database
            while (allTests.next()) {
                HBox testRow = new HBox();
                testRow.setMaxHeight(400);
                testRow.setAlignment(Pos.CENTER);

                Label testName = new Label(allTests.getString("name"));
                Button reviewBtn = new Button("Review");

                Test test = new Test(allTests.getInt("ID"), allTests.getString("name"), allTests.getInt("schoolClass"), new ArrayList<Question>());
                test.setQuestionsFromDB();
                reviewBtn.setOnAction(e -> reviewTest(test));
                testRow.getChildren().addAll(testName, reviewBtn);
                testsList.getChildren().add(testRow);
            }
        } catch (SQLException exception) {
            // Notify the user
            Alert alert = new Alert("Database Error","Could not read test from database");
            exception.printStackTrace();
        } finally {
            try {
                allTests.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        borderPane.setCenter(testsList);

        // Show the window
        Scene newQuestionTypeScene = new Scene(borderPane, 600, 600);
        testsDialog.setScene(newQuestionTypeScene);
        testsDialog.show();
        setActiveWindow(testsDialog);
    }

    /**
     * Show analytics of a test
     * @param test Test the test model that is being reviewed
     */
    private void reviewTest (Test test) {
        // Get the questions that are a part of the test
        List<Question> questions = test.getQuestions();

        // Create the window for the scene to be attached to
        Stage classesForTestDialog = new Stage();
        classesForTestDialog.initModality(Modality.APPLICATION_MODAL);
        classesForTestDialog.setTitle("Review Test");

        VBox questionsLayout = new VBox();
        questionsLayout.setAlignment(Pos.CENTER);
        // Limit the height of this layout
        questionsLayout.setMaxHeight(400);
        // Set padding between elements in the layout
        questionsLayout.setSpacing(20);

        Label testHeading = new Label();
        testHeading.setText("Review Test's (" + test.getName() + ") Analytics for Class (" + test.getSchoolClass().getClassCode() + ")");
        questionsLayout.getChildren().add(testHeading);

        // Loop through the question models that belong to the test
        for (Question question: questions) {
            Integer questionID = question.getID();
            // Get the stored answers that have been submitted for this question and this test
            ResultSet questionAttempts = database.executeQuery("SELECT * FROM submittedAnswer WHERE test=" + test.getID() + " AND question=" + questionID);
            Integer totalAnswers = 0;
            Integer correctAnswers = 0;

            try {
                // Loop over the submitted answers from users for this question
                while (questionAttempts.next()) {
                    // Track the amount of answers that have been submitted for this question
                    totalAnswers++;

                    // Check if the submitted answer was correct
                    Integer submittedAnswerCorrect = questionAttempts.getInt("correct");
                    // Track how many correct answers have been submitted for this question
                    if (submittedAnswerCorrect == 1) correctAnswers++;
                }

                HBox questionRow = new HBox();
                questionRow.setAlignment(Pos.CENTER);
                // Set padding
                questionRow.setSpacing(20);
                Label questionTitle = new Label();
                questionTitle.setText("Question: " + question.getTitle());

                VBox passRate = new VBox();
                passRate.setAlignment(Pos.CENTER);
                Label heading = new Label();
                heading.setText("Pass Rate");
                Float passPercentage = ((float) correctAnswers) / totalAnswers * 100;
                Label passRatePercentage = new Label(String.valueOf(passPercentage) + '%');
                passRate.getChildren().addAll(heading, passRatePercentage);

                questionRow.getChildren().addAll(questionTitle, passRate);
                questionsLayout.getChildren().add(questionRow);
            } catch (SQLException exception) {
                Alert alert = new Alert("Submitted Answer Error", "Error reading answers that have been submitted for question");
                exception.printStackTrace();
            } finally {
                try {
                    questionAttempts.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }

        Scene scene = new Scene(questionsLayout, 600, 600);
        classesForTestDialog.setScene(scene);
        classesForTestDialog.show();

    }

    /**
     * Show the main menu that is available for teachers
     */
    private void showTeacherScene () {
        activeWindow.setTitle("Teacher View");
        Button newQuestionBtn = new Button("New Question +");
        newQuestionBtn.setOnAction(e -> newQuestionScene());
        Button showsTestsBtn = new Button("Tests");
        // Show a different scene
        showsTestsBtn.setOnAction(e -> listTestsScene());

        // Create the layout for the scene
        HBox layout = new HBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        layout.getChildren().addAll(newQuestionBtn, showsTestsBtn);

        // Show the window
        Scene newUserDialogScene = new Scene(layout, 600, 600);
        activeWindow.setScene(newUserDialogScene);
    }

    /**
     * List tests that are available to the student
     */
    private void showStudentScene () {
        HBox layout = new HBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);

        // Collect the tests from the database that are available for the user which is currently logged in
        ResultSet availableTests = database.executeQuery("SELECT * FROM test WHERE schoolClass=" + authenticatedUser.getSchoolClass());
        try {
            // Loop over the tests
            while (availableTests.next()) {
                Button runTestBtn = new Button(availableTests.getString("name"));
                try {
                    // Create instance of test model
                    Test test = new Test(availableTests.getInt("ID"), availableTests.getString("name"), availableTests.getInt("schoolClass"), new ArrayList<Question>());
                    test.setQuestionsFromDB();
                    // When the test is run, execute this function
                    runTestBtn.setOnAction(e -> test.run());
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

                layout.getChildren().add(runTestBtn);
            }
        } catch (SQLException exception) {
            Alert alert = new Alert("Available Test Error", "Error reading available tests for student (" + authenticatedUser.getUsername() + ")");
            exception.printStackTrace();
        } finally {
            try {
                availableTests.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        // Create the scene
        Scene scene = new Scene(layout, 300, 300);

        // Show the scene
        activeWindow.setScene(scene);
        activeWindow.setTitle("Student View");
    }

    /**
     * Show the screen that system admins see once they have logged in
     */
    private void showAdminScene () {
        // Create the button which opens a new window to allow sysadmin to create new user
        Button createNewUserBtn = new Button("New User +");
        // Add event listener for when the button is clicked
        createNewUserBtn.setOnAction(e -> {
            // Create new window for the form where new users can be made
            Stage newUserDialog = new Stage();
            // Only allow the current window to be in focus
            newUserDialog.initModality(Modality.APPLICATION_MODAL);
            newUserDialog.setTitle("Create a New User");

            TextField nameInput = new TextField();
            nameInput.setPromptText("Name...");

            TextField usernameInput = new TextField();
            usernameInput.setPromptText("Username...");

            PasswordField passwordInput = new PasswordField();
            passwordInput.setPromptText("Password...");

            TextField studentIdInput = new TextField();
            studentIdInput.setPromptText("Student ID...");

            TextField dobInput = new TextField();
            dobInput.setPromptText("DOB (dd-mm-yyyy)");

            TextField yearInput = new TextField();
            yearInput.setPromptText("Student's Year...");

            Label userTypeLbl = new Label();
            userTypeLbl.setText("User Type");

            // Drop down load of user types
            ComboBox<String> userTypeInput = new ComboBox();
            // Select all of the user type option from the database
            ResultSet userTypes = database.executeQuery("SELECT * FROM userType");
            try {
                while (userTypes.next()) {
                    userTypeInput.getItems().add(userTypes.getString("name"));
                }
            } catch (SQLException exception) {
                Alert alert = new Alert("Database Error", "Cannot read user types from database");
            }

            Label classLbl = new Label();
            classLbl.setText("Class");
            // Drop down list of school classes
            ComboBox<String> schoolClassInput = new ComboBox();
            ResultSet classes = database.executeQuery("SELECT * FROM schoolClass");
            try {
                // Loop over the school classes
                while (classes.next()) {
                    schoolClassInput.getItems().add(classes.getString("classCode"));
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            } finally {
                try {
                    classes.close();
                } catch (SQLException exception) {
                    Alert alert = new Alert("Class Error", "Error reading classes from the database");
                    exception.printStackTrace();
                }
            }

            // Create button which saves the new user to the database
            Button saveNewUser = new Button("Save");
            // Event listener to handle retrieving user details from form and saving to database
            saveNewUser.setOnAction(event -> {
                // Get the userType ID from the database
                ResultSet userTypeRecord = database.executeQuery("SELECT * FROM userType WHERE name='" + userTypeInput.getValue() + "'");
                Integer userTypeID = null;
                try {
                    if (userTypeRecord.next()) {
                        userTypeID = userTypeRecord.getInt("ID");
                    }
                } catch (SQLException exception) {
                    // Notify the user
                    Alert alert = new Alert("Database Error", exception.getMessage());
                    exception.printStackTrace();
                } finally {
                    try {
                        userTypeRecord.close();
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                }

                // Get the school class ID from the database
                ResultSet schoolClassRecord = database.executeQuery("SELECT * FROM schoolClass WHERE classCode='" + schoolClassInput.getValue() + "'");
                Integer schoolClassID = null;
                try {
                    if (userTypeRecord.next()) {
                        schoolClassID = schoolClassRecord.getInt("ID");
                    }
                } catch (SQLException exception) {
                    // Notify the user
                    Alert alert = new Alert("Database Error", exception.getMessage());
                    exception.printStackTrace();
                } finally {
                    try {
                        schoolClassRecord.close();
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                }

                try {
                    // Check that the userType record could be found in the database
                    User newUser = new User(null, Integer.parseInt(studentIdInput.getText()), dobInput.getText(), nameInput.getText(),
                        usernameInput.getText(), passwordInput.getText(), schoolClassID,
                        Integer.parseInt(yearInput.getText()), userTypeID);

                    // Save user model to database
                    newUser.save();
                    // Close the form for adding a new user
                    newUserDialog.close();
                    // Show an dialogue to notify the user that the user has been created
                    Alert alert = new Alert("New User", "The new user (" + newUser.getUsername() + ") has been created");
                } catch (NumberFormatException exception) {
                    // Notify the user
                    Alert alert = new Alert("Incorrect Formatting", "Please make sure that the user's ID and year group are defined as  integers");
                    exception.printStackTrace();
                }
            });

            // Create the layout for the scene
            VBox layout = new VBox();
            layout.setAlignment(Pos.CENTER);
            // Set padding
            layout.setSpacing(10);
            layout.getChildren().addAll(studentIdInput, nameInput, usernameInput, passwordInput, dobInput, yearInput, userTypeLbl, userTypeInput, classLbl, schoolClassInput, saveNewUser);

            // Show the window
            Scene newUserDialogScene = new Scene(layout, 600, 600);
            newUserDialog.setScene(newUserDialogScene);
            newUserDialog.show();
        });

        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        // Set padding
        layout.setSpacing(10);
        layout.getChildren().add(createNewUserBtn);

        // Create the scene
        Scene scene = new Scene(layout, 300, 300);

        // Show the scene
        activeWindow.setScene(scene);
        activeWindow.setTitle("System Admin View");
    }

    /**
     * Switch the appropriate home screen based on user type
     */
    private void showHomeScene () {
        UserType userType = authenticatedUser.getUserType();
        String userTypeName = userType.getName();

        if (userTypeName.equals("sysadmin")) {
            showAdminScene();
        } else if (userTypeName.equals("teacher")) {
            showTeacherScene();
        } else { // Default to this view
            showStudentScene();
        }
    }

}
