package org.glazweq.eduportal.security.config;
import lombok.AllArgsConstructor;
import org.glazweq.eduportal.appUser.AppUserService;
import org.glazweq.eduportal.security.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig {

    private final AppUserService appUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//    @Autowired
//    private UserDetailsService userDetailsService;
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring().requestMatchers("/static/**");
    }
//    @Bean
//    public static PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(appUserService);
        return provider;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests((authorize) ->

                        authorize
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/main").permitAll()
                                .requestMatchers("/registration").permitAll()
                                .requestMatchers("/registration/save").permitAll()
                                .requestMatchers("/registration/confirm").permitAll()

                                .requestMatchers("/faculties").permitAll()
                                .requestMatchers("/faculties/{facultyAbbreviation}").permitAll()
                                .requestMatchers("/faculties/{facultyAbbreviation}/{specialtyAbbreviation}").permitAll()

//                                TODO: permit only admins
                                .requestMatchers("/faculties/add").permitAll()
                                .requestMatchers("/faculties/delete").permitAll()
                                .requestMatchers("/specialties/add").permitAll()
                                .requestMatchers("/specialties/delete").permitAll()
                                .requestMatchers("/subject/add").permitAll()
                                .requestMatchers("/subject/delete").permitAll()

                ).formLogin(
                        form -> form

                                .loginPage("/login")
                                .loginProcessingUrl("/login")

                                .defaultSuccessUrl("/main")
                                .permitAll()

                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                );
        return http.build();
    }






}