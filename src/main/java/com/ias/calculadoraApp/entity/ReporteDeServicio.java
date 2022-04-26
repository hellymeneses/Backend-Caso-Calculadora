package com.ias.calculadoraApp.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.UniqueElements;

@Entity
@Table(name = "reporte_de_servicios")
public class ReporteDeServicio implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotEmpty(message = "no puede estar vacio")
	@Size(min = 10, max = 10, message = "el tamaño tiene que estar entre 10 caracteres")
	@Column(nullable = false)
	private String idTecnico;

	@NotEmpty(message = "no puede estar vacio")
	@Size(min = 3, max = 6, message = "el tamaño tiene que estar entre 3 y 6")
	@Column(nullable = false, unique = true)
	private String idServicio;

	@NotNull
	@Past
	private Date fechaInicio;

	@Past
	@NotNull
	private Date fechaFin;

	@Column(nullable = false)
	private String horaInicio;

	@Column(nullable = false)
	private String horaFin;
private Double minutosTotales;
	private Double horasTotales;
	private Integer semana;
	private Double TotalAPagar;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdTecnico() {
		return idTecnico;
	}

	public void setIdTecnico(String idTecnico) {
		this.idTecnico = idTecnico;
	}

	public String getIdServicio() {
		return idServicio;
	}

	public void setIdServicio(String idServicio) {
		this.idServicio = idServicio;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Double getHorasTotales() {
		return horasTotales;
	}

	public void setHorasTotales(Double horasTotales) {
		this.horasTotales = horasTotales;
	}

	public Integer getSemana() {
		return semana;
	}

	public void setSemana(Integer semana) {
		this.semana = semana;
	}

	public Double getTotalAPagar() {
		return TotalAPagar;
	}

	public void setTotalAPagar(Double totalAPagar) {
		TotalAPagar = totalAPagar;
	}

	public String getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}

	public String getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(String horaFin) {
		this.horaFin = horaFin;
	}

	public Double getMinutosTotales() {
		return minutosTotales;
	}

	public void setMinutosTotales(Double minutosTotales) {
		this.minutosTotales = minutosTotales;
	}

	private static final long serialVersionUID = 1L;

}
