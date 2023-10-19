package dio.diospringsecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.provisioning.InMemoryUserDetailsManager;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConf {

  @Autowired
  private SecurityDataService securityService;

  @Autowired
  public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(securityService).passwordEncoder(new BCryptPasswordEncoder());
  }

  @Bean
  SecurityFilterChain configure(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable()).authorizeHttpRequests((authorize) -> {
      authorize.requestMatchers("/").permitAll()
          .requestMatchers(HttpMethod.POST, "/login").permitAll()
          .requestMatchers("/managers").hasAnyRole("MANAGERS")
          .requestMatchers("/users").hasAnyRole("USERS", "MANAGERS")
          .anyRequest().authenticated();
    }).httpBasic(Customizer.withDefaults()); // .formLogin(Customizer.withDefaults());
    return http.build();
  }

  // @Bean
  // public static PasswordEncoder passwordEncoder() {
  // return new BCryptPasswordEncoder();
  // }

  // @Bean
  // public UserDetailsService userDetailsService() {
  // UserDetails user = User.builder()
  // .username("user")
  // .password(passwordEncoder().encode("user123"))
  // .roles("USERS")
  // .build();

  // UserDetails admin = User.builder()
  // .username("admin")
  // .password(passwordEncoder().encode("master123"))
  // .roles("MANAGERS")
  // .build();
  // return new InMemoryUserDetailsManager(user, admin);
  // }
}
