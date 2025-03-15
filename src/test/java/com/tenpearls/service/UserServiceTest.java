package com.tenpearls.service;

import com.tenpearls.base.BaseTest;
import com.tenpearls.model.User;
import com.tenpearls.repository.UserRepository;
import com.tenpearls.security.JwtService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for UserService.
 * Extends BaseTest to integrate with Log4j2 and ExtentReports.
 */
public class UserServiceTest extends BaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    private UserService userService;

    private AutoCloseable closeable;

    @BeforeMethod
    public void setUp() {
        logger.info("Setting up UserServiceTest");
        closeable = MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder, jwtService, authenticationManager);
        logger.debug("UserServiceTest setup completed");
    }
    
    @AfterMethod
    public void tearDown() throws Exception {
        logger.info("Tearing down UserServiceTest");
        closeable.close();
        logger.debug("UserServiceTest teardown completed");
    }

    @Test
    public void testRegisterUser_Success() {
        logger.info("Testing registerUser_Success");
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
        logger.debug("registerUser_Success test completed");
    }
    
    @Test
    public void testRegisterUser_ExistingEmail() {
        logger.info("Testing registerUser_ExistingEmail");
        // Given
        String email = "existing@example.com";
        String password = "Password123";
        String firstName = "Existing";
        String lastName = "User";
        
        when(userRepository.existsByEmail(email)).thenReturn(true);
        
        // When/Then
        assertThatThrownBy(() -> userService.registerUser(firstName, lastName, email, password))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Email already registered");
        
        verify(userRepository).existsByEmail(email);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
        logger.debug("registerUser_ExistingEmail test completed");
    }
    
    @Test
    public void testRegisterUser_InvalidEmail() {
        logger.info("Testing registerUser_InvalidEmail");
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
        logger.debug("registerUser_InvalidEmail test completed");
    }
    
    @Test
    public void testRegisterUser_WeakPassword() {
        logger.info("Testing registerUser_WeakPassword");
        // Given
        String email = "test@example.com";
        String password = "weak";
        String firstName = "Weak";
        String lastName = "Password";
        
        when(userRepository.existsByEmail(email)).thenReturn(false);
        
        // When/Then
        assertThatThrownBy(() -> userService.registerUser(firstName, lastName, email, password))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Password must be at least 8 characters and contain at least one digit, one lowercase, and one uppercase letter");
        
        verify(userRepository).existsByEmail(email);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
        logger.debug("registerUser_WeakPassword test completed");
    }
    
    @Test
    public void testLoginUser_Success() {
        logger.info("Testing loginUser_Success");
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
        
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtService.generateToken(user)).thenReturn(token);
        
        // When
        String result = userService.loginUser(email, password);
        
        // Then
        assertThat(result).isEqualTo(token);
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(user);
        logger.debug("loginUser_Success test completed");
    }
    
    @Test
    public void testLoginUser_InvalidCredentials() {
        logger.info("Testing loginUser_InvalidCredentials");
        // Given
        String email = "test@example.com";
        String password = "WrongPassword";
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new IllegalArgumentException("Invalid email or password"));
        
        // When/Then
        assertThatThrownBy(() -> userService.loginUser(email, password))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid email or password");
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtService);
        logger.debug("loginUser_InvalidCredentials test completed");
    }
}