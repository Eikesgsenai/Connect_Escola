package Codicis_Nocturni.connec_escola.controller.dto;

import Codicis_Nocturni.connec_escola.model.Role;
import jakarta.validation.constraints.NotNull;

// DTO para enviar a atualização de Role
public class UpdateRoleRequest {

    @NotNull(message = "A nova Role não pode ser nula")
    private Role role;

    // Getters e Setters
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}