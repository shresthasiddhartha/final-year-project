package Zest.gym.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Zest.gym.model.MembershipOwned;
@Repository
public interface MembershipOwnedRepository extends JpaRepository<MembershipOwned, Integer> {
	
	
	List<MembershipOwned> findByEmail(String email);
	
	
	boolean existsByEmail(String email);

}
