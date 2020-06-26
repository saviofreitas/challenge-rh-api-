package com.saviofreitas.challenge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saviofreitas.challenge.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{

	List<Person> findByDepartamentId(Long departamentId);
}
