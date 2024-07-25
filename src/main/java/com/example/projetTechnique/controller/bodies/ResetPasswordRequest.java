package com.example.projetTechnique.controller.bodies;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    private String resetToken;
    private String newPassword;
}
