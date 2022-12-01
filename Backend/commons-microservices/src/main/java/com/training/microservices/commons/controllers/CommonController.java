package com.training.microservices.commons.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.training.microservices.commons.services.CommonService;

public class CommonController<E, S extends CommonService<E>> {
	
	@Autowired
	protected S service;
	
	@GetMapping
	public ResponseEntity<?> listAll(){
		return ResponseEntity.ok().body(service.findAll());	
	}
	
	@GetMapping("/paginated")
	public ResponseEntity<?> listAll(Pageable pageable){
		return ResponseEntity.ok().body(service.findAll(pageable));	
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable Long id){
		Optional<E> entity = service.findById(id);
		if(entity.isEmpty()) {
			return ResponseEntity.notFound().build();	
		}
		return ResponseEntity.ok(entity.get());	
	}
	
	@PostMapping
	public ResponseEntity<?> save(@Validated @RequestBody E entity, BindingResult result){
		if	(result.hasErrors()) {
			return this.validate(result);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(entity));	
	}
	
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id){
		service.deleteById(id);
		return ResponseEntity.noContent().build();	
	}
	
	protected ResponseEntity<?> validate(BindingResult result){
		Map<String, Object> errors = new HashMap<>();
		result.getFieldErrors().forEach(e ->{
			errors.put(e.getField(), e.getDefaultMessage());
		});
		return ResponseEntity.badRequest().body(errors);
	}
}
