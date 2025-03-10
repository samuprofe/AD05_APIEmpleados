package com.example.apiempleados.controllers;

import com.example.apiempleados.entities.Empleado;
import com.example.apiempleados.repositories.EmpleadoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class EmpleadosController {

    @Autowired
    EmpleadoRepository empleadoRepository;

    @GetMapping("/empleados")
    public ResponseEntity<Page<Empleado>> findAllEmpleados(@PageableDefault(page = 0, size = 5, sort = "apellidos", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(empleadoRepository.findAll(pageable));
    }

    @GetMapping("/empleados/{id}")
    public ResponseEntity<Empleado> findEmpleado(@PathVariable Long id){
        return empleadoRepository.findById(id)
                .map(empleado -> ResponseEntity.ok(empleado))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/empleados/{id}")
    public ResponseEntity<Object> deleteEmpleado(@PathVariable Long id){
        return empleadoRepository.findById(id)
                .map(empleado -> {
                    empleadoRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() ->{
                    return  ResponseEntity.notFound().build();
                });

//         Optional<Empleado> empleadOptl = empleadoRepository.findById(id);
//         if (empleadOptl.isPresent()){
//             empleadoRepository.deleteById(id);
//             return ResponseEntity.noContent().build();
//         }
//         else{
//             return  ResponseEntity.notFound().build();
//         }

    }

    @PostMapping("/empleados")
    public ResponseEntity<?> createEmpleado(@Valid @RequestBody Empleado empleado){
        //Una mejora posible sería retornar la URI del recurso creado.
        //Lo simplificamos por el momento devolviendo el código 201(created) y el empleado creado en el cuerpo
        try {
            return ResponseEntity.status(301).body(empleadoRepository.save(empleado));
        }catch (DataIntegrityViolationException e){
            return ResponseEntity.badRequest().body("La dirección de email ya existe en la base de datos");
        }
    }

    @PutMapping("/empleados/{id}")
    public ResponseEntity<Empleado> updateEmpleado(@Valid @RequestBody Empleado empleadoNuevo, @PathVariable Long id){
        Optional<Empleado> empleado = empleadoRepository.findById(id);
        if(empleado.isPresent()){
            empleado.get().setApellidos(empleadoNuevo.getApellidos());
            empleado.get().setEmail(empleadoNuevo.getEmail());
            empleado.get().setNombre(empleadoNuevo.getNombre());
            empleado.get().setFechaNacimiento(empleadoNuevo.getFechaNacimiento());
            empleadoRepository.save(empleado.get());
            return ResponseEntity.ok(empleado.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }
}
