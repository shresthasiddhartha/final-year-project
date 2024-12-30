package Zest.gym.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Zest.gym.model.Diet;
@Repository
public interface DietRepository extends JpaRepository<Diet, Integer> {

	
	List<Diet> findAll();
	
}
