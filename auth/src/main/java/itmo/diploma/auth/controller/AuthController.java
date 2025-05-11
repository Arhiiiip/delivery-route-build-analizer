package itmo.diploma.auth.controller;

import itmo.diploma.auth.entity.ServiceUser;
import itmo.diploma.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/validate")
    public boolean validateApiKey(@RequestParam String apiKey) {
        return authService.validateApiKey(apiKey);
    }

    @PostMapping("/user")
    public ServiceUser addUser(@RequestParam String login, @RequestParam String apiKey) {
        return authService.addUser(login, apiKey);
    }

    @PostMapping("/createKey")
    public String createApiKey(@RequestParam String serviceName) {
        return authService.createApiKey(serviceName);
    }
}
