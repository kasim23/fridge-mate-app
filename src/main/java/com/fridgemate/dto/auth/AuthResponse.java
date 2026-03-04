package com.fridgemate.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

// What the server sends back after a successful register or login.
// The client stores this token and sends it as "Authorization: Bearer <token>"
// on every subsequent request.
@Data
@AllArgsConstructor // we construct this with all three fields, so we need a full constructor
public class AuthResponse {

    private String token;  // the JWT string — "eyJ..."
    private String email;  // echoed back so the client knows who they're logged in as
    private String name;   // useful for displaying "Welcome, Omar" in the UI
}
