package dio.spring.security.jwt.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "tab_user")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_user")
  private Integer id;
  @Column(length = 50, nullable = false)
  private String nome;
  @Column(length = 20, nullable = false)
  private String username;
  @Column(length = 100, nullable = false)
  private String password;
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tab_users_roles", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "role_id")
  private List<String> roles = new ArrayList<>();

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

}
