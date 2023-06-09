package com.example.demo.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Persona;
import com.example.demo.service.IPersonaService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
		RequestMethod.DELETE })
@RequestMapping(path = "/api")
public class Controller {

	@Autowired
	IPersonaService per;

	@GetMapping("/hola-mundo")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		try {
			return String.format("Hello %s!", name);
		} catch (Exception ex) {
			System.out.println(ex);
			return ex.getMessage();
		}
	}

	@GetMapping("/fechas/actual")
	public ResponseEntity<Object> fechaActual() {
		try {
			Map<Object, Object> response = new HashMap<>();
			response.put("message", Instant.now().toString());
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception ex) {
			System.out.println(ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex);
		}
	}

	@GetMapping("/all")
	public ResponseEntity<Object> listAll() {
		try {
			Map<Object, Object> response = new HashMap<>();
			response.put("message", per.getPersonas());
			return ResponseEntity.status(200).body(response);
		} catch (Exception ex) {
			System.out.println(ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex);
		}
	}

	@PostMapping(path = "/personas", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> save(@Valid @RequestBody Persona persona) {
		try {
			Map<Object, Object> response = new HashMap<>();
			if (per.exists(persona.getPkPersona())) {
				response.put("message", "Ya existe un registro con el ID: " + persona.getPkPersona());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			} else {
				per.save(persona);
				response.put("message", "Entidad introducida correctamente");
				return ResponseEntity.status(HttpStatus.CREATED).body(response);
			}
		} catch (Exception ex) {
			System.out.println(ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex);
		}
	}

	@PutMapping(path = "/personas", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> update(@Valid @RequestBody Persona persona) {
		try {
			List<Persona> personaF = per.findById(persona.getPkPersona());
			Map<Object, Object> response = new HashMap<>();

			if (personaF.isEmpty()) {
				response.put("message", "No se encontro registro");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			} else {
				per.save(persona);
				response.put("message", "Entidad actualizada correctamente");
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
			}
		} catch (Exception ex) {
			System.out.println(ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex);
		}
	}

	@DeleteMapping("/personas/{id}")
	public ResponseEntity<Object> delete(@PathVariable long id) {
		try {
			List<Persona> personaF = per.findById(id);
			System.out.println(personaF);
			Map<Object, Object> response = new HashMap<>();

			if (personaF.isEmpty()) {
				response.put("message", "No se encontro registro");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			} else {
				per.deleteByPersona(personaF.get(0));
				response.put("message", "Entidad eliminada correctamente");
				return ResponseEntity.status(HttpStatus.OK).body(response);
			}
		} catch (Exception ex) {
			System.out.println(ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex);
		}
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
		List<Object> errorsMessage = new ArrayList<>();
		Map<String, Object> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String errorMessage = error.getDefaultMessage();
			errorsMessage.add(errorMessage);
		});
		errors.put("Errors", errorsMessage);
		return errors;
	}
}
