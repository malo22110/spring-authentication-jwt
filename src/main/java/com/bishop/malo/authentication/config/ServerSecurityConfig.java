package com.bishop.malo.authentication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import com.bishop.malo.authentication.filter.CustomCorsFilter;
import com.bishop.malo.authentication.service.CustomUserDetailsService;


@Configuration
public class ServerSecurityConfig extends WebSecurityConfigurerAdapter {
 
    // user Service to handle authentication
 	/**
 	 * UserDetailsService is a core interface which loads user-specific data.
 	 */
 	@Autowired
 	protected CustomUserDetailsService userDetailsService;
 	
 
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() 
      throws Exception {
        return super.authenticationManagerBean();
    }
 
 	/**
 	 * The place to configure the authenticationManager Bean.
 	 * @param AuthenticationManagerBuilder
 	 */
 	@Override
 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
 		auth.userDetailsService(userDetailsService);
 		auth.authenticationProvider(authProvider());
 	}	

 	@Autowired
 	private CorsConfig config;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/login").permitAll()
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .and()
            .formLogin().permitAll();
    }
    
    
	/**
	 * authenticationProvider that use our userDetailsService and passwordEncoder Beans.
	 * @return
	 */
	@Bean
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	// type of the password encoding
	/**
	 * our password encoder that give our encoding strategy.
	 * @return PasswordEncoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	 @Bean
	 public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		 return new GrantedAuthorityDefaults("");
	 }
}