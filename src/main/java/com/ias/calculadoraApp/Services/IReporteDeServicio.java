package com.ias.calculadoraApp.Services;

import java.util.List;

import com.ias.calculadoraApp.dtos.HorasDineroDto;
import com.ias.calculadoraApp.entity.ReporteDeServicio;

public interface IReporteDeServicio {

	public List<ReporteDeServicio> findAll();

	public ReporteDeServicio findById(Long id);

	public ReporteDeServicio save(ReporteDeServicio reporteDeServicio);

	public void delete(Long id);
		
	public List<ReporteDeServicio>encontrarTecnicoPorIdAndSemana(String id_tecnico , Integer semana);
	
	public HorasDineroDto valorAPagar (String id_tecnico , Integer semana);
}
