package com.tenpearls.service;

import com.tenpearls.model.User;
import com.tenpearls.repository.UserRepository;
import com.tenpearls.security.JwtService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private UserService userService;

    private AutoCloseable closeable;

    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder, jwtService);
    }
    
    @AfterMethod
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testRegisterUser_Success() {
        // Given
        String email = "test@example.com";
        String password = "Password123";
        String firstName = "Test";
        String lastName = "User";
        String encodedPassword = "encodedPassword";
        
        User savedUser = new User(
            firstName,
            lastName,
            email,
            encodedPassword,
            null  // Role
        );
        savedUser.setId(1L);
        
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        User registeredUser = userService.registerUser(firstName, lastName, email, password);
        
        // Then
        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getId()).isEqualTo(1L);
        assertThat(registeredUser.getEmail()).isEqualTo(email);
        assertThat(registeredUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(registeredUser.getFirstName()).isEqualTo(firstName);
        assertThat(registeredUser.getLastName()).isEqualTo(lastName);
        
        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    public void testRegisterUser_ExistingEmail() {
        // Given
        String email = "existing@example.com";
        String password = "Password123";
        String firstName = "Existing";
        String lastName = "User";
        
        when(userRepository.existsByEmail(email)).thenReturn(true);
        
        // When/Then
        assertThatThrownBy(() -> userService.registerUser(firstName, lastName, email, password))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("User with this email already exists");
        
        verify(userRepository).existsByEmail(email);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }
    
    @Test
    public void testRegisterUser_InvalidEmail() {
        // Given
        String email = "invalid-email";
        String password = "Password123";
        String firstName = "Invalid";
        String lastName = "Email";
        
        // When/Then
        assertThatThrownBy(() -> userService.registerUser(firstName, lastName, email, password))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid email format");
        
        verifyNoInteractions(userRepository, passwordEncoder);
    }
    
    @Test
    public void testRegisterUser_WeakPassword() {
        // Given
        String email = "test@example.com";
        String password = "weak";
        String firstName = "Weak";
        String lastName = "Password";
        
        when(userRepository.existsByEmail(email)).thenReturn(false);
        
        // When/Then
        assertThatThrownBy(() -> userService.registerUser(firstName, lastName, email, password))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Password must be at least 8 characters and include uppercase, lowercase, and numbers");
        
        verify(userRepository).existsByEmail(email);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }
    
    @Test
    public void testLoginUser_Success() {
        // Given
        String email = "test@example.com";
        String password = "Password123";
        String token = "jwt-token";
        
        User user = new User(
            "Test",
            "User",
            email,
            "encodedPassword",
            null  // Role
        );
        user.setId(1L);
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(token);
        
        // When
        String result = userService.loginUser(email, password);
        
        // Then
        assertThat(result).isEqualTo(token);
        
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, user.getPassword());
        verify(jwtService).generateToken(user);
    }
    
    @Test
    public void testLoginUser_InvalidCredentials() {
        // Given
        String email = "test@example.com";
        String password = "WrongPassword";
        
        User user = new User(
            "Test",
            "User",
            email,
            "encodedPassword",
            null  // Role
        );
        user.setId(1L);
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);
        
        // When/Then
        assertThatThrownBy(() -> userService.loginUser(email, password))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid email or password");
        
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, user.getPassword());
        verifyNoInteractions(jwtService);
    }
    
    @Test
    public void testLoginUser_UserNotFound() {
        // Given
        String email = "nonexistent@example.com";
        String password = "Password123";
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> userService.loginUser(email, password))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid email or password");
        
        verify(userRepository).findByEmail(email);
        verifyNoInteractions(passwordEncoder, jwtService);
    }
}