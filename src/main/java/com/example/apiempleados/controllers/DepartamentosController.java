package com.example.apiempleados.controllers;

import com.example.apiempleados.entities.Departamento;
import com.example.apiempleados.repositories.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DepartamentosController {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    // Obtener todos los departamentos
    @GetMapping("/departamentos")
    public ResponseEntity<List<Departamento>> findAllDepartamentos() {
        return ResponseEntity.ok(departamentoRepository.findAll());
    }

    // Obtener un departamento por ID
    @GetMapping("/departamentos/{id}")
    public ResponseEntity<Departamento> findDepartamento(@PathVariable Long id) {
        return departamentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo departamento
    //Una mejora posible sería retornar la URI del recurso creado.
    //Lo simplificamos por el momento devolviendo el código 201(created) y el empleado creado en el cuerpo
    @PostMapping("/departamentos")
    public ResponseEntity<Departamento> createDepartamento(@RequestBody Departamento departamento) {
        return ResponseEntity.status(201).body(departamentoRepository.save(departamento));
    }

    // Actualizar un departamento existente
    @PutMapping("/departamentos/{id}")
    public ResponseEntity<Departamento> updateDepartamento(@RequestBody Departamento departamentoNuevo, @PathVariable Long id) {
        return departamentoRepository.findById(id)
                .map(departamento -> {
                    departamento.setNombre(departamentoNuevo.getNombre());
                    return ResponseEntity.ok(departamentoRepository.save(departamento));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar un departamento por ID
    @DeleteMapping("/departamentos/{id}")
    public ResponseEntity<Object> deleteDepartamento(@PathVariable Long id) {
        return departamentoRepository.findById(id)
                .map(departamento -> {
                    departamentoRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
