package Zest.gym.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Zest.gym.model.Trainer;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Integer> {

	
	Optional<Trainer> findByEmail(String email);
	
	
}
