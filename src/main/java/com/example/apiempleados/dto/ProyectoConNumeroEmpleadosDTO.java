package com.example.apiempleados.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProyectoConNumeroEmpleadosDTO {
    private String nombre;
    private BigDecimal presupuesto;
    private int numeroDeEmpleados;
}