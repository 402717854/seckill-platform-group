package com.seckill.platform.auth.config;

import com.seckill.platform.auth.service.impl.UserServiceImpl;
import com.seckill.platform.common.constant.AuthConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

/**
 * 认证服务器配置
 * wys
 */
@Configuration
@EnableAuthorizationServer
public class Oauth2ServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private UserServiceImpl userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private TokenStore tokenStore;

    @Bean
    public AuthorizationCodeServices authorizationCodeServices(){
        return new JdbcAuthorizationCodeServices(dataSource);
//        return new InMemoryAuthorizationCodeServices();
    }
    @Bean
    public ClientDetailsService clientDetailsService() {
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        clientDetailsService.setPasswordEncoder(passwordEncoder);
        return clientDetailsService;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //用来配置客户端详情服务
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.inMemory()
//                .withClient(AuthConstant.ADMIN_CLIENT_ID)
//                .secret(passwordEncoder.encode("123456"))
//                .scopes("all")
//                .authorizedGrantTypes("password", "refresh_token")
//                .accessTokenValiditySeconds(3600*24)
//                .refreshTokenValiditySeconds(3600*24*7)
//                .and()
//                .withClient(AuthConstant.PORTAL_CLIENT_ID)
//                .secret(passwordEncoder.encode("123456"))
//                .scopes("all")
//                .authorizedGrantTypes("password", "refresh_token")
//                .accessTokenValiditySeconds(3600*24)
//                .refreshTokenValiditySeconds(3600*24*7);
        clients.withClientDetails(clientDetailsService());
    }

    @Bean
    public AuthorizationServerTokenServices tokenServices(){
        DefaultTokenServices services = new DefaultTokenServices();
        services.setClientDetailsService(clientDetailsService);
        services.setSupportRefreshToken(true);
        services.setReuseRefreshToken(false);
        services.setTokenStore(tokenStore);//指定token存储到相关存储组件
        /**
         * 无效的时间配置，token的有效期优先选择数据库中客户端的配置，如需修改，修改表oauth_client_details中的配置
         * @see DefaultTokenServices#getAccessTokenValiditySeconds(org.springframework.security.oauth2.provider.OAuth2Request)
         */
//        services.setAccessTokenValiditySeconds(10);//有效期10秒
//        services.setRefreshTokenValiditySeconds(30);//有效期30秒
        return services;
    }
    /**
     * 用来配置令牌（token）的访问端点和令牌服务(tokenservices)
     * AuthorizationServerEndpointsConfigurer端点配置
     * AuthorizationServerEndpointsConfigurer其实是一个装载类，装载Endpoints所有相关的类配置
     * （AuthorizationServer、TokenServices、TokenStore、ClientDetailsService、UserDetailsService）。
     * @param endpoints the endpoints configurer
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager)//使用密码模式需要配置
                .authorizationCodeServices(authorizationCodeServices())
                .tokenServices(tokenServices())
                .userDetailsService(userDetailsService) //配置加载用户信息的服务 //刷新令牌授权包含对用户信息的检查
                .allowedTokenEndpointRequestMethods(HttpMethod.GET,HttpMethod.POST); //支持GET,POST请求
    }

    /**
     *  AuthorizationServerSecurityConfigurer继承SecurityConfigurerAdapter.
     *  也就是一个 Spring Security安全配置提供给AuthorizationServer去配置AuthorizationServer的端点（/oauth/****）的安全访问规则、过滤器Filter。
     * @param security a fluent configurer for security features
     * @throws Exception
     */
    /**
     * 用来配置令牌端点的安全约束.
     *  配置：安全检查流程,用来配置令牌端点（Token Endpoint）的安全与权限访问
     *  默认过滤器：BasicAuthenticationFilter
     *  1、oauth_client_details表中clientSecret字段加密【ClientDetails属性secret】
     *  2、CheckEndpoint类的接口 oauth/check_token 无需经过过滤器过滤，默认值：denyAll()
     * 对以下的几个端点进行权限配置：
     * /oauth/authorize：授权端点
     * /oauth/token：令牌端点
     * /oauth/confirm_access：用户确认授权提交端点
     * /oauth/error：授权服务错误信息端点
     * /oauth/check_token：用于资源服务访问的令牌解析端点
     * /oauth/token_key：提供公有密匙的端点，如果使用JWT令牌的话
     **/
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")// 开启/oauth/token_key验证端口无权限访问
                .checkTokenAccess("isAuthenticated()") // 开启/oauth/check_token验证端口认证权限访问
                .allowFormAuthenticationForClients();//允许客户表单认证
    }
}
