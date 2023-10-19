package dio.spring.security.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dio.spring.security.jwt.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  @Query("SELECT e FROM User e JOIN FETCH e.roles WHERE e.username= (:username)")
  public User findByUsername(@Param("username") String username);

  boolean existsByUsername(String username);

}
