package com.ias.calculadoraApp.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ias.calculadoraApp.entity.ReporteDeServicio;

public interface IReporteDeServicio extends JpaRepository<ReporteDeServicio, Long> {
	
	@Query(value="select * from reporte_de_servicios where id_tecnico = ?1 and  semana = ?2", nativeQuery = true)
	public List<ReporteDeServicio>encontrarTecnicoPorIdAndSemana(String id_tecnico , Integer semana);

}
