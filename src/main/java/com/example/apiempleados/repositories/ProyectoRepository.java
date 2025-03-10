package com.example.apiempleados.repositories;

import com.example.apiempleados.dto.ProyectoConNumeroEmpleadosDTO;
import com.example.apiempleados.entities.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {


    /////////// MÉTODOS DEFINIDOS POR SU NOMBRE ///////////////
    List<Proyecto> findByFechaInicio(LocalDate fecha);
    List<Proyecto> findByNombreLikeOrderByNombreAsc(String nombre);
    List<Proyecto> findByPresupuestoGreaterThan(BigDecimal presupuesto);
    Optional<Proyecto> findFirstByPresupuestoOrderByPresupuestoDesc(BigDecimal presupuesto);

    List<Proyecto> findByDescripcionContainingIgnoreCase(String descripcion);

    Optional<Proyecto> findFirstByDescripcionContainingIgnoreCase(String descripcion);

    Long countByFechaInicio(LocalDate fecha);




    ////////// CONSULTAS PERSONALIZADAS CON @QUERY ////////////
    @Query("SELECT AVG(p.presupuesto) FROM Proyecto p WHERE YEAR(p.fechaInicio) = :anio")
    BigDecimal obtenerPresupuestoMedioPorAnio(@Param("anio") int anio);

//    @Query("SELECT p.nombre, p.presupuesto, SIZE(p.empleados) FROM Proyecto p")
//    List<Object[]> obtenerProyectosConNumeroDeEmpleados();  //No recomendado

    @Query("SELECT new com.example.apiempleados.dto.ProyectoConNumeroEmpleadosDTO(p.nombre, p.presupuesto, SIZE(p.empleados)) FROM Proyecto p")
    List<ProyectoConNumeroEmpleadosDTO> obtenerProyectosConNumeroDeEmpleadosDto();

    @Modifying
    @Transactional
    @Query("UPDATE Proyecto p SET p.estado = :estado")
    void actualizarEstadoDeTodosLosProyectos(@Param("estado") String estado);
}
