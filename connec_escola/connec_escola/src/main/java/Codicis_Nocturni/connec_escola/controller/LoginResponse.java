package Codicis_Nocturni.connec_escola.controller.dto;

// DTO para enviar o token JWT (o "crachá") de volta ao frontend
public class LoginResponse {

    private String token;
    private String role; // Também enviamos a ROLE para o frontend saber o que mostrar

    public LoginResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    // Getters e Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}