public class UserType implements Globals {
    private Integer ID;
    // The name of the user type
    private String name;

    UserType (Integer ID, String name) {
        setID(ID);
        setName(name);
    }

    UserType () {
        this(null, null);
    }

    /**
     * Set the ID for this UserType model
     * @param ID Integer thte ID for this UserType model
     */
    private void setID(Integer ID) {
        this.ID = ID;
    }

    /**
     * Set the name of this UserType model
     * @param name String the name of this UserType model
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the name of the userType model
     * @return String get the model of the userType name
     */
    public String getName () {
        return this.name;
    }

    /**
     * Save the userType model to the database
     */
    public void save () {
        database.updateStatement("INSERT INTO userType (ID, name) VALUES (null, '" + getName() + "')");
    }
}
