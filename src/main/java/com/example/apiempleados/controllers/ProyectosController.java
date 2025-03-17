package com.example.apiempleados.controllers;

import com.example.apiempleados.dto.ProyectoConNumeroEmpleadosDTO;
import com.example.apiempleados.entities.Proyecto;
import com.example.apiempleados.entities.Empleado;
import com.example.apiempleados.repositories.ProyectoRepository;
import com.example.apiempleados.repositories.EmpleadoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ProyectosController {

    @Autowired
    private ProyectoRepository proyectoRepository;
    @Autowired
    private EmpleadoRepository empleadoRepository;

    // Obtener todos los proyectos
    @GetMapping("/proyectos")
    public ResponseEntity<Page<Proyecto>> findAllProyectos(@PageableDefault(page = 0, size = 5,sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(proyectoRepository.findAll(pageable));
    }

    // Obtener un proyecto por ID
    @GetMapping("/proyectos/{id}")
    public ResponseEntity<Proyecto> findProyecto(@PathVariable Long id) {
        return proyectoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo proyecto
    @PostMapping("/proyectos")
    public ResponseEntity<Proyecto> createProyecto(@Valid @RequestBody Proyecto proyecto) {
        return ResponseEntity.status(201).body(proyectoRepository.save(proyecto));
    }

    // Actualizar un proyecto existente
    @PutMapping("/proyectos/{id}")
    public ResponseEntity<Proyecto> updateProyecto(@Valid @RequestBody Proyecto proyectoNuevo, @PathVariable Long id) {
        return proyectoRepository.findById(id)
                .map(proyecto -> {
                    proyecto.setNombre(proyectoNuevo.getNombre());
                    proyecto.setFechaInicio(proyectoNuevo.getFechaInicio());
                    proyecto.setDescripcion(proyectoNuevo.getDescripcion());
                    proyecto.setEstado(proyectoNuevo.getEstado());
                    proyecto.setPresupuesto(proyectoNuevo.getPresupuesto());
                    return ResponseEntity.ok(proyectoRepository.save(proyecto));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar un proyecto por ID
    @DeleteMapping("/proyectos/{id}")
    public ResponseEntity<Object> deleteProyecto(@PathVariable Long id) {
        return proyectoRepository.findById(id)
                .map(proyecto -> {
                    //Borro relaciones con empleados
                    proyecto.getEmpleados().forEach(empleado -> {empleado.getProyectos().remove(proyecto);});
                    proyecto.getEmpleados().clear();

                    proyectoRepository.delete(proyecto);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Agregar un empleado a un proyecto
    @PostMapping("/proyectos/{proyectoId}/empleados/{empleadoId}")
    public ResponseEntity<Proyecto> addEmpleadoToProyecto(@PathVariable Long proyectoId, @PathVariable Long empleadoId) {
        Optional<Proyecto> proyectoOpt = proyectoRepository.findById(proyectoId);
        Optional<Empleado> empleadoOpt = empleadoRepository.findById(empleadoId);

        if (proyectoOpt.isPresent() && empleadoOpt.isPresent()) {
            Proyecto proyecto = proyectoOpt.get();
            Empleado empleado = empleadoOpt.get();

            //TODO Comprobar que no exista ya el empleado en el proyecto
            if(!proyecto.getEmpleados().contains(empleado)) {
                proyecto.getEmpleados().add(empleado);  // Agregar el empleado a la lista del proyecto
            }

            proyectoRepository.save(proyecto);  // Guardar el proyecto actualizado

            return ResponseEntity.ok(proyecto); //Código 200 OK
        }

        return ResponseEntity.notFound().build();   //Código 404 Not Found
    }

    // Eliminar un empleado de un proyecto
    @DeleteMapping("/proyectos/{proyectoId}/empleados/{empleadoId}")
    public ResponseEntity<Proyecto> removeEmpleadoFromProyecto(@PathVariable Long proyectoId, @PathVariable Long empleadoId) {
        Optional<Proyecto> proyectoOpt = proyectoRepository.findById(proyectoId);
        Optional<Empleado> empleadoOpt = empleadoRepository.findById(empleadoId);

        if (proyectoOpt.isPresent() && empleadoOpt.isPresent()) {
            Proyecto proyecto = proyectoOpt.get();
            Empleado empleado = empleadoOpt.get();

            if (proyecto.getEmpleados().contains(empleado)) {
                proyecto.getEmpleados().remove(empleado);  // Remover de la lista

                proyectoRepository.save(proyecto);  // Guardar el cambio en proyecto

                return ResponseEntity.ok(proyecto);
            }
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/proyectos/resumen")
    public ResponseEntity<?> obtenerProyectosResumen() {

        List<ProyectoConNumeroEmpleadosDTO> proyectos = proyectoRepository.obtenerProyectosConNumeroDeEmpleadosDto();
        return ResponseEntity.ok(proyectos);


//        List<Proyecto> proyectos = proyectoRepository.findAll();
//        List<ProyectoConNumeroEmpleadosDTO> proyectosConNumeroEmplepados= new ArrayList<ProyectoConNumeroEmpleadosDTO>();
//        proyectos.forEach(proyecto -> {
//            ProyectoConNumeroEmpleadosDTO proyectoDTO = new ProyectoConNumeroEmpleadosDTO(proyecto.getNombre(), proyecto.getPresupuesto(), proyecto.getEmpleados().size());
//            proyectosConNumeroEmplepados.add(proyectoDTO);
//        });
//        return ResponseEntity.ok(proyectosConNumeroEmplepados);


    }
}
