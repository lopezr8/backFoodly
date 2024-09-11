package com.rafael.foodly.foodly.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.rafael.foodly.foodly.security.filter.JwtAuthenticationFilter;
import com.rafael.foodly.foodly.security.filter.JwtValidationFilter;

@Configuration
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    AuthenticationManager authenticationManager() throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http.authorizeHttpRequests( (authz) -> authz

        .requestMatchers(HttpMethod.POST, "/validate-token").permitAll()
        

        .requestMatchers(HttpMethod.POST,"/foodly/checkout/rate-finish/{checkoutId}").authenticated()
        .requestMatchers(HttpMethod.POST,"/foodly/checkout/customer/{username}/{score}").hasRole("CHEF")
        .requestMatchers(HttpMethod.POST,"/foodly/checkout/chef/{dishId}/{score}").hasRole("CUSTOMER")
        .requestMatchers(HttpMethod.GET,"/foodly/checkout").hasRole("ADMIN")

        .requestMatchers(HttpMethod.GET,"/foodly/checkout/customer/{username}").hasRole("CUSTOMER")
        .requestMatchers(HttpMethod.GET,"/foodly/checkout/chef/{username}").hasRole("CHEF")

        .requestMatchers(HttpMethod.GET,"/foodly/checkout/{username}/{checkoutId}").hasRole("CUSTOMER")
        .requestMatchers(HttpMethod.POST,"/foodly/checkout/{username}/{dishId}").hasRole("CUSTOMER")
        .requestMatchers(HttpMethod.POST,"/foodly/checkout/finish/{username}/{ckehoutId}").hasRole("CUSTOMER")
        .requestMatchers(HttpMethod.POST,"/foodly/checkout/send/{username}/{ckehoutId}").hasRole("CHEF")

        

        .requestMatchers(HttpMethod.GET,"/foodly/chef/dish").hasRole("CUSTOMER")
        .requestMatchers(HttpMethod.GET,"/foodly/chef/dish/{id}").permitAll()
        .requestMatchers(HttpMethod.GET,"/foodly/chef/dish/list/{username}").permitAll()
        .requestMatchers(HttpMethod.PUT,"/foodly/chef/dish/{username}/{id}").hasRole("CHEF")
        .requestMatchers(HttpMethod.DELETE,"/foodly/chef/dish/{username}/{id}").hasRole("CHEF")
        .requestMatchers(HttpMethod.POST,"/foodly/chef/dish/{username}").hasRole("CHEF")

        .requestMatchers(HttpMethod.POST,"/foodly/users/chef").permitAll() 
        .requestMatchers(HttpMethod.POST,"/foodly/users/customer").permitAll()
        .requestMatchers(HttpMethod.POST,"/foodly/users").hasRole("ADMIN") 
        .requestMatchers(HttpMethod.PUT,"/foodly/users/chef-customer").permitAll() 
        .requestMatchers(HttpMethod.PUT,"/foodly/users/activate/chef/{username}").hasRole("ADMIN") 
        .requestMatchers(HttpMethod.PUT,"/foodly/users/activate/customer/{username}").hasRole("ADMIN") 
        .requestMatchers(HttpMethod.PUT,"/foodly/users/activate/{username}").hasRole("ADMIN")  

        
        .requestMatchers(HttpMethod.GET,"/foodly/users/identification/{id}").permitAll() 
        .requestMatchers(HttpMethod.GET,"/foodly/users/username/{id}").permitAll() 
        .requestMatchers(HttpMethod.GET,"/foodly/users/ROLE_CUSTOMERStatus/{id}").permitAll() 
        .requestMatchers(HttpMethod.GET,"/foodly/users/ROLE_CHEFStatus/{id}").permitAll() 
        

        .requestMatchers(HttpMethod.GET,"/foodly/users").hasRole("ADMIN") 
        .requestMatchers(HttpMethod.GET,"/foodly/users/active").hasRole("ADMIN") 
        .requestMatchers(HttpMethod.GET,"/foodly/users/customer/{username}").permitAll()
        .requestMatchers(HttpMethod.GET,"/foodly/users/chef/{username}").permitAll() 

        .requestMatchers(HttpMethod.GET,"/foodly/users/inactive").hasRole("ADMIN") 
        .requestMatchers(HttpMethod.GET,"/foodly/users/{username}").hasRole("ADMIN") 
        .requestMatchers(HttpMethod.GET,"/foodly/users/chefs").hasRole("ADMIN") 
        .requestMatchers(HttpMethod.GET,"/foodly/users/customers").hasRole("ADMIN") 
        .requestMatchers(HttpMethod.DELETE,"/foodly/users/{username}").hasRole("ADMIN") 
        .requestMatchers(HttpMethod.DELETE,"/foodly/users/chef/{username}").hasRole("ADMIN") 
        .requestMatchers(HttpMethod.DELETE,"/foodly/users/customer/{username}").hasRole("ADMIN") 

        .anyRequest().authenticated())
        .addFilter(new JwtAuthenticationFilter(authenticationManager() ))
        .addFilter(new JwtValidationFilter(authenticationManager()))
        .csrf( config -> config.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(managment -> managment.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*") );
        config.setAllowedMethods( Arrays.asList("GET", "POST", "PUT", "DELETE"));        
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource() ));
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
    }

}
