package com.bishop.malo.authentication.config;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	
	
	private static final String RESOURCE_ID = "blog_resource";
	
	/**
	 * token validity in seconds.
	 */
	private int accessTokenValiditySeconds = 10000;
	
	/**
	 * token refresh validity in seconds.
	 */
	private int refreshTokenValiditySeconds = 30000;
	
	
	/**
	 * AuthenticationManager Bean.
	 */
	@Autowired
	private AuthenticationManager authenticationManager;
	
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
	    tokenEnhancerChain.setTokenEnhancers(
	      Arrays.asList(tokenEnhancer(), accessTokenConverter()));
	 
	    endpoints.tokenStore(tokenStore())
	             .tokenEnhancer(tokenEnhancerChain)
	             .tokenServices(tokenServices())
	             .authenticationManager(authenticationManager);
//		endpoints
//		.authenticationManager(this.authenticationManager)
//		.tokenServices(tokenServices())
//		.tokenStore(tokenStore())
//		.accessTokenConverter(accessTokenConverter());
	}
	
	
	/**
	 * Configure the security of the Authorization Server,
	 *  which means in practical terms the /oauth/token endpoint.
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		// allowing access to the token only for clients with 'ROLE_TRUSTED_CLIENT' authority
		 security
         .tokenKeyAccess("permitAll()")
         .checkTokenAccess("isAuthenticated()")
         .allowFormAuthenticationForClients();
	}
	
	
	  @Override // [3]
      public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
           // @formatter:off
           clients.inMemory()
           .withClient("trusted-app")
           .authorizedGrantTypes("authorization_code")
           .authorities("ROLE_CLIENT")
           .scopes("read", "trust")
           .resourceIds(RESOURCE_ID)
           .redirectUris("http://anywhere?key=value")
           .secret("secret")
           .and()
           .withClient("trusted-app")
           .authorizedGrantTypes("client_credentials", "password")
           .authorities("ROLE_CLIENT")
           .scopes("read")
           .resourceIds(RESOURCE_ID)
           .secret("secret");
      } 

	@Bean
	public TokenEnhancer tokenEnhancer() {
		return new CustomTokenEnhancer();
	}
	
	
	/**
	 * 
	 * @return TokenStore
	 */
	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	/**
	 * give the JWT acces token converter with our secret key configured for the private attribute signingkey.
	 * run into /src/main/resources/ : keytool -genkeypair -alias mykeys -keyalg RSA -keypass my
	 * pass -keystore mykeys.jks -storepass mypass
	 * @return JwtAccessTokenConverter
	 */
	
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setSigningKey("123");
		return converter;
	}
	

	/**
	 * 
	 * @return DefaultTokenServices
	 */
	@Bean
	@Primary
	public DefaultTokenServices tokenServices() {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		defaultTokenServices.setSupportRefreshToken(true);
		defaultTokenServices.setTokenEnhancer(accessTokenConverter());
		return defaultTokenServices;
	}
}
