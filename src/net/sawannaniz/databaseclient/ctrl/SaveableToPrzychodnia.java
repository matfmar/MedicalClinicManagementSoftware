package net.sawannaniz.databaseclient.ctrl;

public interface SaveableToPrzychodnia {
    public boolean insertToDatabase();
    public boolean removeFromDatabase();
    public boolean modifyInDatabase();
}
