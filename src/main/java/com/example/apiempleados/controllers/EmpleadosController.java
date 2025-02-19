package com.example.apiempleados.controllers;

import com.example.apiempleados.entities.Empleado;
import com.example.apiempleados.repositories.EmpleadoRepository;
import org.hibernate.usertype.StaticUserTypeSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class EmpleadosController {

    @Autowired
    EmpleadoRepository empleadoRepository;

    @GetMapping("/empleados")
    public ResponseEntity<List<Empleado>> findAllEmpleados(){
        return ResponseEntity.ok(empleadoRepository.findAll());
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
    public ResponseEntity<Empleado> createEmpleado(@RequestBody Empleado empleado){
        //Una mejora posible sería retornar la URI del recurso creado.
        //Lo simplificamos por el momento devolviendo el código 201(created) y el empleado creado en el cuerpo
        return ResponseEntity.status(301).body(empleadoRepository.save(empleado));
    }

    @PutMapping("/empleados/{id}")
    public ResponseEntity<Empleado> updateEmpleado(@RequestBody Empleado empleadoNuevo, @PathVariable Long id){
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
