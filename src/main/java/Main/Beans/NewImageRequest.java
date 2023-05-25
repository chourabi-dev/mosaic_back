package Main.Beans;

public class NewImageRequest {
    public String nom;
    public String email;
    public String message;
    public String image;

    public NewImageRequest(String nom, String email, String message, String image) {
        this.nom = nom;
        this.email = email;
        this.message = message;
        this.image = image;
    }

    public NewImageRequest() {
    }
}
