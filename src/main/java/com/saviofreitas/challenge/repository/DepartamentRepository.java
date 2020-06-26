package com.saviofreitas.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saviofreitas.challenge.model.Departament;

@Repository
public interface DepartamentRepository extends JpaRepository<Departament, Long>{

}
