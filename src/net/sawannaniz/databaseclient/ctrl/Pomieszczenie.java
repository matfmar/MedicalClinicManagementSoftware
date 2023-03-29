package net.sawannaniz.databaseclient.ctrl;

public class Pomieszczenie implements SaveableToPrzychodnia {
    public Pomieszczenie(String nr, int p) {
        numer = nr;
        pietro = p;
    }
    public boolean insertToDatabase() {
        return true;
    }
    public boolean removeFromDatabase() {
        return true;
    }
    public boolean modifyInDatabase() {
        return true;
    }
    private String numer;
    private int pietro;
}
