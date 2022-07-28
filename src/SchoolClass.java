public class SchoolClass implements Globals {
    private Integer ID;
    private String classCode;

    SchoolClass (Integer ID, String classCode) {
        setID(ID);
        setClassCode(classCode);
    }

    SchoolClass () {
        this(null, null);
    }

    /**
     * Set the ID of the SchoolClass model
     * @param ID Integer the ID of the SchoolClass model
     */
    private void setID (Integer ID) {
        this.ID = ID;
    }

    /**
     * Set the reference name for the SchoolClass model
     * @param classCode String the reference name / class code for the SchoolClass model
     */
    private void setClassCode (String classCode) {
        this.classCode = classCode;
    }

    /**
     * Save the SchoolClass model to the database
     */
    public void save () {
        database.updateStatement("INSERT INTO schoolClass (ID, classCode) VALUES (null, '" + getClassCode() + "')");
    }

    /**
     * Get the unique code for the studentClass
     * @return String the unique classCode for the studentClass
     */
    public String getClassCode() {
        return classCode;
    }
}
