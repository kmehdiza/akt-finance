package az.ingress.user.management.repository;

import az.ingress.user.management.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"authorities", "partner"})
    @Query("select u from User u where lower(u.username)=lower(:username)")
    Optional<User> findOneWithAuthoritiesAndPartnerByUsername(@Param("username") String login);

    Optional<User> findUserByUsernameIgnoreCase(String username);

}
