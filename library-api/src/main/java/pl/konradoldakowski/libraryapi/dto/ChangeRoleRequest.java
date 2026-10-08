package pl.konradoldakowski.libraryapi.dto;

import jakarta.validation.constraints.NotBlank;
import pl.konradoldakowski.libraryapi.entity.Role;

public class ChangeRoleRequest {

    @NotBlank
    private Role role;

    public ChangeRoleRequest() {
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
