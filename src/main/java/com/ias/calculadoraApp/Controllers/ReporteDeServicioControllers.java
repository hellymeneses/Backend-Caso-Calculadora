package com.ias.calculadoraApp.Controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ias.calculadoraApp.Services.IReporteDeServicio;
import com.ias.calculadoraApp.dtos.HorasDineroDto;
import com.ias.calculadoraApp.entity.ReporteDeServicio;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class ReporteDeServicioControllers {

	@Autowired

	private IReporteDeServicio reporteService;

	@GetMapping("/reportes-de-servicios")
	public List<ReporteDeServicio> index() {
		return reporteService.findAll();
	}

	@GetMapping("/reportes-de-servicios/{id}")
	public ReporteDeServicio show(@PathVariable Long id) {
		return reporteService.findById(id);

	}

	@PostMapping("/reportes-de-servicios")
	public ReporteDeServicio create(@RequestBody ReporteDeServicio reporteDeServicio) {
		return reporteService.save(reporteDeServicio);

	}

	@PutMapping("/reportes-de-servicios/{id}")
	public ReporteDeServicio update(@RequestBody ReporteDeServicio reporteDeServicio, @PathVariable Long id) {
		ReporteDeServicio reporteDeServicioActual = reporteService.findById(id);

		reporteDeServicioActual.setIdServicio(reporteDeServicio.getIdServicio());
		reporteDeServicioActual.setIdTecnico(reporteDeServicio.getIdTecnico());
		reporteDeServicioActual.setFechaInicio(reporteDeServicio.getFechaInicio());
		reporteDeServicioActual.setFechaFin(reporteDeServicio.getFechaFin());

		return reporteService.save(reporteDeServicioActual);
	}

	@DeleteMapping("/reportes-de-servicios/{id}")
	public void delete(@PathVariable Long id) {
		reporteService.delete(id);
	}

	@GetMapping("/reportes-de-servicios/idTecAndSemana/{id_tecnico}/{semana}")
	public List<ReporteDeServicio> idTecnicoAndSemana(@PathVariable String id_tecnico, @PathVariable Integer semana) {
		System.out.println("parametros " + id_tecnico + " " + semana);
		return reporteService.encontrarTecnicoPorIdAndSemana(id_tecnico, semana);
	}

	@GetMapping("/reportes-de-servicios/valorAPagar/{id_tecnico}/{semana}")
	public HorasDineroDto valorporhoras(@PathVariable String id_tecnico, @PathVariable Integer semana) {

		return reporteService.valorAPagar(id_tecnico, semana);
	}
}
