package com.aspmj.sorteio.config

import com.aspmj.sorteio.service.AppUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
@EnableWebSecurity
class SpringSecurityConfig(private val appUserDetailsService: AppUserDetailsService) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {

        http?.authorizeRequests()
                ?.antMatchers(
                    "/login",
                    "/participantes",
                    "/participantes/**",
                    "/sorteios/participar",
                    "/sorteios/participar/**",
                )
                ?.permitAll()
            ?.anyRequest()
                ?.authenticated()
            ?.and()
            ?.formLogin()
                ?.loginPage("/login")
                ?.permitAll()
            ?.and()
            ?.logout()
                ?.permitAll()
    }

    override fun configure(web: WebSecurity?) {
        web?.ignoring()?.antMatchers(
            "/js/**",
            "/css/**",
            "/images/**",
            "/locales/**",
            "/webfontes/**",
            "/h2-console",
            "/h2-console/**",
            "/favicon.ico",
            "/**/favicon.ico"
        )
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.userDetailsService(appUserDetailsService)
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}