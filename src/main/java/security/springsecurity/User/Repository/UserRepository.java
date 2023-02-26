package security.springsecurity.User.Repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import security.springsecurity.User.Entity.User;

@Repository
@RequiredArgsConstructor
public interface UserRepository extends JpaRepository<User, Long> {
}
