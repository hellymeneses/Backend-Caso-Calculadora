package com.ias.calculadoraApp.Services;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ias.calculadoraApp.dtos.HorasDineroDto;
import com.ias.calculadoraApp.entity.ReporteDeServicio;

@Service
public class ReporteDeServicioImple implements IReporteDeServicio {

	Double horasTotales = (double) 0;
	Double valor = (double) 0;
	Double totalNocturnas = (double) 0;
	Double minutosNocturnas = (double) 0;
	Double totalDiurnas = (double) 0;
	Double minutosDiurnas = (double) 0;
	Double totalExtraDiurnas = (double) 0;
	Double minutosExtraDiurnas = (double) 0;
	Double totalExtraNocturnas = (double) 0;
	Double minutosExtraNocturnas = (double) 0;
	Double totalDominicales = (double) 0;
	Double minutosDominicales = (double) 0;
	Double totalExtraDominicales = (double) 0;
	Double minutosExtraDominicales = (double) 0;
	Double totalAPagarHorasNocturnas = (double) 0;
	Double totalAPagarHorasDiurnas = (double) 0;
	Double totalAPagarHorasDominicales = (double) 0;
	Double totalAPagarHorasExtraDominicales = (double) 0;
	Double totalAPagarHorasExtraDiurnas = (double) 0;
	Double totalAPagarHorasExtraNocturnas = (double) 0;
	Double horasTotalesAcomuladas = (double) 0;
	Double minutosCumplidosPorSemana = (double) 48;
	Double horaFinNoctAm = (double) 7;
	Double horaInicNoctPm = (double) 20;
	int dayOfWeek = 0;

	@Autowired
	private com.ias.calculadoraApp.Dao.IReporteDeServicio ReporteServicioDao;

	@Override
	@Transactional(readOnly = true)
	public List<ReporteDeServicio> findAll() {
		return (List<ReporteDeServicio>) ReporteServicioDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public ReporteDeServicio findById(Long id) {

		return ReporteServicioDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public ReporteDeServicio save(ReporteDeServicio reporteDeServicio) {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setMinimalDaysInFirstWeek(7);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTime(date);
		reporteDeServicio.setSemana(calendar.get(Calendar.WEEK_OF_YEAR));
		return ReporteServicioDao.save(reporteDeServicio);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		ReporteServicioDao.deleteById(id);
	}

	@Override
	public List<ReporteDeServicio> encontrarTecnicoPorIdAndSemana(String id_tecnico, Integer semana) {
		List<ReporteDeServicio> listaDeServicios = ReporteServicioDao.encontrarTecnicoPorIdAndSemana(id_tecnico,
				semana);
		return listaDeServicios;
	}

	@Override
	public HorasDineroDto valorAPagar(String id_tecnico, Integer semana) {

		inicializarVariables();

		HorasDineroDto HorasDineroDto = new HorasDineroDto();

		List<ReporteDeServicio> listaDeServicios = ReporteServicioDao.encontrarTecnicoPorIdAndSemana(id_tecnico,
				semana);
		listaDeServicios.forEach(element -> {
			
			validarDia(element);
		
		});

		pagarHorasTrabajadas();

		HorasDineroDto.setTotalAPagar(valor);
		HorasDineroDto.setHorasNocturnas(
				String.valueOf(Math.round(totalNocturnas)) + ":" + String.valueOf(Math.round(minutosNocturnas)));
		HorasDineroDto.setHorasDiurnas(
				String.valueOf(Math.round(totalDiurnas)) + ":" + String.valueOf(Math.round(minutosDiurnas)));
		HorasDineroDto
				.setHorasDominicales(LocalTime.MIN.plus(Duration.ofMinutes(totalDominicales.longValue())).toString());
		HorasDineroDto.setHorasTotalesAcomuladas(
				LocalTime.MIN.plus(Duration.ofMinutes(horasTotalesAcomuladas.longValue())).toString());
		HorasDineroDto.setHorasExtraDiurnas(
				String.valueOf(Math.round(totalExtraDiurnas)) + ":" + String.valueOf(Math.round(minutosExtraDiurnas)));
		HorasDineroDto.setHorasExtraNocturnas(String.valueOf(Math.round(totalExtraNocturnas)) + ":"
				+ String.valueOf(Math.round(minutosExtraNocturnas)));
		HorasDineroDto.setHorasExtraDominicales(
				LocalTime.MIN.plus(Duration.ofMinutes(totalExtraDominicales.longValue())).toString());
		HorasDineroDto.setTotalNocturnas(totalAPagarHorasNocturnas);
		HorasDineroDto.setTotalExtraDiurnas(totalAPagarHorasExtraDiurnas);
		HorasDineroDto.setTotalExtraNocturna(totalAPagarHorasExtraNocturnas);
		HorasDineroDto.setTotalExtraDominicales(totalAPagarHorasExtraDominicales);
		HorasDineroDto.setTotalDiurnas(totalAPagarHorasDiurnas);
		HorasDineroDto.setTotalDominicales(totalAPagarHorasDominicales);

		return HorasDineroDto;
	}
	
	

	public double calcularHorasNocturnasAm(ReporteDeServicio element) {

		if (Integer.valueOf(element.getHoraInicio()) >= horaFinNoctAm) {
			return 0;
		} else {

			if (Integer.valueOf(element.getHoraFin()) >= horaFinNoctAm) {
				if ((horasTotalesAcomuladas) > minutosCumplidosPorSemana) {
					double value = (totalNocturnas + totalDiurnas + totalDominicales) - minutosCumplidosPorSemana;
					totalNocturnas = (totalNocturnas - value);
					minutosNocturnas = minutosNocturnas + element.getMinutosTotales();
					calcularHorasExtraNocturnasAm(element, value);
					return 0;
				} else {
					totalNocturnas = totalNocturnas + (horaFinNoctAm - Double.valueOf(element.getHoraInicio()));
					horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
							+ totalDominicales + totalExtraDominicales;
					minutosNocturnas = minutosNocturnas + element.getMinutosTotales();

					return totalNocturnas;
				}

			}
			if (Integer.valueOf(element.getHoraInicio()) < horaInicNoctPm
					&& Integer.valueOf(element.getHoraFin()) < horaFinNoctAm) {
				if ((horasTotalesAcomuladas) > minutosCumplidosPorSemana) {
					double value = (totalNocturnas + totalDiurnas + totalDominicales) - minutosCumplidosPorSemana;
					totalNocturnas = (totalNocturnas - value);
					minutosNocturnas = minutosNocturnas + element.getMinutosTotales();

					calcularHorasExtraNocturnasAm(element, value);
					return 0;
				} else {
					totalNocturnas = totalNocturnas
							+ (Double.valueOf(element.getHoraFin()) - Double.valueOf(element.getHoraInicio()));
					horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
							+ totalDominicales + totalExtraDominicales;
					minutosNocturnas = minutosNocturnas + element.getMinutosTotales();

					return totalNocturnas;
				}
			}
		}

		return totalNocturnas;
	}

	public double calcularHorasNocturnasPm(ReporteDeServicio element) {
		double horaFinal = Double.valueOf(element.getHoraFin()) == 0 ? 24 : Double.valueOf(element.getHoraFin());
		if (horaFinal > horaInicNoctPm) {
			if (Double.valueOf(element.getHoraInicio()) <= horaInicNoctPm && horaFinal > horaInicNoctPm) {
				if ((horasTotalesAcomuladas) > minutosCumplidosPorSemana) {
					double value = (totalNocturnas + totalDiurnas + totalDominicales) - minutosCumplidosPorSemana;
					totalNocturnas = (totalNocturnas - value);
					minutosNocturnas = minutosNocturnas + element.getMinutosTotales();
					calcularHorasExtraNocturnasPm(element, value);
					return 0;
				} else {
					totalNocturnas = totalNocturnas + (horaFinal - horaInicNoctPm);
					horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
							+ totalDominicales + totalExtraDominicales;
					minutosNocturnas = minutosNocturnas + element.getMinutosTotales();
					return totalNocturnas;
				}
			}
			if (Double.valueOf(element.getHoraInicio()) >= horaInicNoctPm) {
				if ((horasTotalesAcomuladas) > minutosCumplidosPorSemana) {
					double value = (totalNocturnas + totalDiurnas + totalDominicales) - minutosCumplidosPorSemana;
					totalNocturnas = (totalNocturnas - value);
					minutosNocturnas = minutosNocturnas + element.getMinutosTotales();

					calcularHorasExtraNocturnasPm(element, value);
					return 0;
				} else {
					totalNocturnas = totalNocturnas + (horaFinal - Double.valueOf(element.getHoraInicio()));
					horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
							+ totalDominicales + totalExtraDominicales;
					minutosNocturnas = minutosNocturnas + element.getMinutosTotales();

					return totalNocturnas;
				}
			} else {
				return 0;
			}

		}

		return totalNocturnas;

	}

	public double calcularHorasDiurnasExternas(ReporteDeServicio element) {

		if (Double.valueOf(element.getHoraInicio()) <= horaFinNoctAm
				&& Double.valueOf(element.getHoraFin()) >= horaInicNoctPm) {

			if (Double.valueOf(element.getHoraInicio()) <= horaFinNoctAm
					&& Double.valueOf(element.getHoraFin()) >= horaInicNoctPm) {
				if ((horasTotalesAcomuladas) > minutosCumplidosPorSemana) {
					double value = (totalNocturnas + totalDiurnas + totalDominicales) - minutosCumplidosPorSemana;
					totalDiurnas = (totalDiurnas - value);
					minutosDiurnas = minutosDiurnas + element.getMinutosTotales();
					calcularHorasExtraDiurnasExternas(element, value);

					return 0;
				} else {
					totalDiurnas = totalDiurnas + (horaInicNoctPm - horaFinNoctAm);
					horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
							+ totalDominicales + totalExtraDominicales;
					minutosDiurnas = minutosDiurnas + element.getMinutosTotales();
					return totalDiurnas;
				}
			}
			if (Double.valueOf(element.getHoraInicio()) < horaFinNoctAm
					&& Double.valueOf(element.getHoraFin()) > horaInicNoctPm) {
				if ((horasTotalesAcomuladas) > minutosCumplidosPorSemana) {
					double value = (totalNocturnas + totalDiurnas + totalDominicales) - minutosCumplidosPorSemana;
					totalDiurnas = (totalDiurnas - value);
					minutosDiurnas = minutosDiurnas + element.getMinutosTotales();
					calcularHorasExtraDiurnasExternas(element, value);
					return 0;
				} else {
					totalDiurnas = totalDiurnas + (horaInicNoctPm - Double.valueOf(element.getHoraInicio()));
					horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
							+ totalDominicales + totalExtraDominicales;
					minutosDiurnas = minutosDiurnas + element.getMinutosTotales();
					return totalDiurnas;
				}
			}
		} else if (Double.valueOf(element.getHoraInicio()) < horaFinNoctAm
				&& Double.valueOf(element.getHoraFin()) < horaInicNoctPm) {
			if ((horasTotalesAcomuladas) > minutosCumplidosPorSemana) {
				double value = (totalNocturnas + totalDiurnas + totalDominicales) - minutosCumplidosPorSemana;
				totalDiurnas = (totalDiurnas - value);
				minutosDiurnas = minutosDiurnas + element.getMinutosTotales();
				calcularHorasExtraDiurnasExternas(element, value);
				return 0;
			} else {
				totalDiurnas = totalDiurnas + (Double.valueOf(element.getHoraFin()) - horaFinNoctAm);
				horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
						+ totalDominicales + totalExtraDominicales;
				minutosDiurnas = minutosDiurnas + element.getMinutosTotales();
				return totalDiurnas;
			}
		} else if (Double.valueOf(element.getHoraInicio()) >= horaFinNoctAm
				&& Double.valueOf(element.getHoraFin()) > horaInicNoctPm) {
			if ((horasTotalesAcomuladas) > minutosCumplidosPorSemana) {
				double value = (totalNocturnas + totalDiurnas + totalDominicales) - minutosCumplidosPorSemana;
				totalDiurnas = (totalDiurnas - value);
				minutosDiurnas = minutosDiurnas + element.getMinutosTotales();
				calcularHorasExtraDiurnasExternas(element, value);
				return 0;
			} else {
				totalDiurnas = totalDiurnas + (horaInicNoctPm - Double.valueOf(element.getHoraInicio()));
				horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
						+ totalDominicales + totalExtraDominicales;
				minutosDiurnas = minutosDiurnas + element.getMinutosTotales();
				return totalDiurnas;
			}
		}

		return totalDiurnas;
	}

	public double calcularDiurnasInternas(ReporteDeServicio element) {

		if (Double.valueOf(element.getHoraInicio()) >= horaFinNoctAm
				&& Double.valueOf(element.getHoraFin()) <= horaInicNoctPm) {
			if (Double.valueOf(element.getHoraInicio()) >= horaFinNoctAm
					&& Double.valueOf(element.getHoraFin()) <= horaInicNoctPm) {
				if ((horasTotalesAcomuladas) > minutosCumplidosPorSemana) {
					double value = (totalNocturnas + totalDiurnas + totalDominicales) - minutosCumplidosPorSemana;
					totalDiurnas = (totalDiurnas - value);
					minutosDiurnas = minutosDiurnas + element.getMinutosTotales();
					calcularExtraDiurnasInternas(element, value);
					return 0;
				} else {
					totalDiurnas = totalDiurnas
							+ (Double.valueOf(element.getHoraFin()) - Double.valueOf(element.getHoraInicio()));
					horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
							+ totalDominicales + totalExtraDominicales;
					minutosDiurnas = minutosDiurnas + element.getMinutosTotales();
					if ((horasTotalesAcomuladas) > minutosCumplidosPorSemana) {
						double value = (totalNocturnas + totalDiurnas + totalDominicales) - minutosCumplidosPorSemana;
						totalDiurnas = (totalDiurnas - value);
						minutosDiurnas = minutosDiurnas + element.getMinutosTotales();
						calcularExtraDiurnasInternas(element, value);
						return 0;
					}
					return totalDiurnas;
				}
			}
		} else {
			return 0;
		}
		return totalDiurnas;
	}

	public double calcularHorasDominicales(ReporteDeServicio element) {

		totalDominicales = totalDominicales + Double.valueOf(element.getHorasTotales());
		horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
				+ totalDominicales + totalExtraDominicales;
		if ((horasTotalesAcomuladas) > minutosCumplidosPorSemana) {
			double value = (totalNocturnas + totalDiurnas + totalDominicales) - minutosCumplidosPorSemana;
			totalDominicales = (totalDominicales - value);

			calcularHorasExtraDominical(element, value);
		}
		return totalDominicales;
	}

	public double calcularHorasExtraDominical(ReporteDeServicio element, double value) {
		totalExtraDominicales = totalExtraDominicales + Double.valueOf(element.getHorasTotales());
		horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
				+ totalDominicales + totalExtraDominicales;

		return totalAPagarHorasExtraDominicales;
	}

	public double calcularHorasExtraNocturnasAm(ReporteDeServicio element, double value) {
		if (Integer.valueOf(element.getHoraInicio()) >= horaFinNoctAm) {
			return 0;
		} else {

			if (Integer.valueOf(element.getHoraFin()) >= horaFinNoctAm) {
				totalExtraNocturnas = totalExtraNocturnas + (horaFinNoctAm - Double.valueOf(element.getHoraInicio()))
						+ value;
				horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
						+ totalDominicales + totalExtraDominicales;
				minutosExtraNocturnas = minutosExtraNocturnas + element.getMinutosTotales();
				return totalExtraNocturnas;
			}

			if (Integer.valueOf(element.getHoraInicio()) < horaInicNoctPm
					&& Integer.valueOf(element.getHoraFin()) < horaFinNoctAm) {
				totalExtraNocturnas = totalExtraNocturnas
						+ (Double.valueOf(element.getHoraFin()) - Double.valueOf(element.getHoraInicio())) + value;
				horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
						+ totalDominicales + totalExtraDominicales;
				minutosExtraNocturnas = minutosExtraNocturnas + element.getMinutosTotales();
				return totalExtraNocturnas;
			}
		}

		return totalExtraNocturnas;
	}

	public double calcularHorasExtraNocturnasPm(ReporteDeServicio element, double value) {
		double horaFinal = Double.valueOf(element.getHoraFin()) == 0 ? 24 : Double.valueOf(element.getHoraFin());
		if (horaFinal > horaInicNoctPm) {
			if (Double.valueOf(element.getHoraInicio()) <= horaInicNoctPm && horaFinal > horaInicNoctPm) {

				totalExtraNocturnas = totalExtraNocturnas + (horaFinal - horaInicNoctPm) + value;
				horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
						+ totalDominicales + totalExtraDominicales;
				minutosExtraNocturnas = minutosExtraNocturnas + element.getMinutosTotales();
				return totalExtraNocturnas;
			}
			if (Double.valueOf(element.getHoraInicio()) >= horaInicNoctPm) {
				totalExtraNocturnas = totalExtraNocturnas + (horaFinal - Double.valueOf(element.getHoraInicio()))
						+ value;
				horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
						+ totalDominicales + totalExtraDominicales;
				minutosExtraNocturnas = minutosExtraNocturnas + element.getMinutosTotales();
				return totalExtraNocturnas;
			} else {
				return 0;
			}
		}

		return totalExtraNocturnas;
	}

	public double calcularHorasExtraDiurnasExternas(ReporteDeServicio element, double value) {

		if (Double.valueOf(element.getHoraInicio()) <= horaFinNoctAm
				&& Double.valueOf(element.getHoraFin()) >= horaInicNoctPm) {
			if (Double.valueOf(element.getHoraInicio()) <= horaFinNoctAm
					&& Double.valueOf(element.getHoraFin()) >= horaInicNoctPm) {
				totalExtraDiurnas = totalExtraDiurnas + (horaInicNoctPm - horaFinNoctAm) + value;
				horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
						+ totalDominicales + totalExtraDominicales;
				minutosExtraDiurnas = minutosExtraDiurnas + element.getMinutosTotales();
				return totalExtraDiurnas;
			}
			if (Double.valueOf(element.getHoraInicio()) < horaFinNoctAm
					&& Double.valueOf(element.getHoraFin()) > horaInicNoctPm) {
				totalExtraDiurnas = totalExtraDiurnas + (horaInicNoctPm - Double.valueOf(element.getHoraInicio()))
						+ value;
				horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
						+ totalDominicales + totalExtraDominicales;
				minutosExtraDiurnas = minutosExtraDiurnas + element.getMinutosTotales();
				return totalExtraDiurnas;
			}
		} else if (Double.valueOf(element.getHoraInicio()) < horaFinNoctAm
				&& Double.valueOf(element.getHoraFin()) < horaInicNoctPm) {
			totalExtraDiurnas = totalExtraDiurnas + (Double.valueOf(element.getHoraFin()) - horaFinNoctAm) + value;

			horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
					+ totalDominicales + totalExtraDominicales;
			minutosExtraDiurnas = minutosExtraDiurnas + element.getMinutosTotales();
			return totalExtraDiurnas;
		} else if (Double.valueOf(element.getHoraInicio()) >= horaFinNoctAm
				&& Double.valueOf(element.getHoraFin()) > horaInicNoctPm) {
			totalExtraDiurnas = totalExtraDiurnas + (horaInicNoctPm - Double.valueOf(element.getHoraInicio())) + value;
			horasTotalesAcomuladas = totalNocturnas + totalDiurnas + totalExtraDiurnas + totalExtraNocturnas
					+ totalDominicales + totalExtraDominicales;
			minutosExtraDiurnas = minutosExtraDiurnas + element.getMinutosTotales();
			return totalExtraDiurnas;
		}

		return totalExtraDiurnas;
	}

	public double calcularExtraDiurnasInternas(ReporteDeServicio element, double value) {

		if (Double.valueOf(element.getHoraInicio()) >= horaFinNoctAm
				&& Double.valueOf(element.getHoraFin()) <= horaInicNoctPm) {
			if (Double.valueOf(element.getHoraInicio()) >= horaFinNoctAm
					&& Double.valueOf(element.getHoraFin()) <= horaInicNoctPm) {

				totalExtraDiurnas = totalExtraDiurnas
						+ (Double.valueOf(element.getHoraFin()) - Double.valueOf(element.getHoraInicio())) + value;
				totalExtraDiurnas = (totalExtraDiurnas - value);
				horasTotalesAcomuladas = totalNocturnas + totalDiurnas + value + totalExtraNocturnas + totalDominicales
						+ totalExtraDominicales;
				minutosExtraDiurnas = minutosExtraDiurnas + element.getMinutosTotales();
				return totalExtraDiurnas;
			}
		} else {
			return 0;
		}
		return totalExtraDiurnas;
	}

	public void pagarHorasTrabajadas() {
		totalAPagarHorasNocturnas = ((totalNocturnas * 60) + minutosNocturnas) * 666.666667;
		totalAPagarHorasDiurnas = ((totalDiurnas * 60) + minutosDiurnas) * 500;
		totalAPagarHorasDominicales = totalDominicales * 833.333333;
		totalAPagarHorasExtraNocturnas = ((totalExtraNocturnas * 60) + minutosExtraNocturnas) * 1000;
		totalAPagarHorasExtraDiurnas = ((totalExtraDiurnas * 60) + minutosExtraDiurnas) * 833.333333;
		totalAPagarHorasExtraDominicales = totalExtraDominicales * 1166.66667;
		valor = totalAPagarHorasNocturnas + totalAPagarHorasDiurnas + totalAPagarHorasDominicales
				+ totalAPagarHorasExtraNocturnas + totalAPagarHorasExtraDiurnas + totalAPagarHorasExtraDominicales;
	}

	public void inicializarVariables() {
		horasTotales = (double) 0;
		valor = (double) 0;
		totalNocturnas = (double) 0;
		minutosNocturnas = (double) 0;
		totalDiurnas = (double) 0;
		minutosDiurnas = (double) 0;
		totalAPagarHorasNocturnas = (double) 0;
		totalAPagarHorasDiurnas = (double) 0;
		totalDominicales = (double) 0;
		minutosDominicales = (double) 0;
		totalAPagarHorasDominicales = (double) 0;
		horasTotalesAcomuladas = (double) 0;
		totalAPagarHorasExtraDiurnas = (double) 0;
		totalExtraDiurnas = (double) 0;
		minutosExtraDiurnas = (double) 0;
		totalExtraNocturnas = (double) 0;
		minutosExtraNocturnas = (double) 0;
		totalAPagarHorasExtraNocturnas = (double) 0;
		totalAPagarHorasExtraDominicales = (double) 0;
		totalExtraDominicales = (double) 0;
		minutosExtraDominicales = (double) 0;
	}
	
	public void validarDia(ReporteDeServicio element) {
		Calendar c = Calendar.getInstance();
		c.setTime(element.getFechaInicio());
		dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

		if (dayOfWeek == 7) {
			calcularHorasDominicales(element);

		} else {
			calcularHorasNocturnasAm(element);
			calcularHorasNocturnasPm(element);
			calcularDiurnasInternas(element);
			calcularHorasDiurnasExternas(element);
		}
	}
}