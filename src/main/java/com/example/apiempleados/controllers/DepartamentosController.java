package com.example.apiempleados.controllers;

import com.example.apiempleados.entities.Departamento;
import com.example.apiempleados.entities.Empleado;
import com.example.apiempleados.repositories.DepartamentoRepository;
import com.example.apiempleados.repositories.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class DepartamentosController {

    @Autowired
    private DepartamentoRepository departamentoRepository;
    @Autowired
    private EmpleadoRepository empleadoRepository;

    // Obtener todos los departamentos
    @GetMapping("/departamentos")
    public ResponseEntity<Page<Departamento>> findAllDepartamentos(@PageableDefault(page = 0, size = 5,sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(departamentoRepository.findAll(pageable));
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
                    departamento.getEmpleados().forEach(empleado -> {empleado.setDepartamento(null);});
                    departamentoRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Agregar un empleado a un departamento
    @PostMapping("/departamentos/{departamentoId}/empleados/{empleadoId}")
    public ResponseEntity<Departamento> addEmpleadoToDepartamento(@PathVariable Long departamentoId, @PathVariable Long empleadoId) {
        Optional<Departamento> departamentoOpt = departamentoRepository.findById(departamentoId);
        Optional<Empleado> empleadoOpt = empleadoRepository.findById(empleadoId);

        if (departamentoOpt.isPresent() && empleadoOpt.isPresent()) {
            Departamento departamento = departamentoOpt.get();
            Empleado empleado = empleadoOpt.get();

            empleado.setDepartamento(departamento);  // Asignar el departamento al empleado
            departamento.getEmpleados().add(empleado);  // Agregar el empleado a la lista

            departamentoRepository.save(departamento);  // Guardar el departamento actualizado.
            empleadoRepository.save(empleado);  // Guardar el empleado con la nueva relación.

            return ResponseEntity.ok(departamento); //Código 200 OK
        }

        return ResponseEntity.notFound().build();   //Código 404 Not Found
    }

    // Eliminar un empleado de un departamento
    @DeleteMapping("/departamentos/{departamentoId}/empleados/{empleadoId}")
    public ResponseEntity<Departamento> removeEmpleadoFromDepartamento(@PathVariable Long departamentoId, @PathVariable Long empleadoId) {
        Optional<Departamento> departamentoOpt = departamentoRepository.findById(departamentoId);
        Optional<Empleado> empleadoOpt = empleadoRepository.findById(empleadoId);

        if (departamentoOpt.isPresent() && empleadoOpt.isPresent()) {
            Departamento departamento = departamentoOpt.get();
            Empleado empleado = empleadoOpt.get();

            if (empleado.getDepartamento() != null && empleado.getDepartamento().getId().equals(departamentoId)) {
                empleado.setDepartamento(null);  // Eliminar la relación con el departamento
                departamento.getEmpleados().remove(empleado);  // Remover de la lista

                empleadoRepository.save(empleado);  // Guardar el cambio en empleado
                departamentoRepository.save(departamento);  // Guardar el cambio en departamento

                return ResponseEntity.ok(departamento);
            }
        }

        return ResponseEntity.notFound().build();
    }
}
