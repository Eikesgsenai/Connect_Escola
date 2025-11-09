package Codicis_Nocturni.connec_escola.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO para receber os dados do formulário de login
public class LoginRequest {

    @NotBlank(message = "O email é obrigatório")
    @Email
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    // Getters e Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}