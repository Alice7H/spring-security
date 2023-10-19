package dio.spring.security.jwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dio.spring.security.jwt.dto.Login;
import dio.spring.security.jwt.dto.Sessao;
import dio.spring.security.jwt.service.LoginService;

@RestController
public class LoginController {
  @Autowired
  private LoginService service;

  @PostMapping("/login")
  public Sessao postLogin(@RequestBody Login login) {
    return service.logar(login);
  }
}
