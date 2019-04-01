package it.andriaware.civicsense;

public class Comune {
    private String Comune;
    private String Email;

    public Comune(String comune, String email) {
        Comune = comune;
        Email = email;
    }

    public String getComune() {
        return Comune;
    }

    public void setComune(String comune) {
        Comune = comune;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }


}
