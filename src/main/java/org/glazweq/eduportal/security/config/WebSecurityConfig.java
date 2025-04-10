package org.glazweq.eduportal.security.config;
import lombok.AllArgsConstructor;
import org.glazweq.eduportal.appUser.user.AppUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig  {

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

        http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) ->

                        authorize


                                .requestMatchers("/login/**").permitAll()
                                .requestMatchers("/error-page").permitAll()

                                .requestMatchers("/registration").permitAll()
                                .requestMatchers("/registration/save").permitAll()
                                .requestMatchers("/registration/confirm").permitAll()


                                .requestMatchers("/profile").authenticated()
                                .requestMatchers("/teachers").authenticated()
                                .requestMatchers("/folders/**").authenticated()
                                .requestMatchers("/folders/add").hasAnyAuthority("ADMIN", "TEACHER")
                                .requestMatchers("/course/add-teacher").hasAnyAuthority("ADMIN", "TEACHER")
                                .requestMatchers("/course/remove-teacher").hasAnyAuthority("ADMIN", "TEACHER")
                                .requestMatchers("/admin/licenseRequests").hasAnyAuthority("ADMIN", "TEACHER")
                                .requestMatchers("/admin/add-license").hasAnyAuthority("ADMIN", "TEACHER")
                                .requestMatchers("/admin/remove-license").hasAnyAuthority("ADMIN", "TEACHER")
                                .requestMatchers("/admin/delete-license").hasAnyAuthority("ADMIN")
                                .requestMatchers("/course/add").hasAnyAuthority("ADMIN", "TEACHER")
                                .requestMatchers("/course/delete").hasAnyAuthority("ADMIN", "TEACHER")
                                .requestMatchers("/file/delete").hasAnyAuthority("ADMIN", "TEACHER")
                                .requestMatchers("/file/upload").hasAnyAuthority("ADMIN", "TEACHER")
                                .requestMatchers("/file/download/").authenticated()
                                .requestMatchers("/file/view/").authenticated()
                                .requestMatchers("/coursesAll").authenticated()
                                .requestMatchers("/teachers").authenticated()
                                .requestMatchers("/licenseRequests").authenticated()
                                .requestMatchers("/teacher/**").authenticated()
//                                TODO: permit only admins

                                .requestMatchers("/file/**").permitAll()
                                .requestMatchers("/folders").permitAll()
                                .requestMatchers("/course/add").permitAll()



                ).formLogin(
                        form -> form

                                .loginPage("/login")
                                .loginProcessingUrl("/login")

                                .defaultSuccessUrl("/folders")
                                .permitAll()

                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                );
        return http.build();
    }






}