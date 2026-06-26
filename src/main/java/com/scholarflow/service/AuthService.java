package com.scholarflow.service;

import com.scholarflow.dto.request.LoginRequest;
import com.scholarflow.dto.request.RefreshTokenRequest;
import com.scholarflow.dto.request.RegisterRequest;
import com.scholarflow.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
}
