package com.example.apiempleados.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "empleados")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String nombre;
    private String apellidos;
    private LocalDate fechaNacimiento;

    @JsonIgnore //Para que Jackson no serielice esta propiedad a JSON y no se forme un buble infinito
    @ManyToOne
    private Departamento departamento;

}
