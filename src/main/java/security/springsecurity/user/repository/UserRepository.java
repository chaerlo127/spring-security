package security.springsecurity.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import security.springsecurity.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
