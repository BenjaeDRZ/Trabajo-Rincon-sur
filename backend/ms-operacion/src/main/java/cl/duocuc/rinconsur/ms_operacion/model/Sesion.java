package cl.duocuc.rinconsur.ms_operacion.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sesiones")
public class Sesion {
    @Id
    private String numero;
    private String codigoAcceso;
    private String fecha;
    private String horaInicioProg;
    private String horaTerminoProg;
    private String observador;
    private String estado;
    private String validador;
    private String motivoRechazo;

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getCodigoAcceso() { return codigoAcceso; }
    public void setCodigoAcceso(String codigoAcceso) { this.codigoAcceso = codigoAcceso; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getHoraInicioProg() { return horaInicioProg; }
    public void setHoraInicioProg(String horaInicioProg) { this.horaInicioProg = horaInicioProg; }
    public String getHoraTerminoProg() { return horaTerminoProg; }
    public void setHoraTerminoProg(String horaTerminoProg) { this.horaTerminoProg = horaTerminoProg; }
    public String getObservador() { return observador; }
    public void setObservador(String observador) { this.observador = observador; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getValidador() { return validador; }
    public void setValidador(String validador) { this.validador = validador; }
    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }
}
