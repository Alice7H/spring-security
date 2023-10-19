package dio.spring.security.jwt.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import dio.spring.security.jwt.dto.Login;
import dio.spring.security.jwt.dto.Sessao;
import dio.spring.security.jwt.model.User;
import dio.spring.security.jwt.repository.UserRepository;
import dio.spring.security.jwt.security.JWTCreator;
import dio.spring.security.jwt.security.JWTObject;
import dio.spring.security.jwt.security.SecurityConfig;

@Service
public class LoginService {
  @Autowired
  private UserRepository repository;
  @Autowired
  private PasswordEncoder encoder;

  public Sessao logar(@RequestBody Login login) {
    User user = repository.findByUsername(login.getUsername());
    if (user != null) {
      boolean passwordOk = encoder.matches(login.getPassword(), user.getPassword());
      if (!passwordOk) {
        throw new RuntimeException("Senha inválida para login" + login.getUsername());
      }

      Sessao sessao = new Sessao();
      sessao.setLogin(user.getUsername());

      JWTObject jwtObject = new JWTObject();
      jwtObject.setIssuedAt(new Date(System.currentTimeMillis()));
      jwtObject.setExpiration((new Date(System.currentTimeMillis() + SecurityConfig.EXPIRATION)));
      jwtObject.setRoles(user.getRoles());
      sessao.setToken(JWTCreator.create(SecurityConfig.PREFIX, SecurityConfig.KEY, jwtObject));
      return sessao;
    } else {
      throw new RuntimeException("Erro ao tentar fazer login");
    }
  }
}
