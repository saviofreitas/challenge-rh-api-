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

import com.saviofreitas.challenge.model.Departament;
import com.saviofreitas.challenge.repository.DepartamentRepository;

@RestController
@RequestMapping(value = "/departaments", produces = "application/json")
@Validated
public class DepartamentController {

	private DepartamentRepository repository;
	
	@Autowired
	public DepartamentController(DepartamentRepository repository) {
		this.repository = repository;
	}
	
	@GetMapping
	public ResponseEntity<List<Departament>> findAll() {
		return new ResponseEntity<List<Departament>>(repository.findAll(), new HttpHeaders(), HttpStatus.OK);
	}

	@GetMapping(path = { "/{id}" })
	public ResponseEntity<Departament> findById(@PathVariable long id) {
		return repository.findById(id).map(record -> ResponseEntity.ok().body(record))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Departament create(@RequestBody Departament departament) {
		return repository.save(departament);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<Departament> update(@PathVariable("id") long id, @RequestBody Departament departament) {
		return repository.findById(id).map(record -> {
			record.setDescription(departament.getDescription());
			Departament updated = repository.save(record);
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
