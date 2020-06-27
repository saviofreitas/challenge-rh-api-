package com.saviofreitas.challenge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saviofreitas.challenge.model.Person;
import com.saviofreitas.challenge.repository.PersonRepository;

@RestController
@RequestMapping(value = "/people", produces = "application/json")
@Validated
public class PersonController {
	
	private PersonRepository repository;
	
	@Autowired
	public PersonController(PersonRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public ResponseEntity<List<Person>> findAll() {
		return new ResponseEntity<List<Person>>(repository.findAll(), new HttpHeaders(), HttpStatus.OK);
	}

	@GetMapping(path = { "/{id}" })
	public ResponseEntity<Person> findById(@PathVariable long id) {
		return repository.findById(id).map(record -> ResponseEntity.ok().body(record))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping(path = { "/departament/{departamentId}" })
	public ResponseEntity<List<Person>> findByDepartamentId(@PathVariable (value = "departamentId") Long departamentId) {
		return new ResponseEntity<List<Person>>(repository.findByDepartamentId(departamentId), new HttpHeaders(), HttpStatus.OK);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Person create(@RequestBody Person departament) {
		return repository.save(departament);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<Person> update(@PathVariable("id") long id, @RequestBody Person person) {
		return repository.findById(id).map(record -> {
			record.setFirstName(person.getFirstName());
			record.setLastName(person.getLastName());
			record.setEmail(person.getEmail());
			record.setDepartament(person.getDepartament());
			Person updated = repository.save(record);
			return ResponseEntity.ok().body(updated);
		}).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping(path = { "/{id}" })
	public ResponseEntity<?> delete(@PathVariable long id) {
		return repository.findById(id).map(record -> {
			repository.deleteById(id);
			return ResponseEntity.ok().build();
		}).orElse(ResponseEntity.notFound().build());
	}
}
