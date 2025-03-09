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

                                .requestMatchers("/").permitAll()
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/error-page").permitAll()

                                .requestMatchers("/registration").permitAll()
                                .requestMatchers("/registration/save").permitAll()
                                .requestMatchers("/registration/confirm").permitAll()
//                                  TODO: optimize this shit
                                .requestMatchers("/faculties").permitAll()
                                .requestMatchers("/specialtiesAll").permitAll()
                                .requestMatchers("/subjectsAll").permitAll()
                                .requestMatchers("/faculties/{facultyAbbreviation}").permitAll()
                                .requestMatchers("/faculties/{facultyAbbr}/{specialtyAbbr}").permitAll()
                                .requestMatchers("/faculties/{facultyAbbr}/{specialtyAbbr}/{subjectAddr}").permitAll()

                                .requestMatchers("/profile").permitAll()


//                                TODO: permit only admins
                                .requestMatchers("/faculties/add").permitAll()
                                .requestMatchers("/faculties/delete").permitAll()

                                .requestMatchers("/specialties/add").permitAll()
                                .requestMatchers("/specialties/delete").permitAll()
                                .requestMatchers("/specialtiesAll/delete").permitAll()
                                .requestMatchers("/subject/add").permitAll()
                                .requestMatchers("/subject/delete").permitAll()
                                .requestMatchers("/subjectsAll/delete").permitAll()
                                .requestMatchers("/subject/add-teacher").permitAll()
                                .requestMatchers("/subject/remove-teacher").permitAll()
                                .requestMatchers("/file/**").permitAll()
                                .requestMatchers("/folders").permitAll()
                                .requestMatchers("/**").permitAll()

                ).formLogin(
                        form -> form

                                .loginPage("/login")
                                .loginProcessingUrl("/login")

                                .defaultSuccessUrl("/faculties")
                                .permitAll()

                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                );
        return http.build();
    }






}