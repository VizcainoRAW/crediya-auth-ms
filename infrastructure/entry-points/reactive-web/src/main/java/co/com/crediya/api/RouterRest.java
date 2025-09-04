package co.com.crediya.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/users"), handler::createUser)
                .andRoute(POST("/api/auth/login"), handler::authenticateUser)
                .andRoute(GET("/api/users"), handler::getAllUsers)
                .andRoute(GET("/api/users/{id}"), handler::getUserById)
                .andRoute(GET("/api/users/search"), handler::getUserByEmail)
                .andRoute(GET("/api/users/exists"), handler::checkUserExists)
                .andRoute(GET("/api/users/{id}/exists"), handler::checkUserExistsById)
                .andRoute(GET("/api/users/exists/by-id"), handler::checkUserExistsByIdQuery);
        }
}
