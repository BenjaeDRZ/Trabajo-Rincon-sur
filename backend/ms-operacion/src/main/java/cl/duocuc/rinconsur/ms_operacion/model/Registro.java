package cl.duocuc.rinconsur.ms_operacion.model;

import jakarta.persistence.*;

/** Una persona registrada por el observador durante una sesión. */
@Entity
@Table(name = "registros", uniqueConstraints = @UniqueConstraint(columnNames = {"sesion", "numero"}))
public class Registro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numero;
    private String sesion;
    private String hora;
    private String clasificacion;
    private String rangoEtario;
    private String compania;
    private Boolean traeCompras;
    private String accesoDestino;
    private Boolean anulado = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getSesion() { return sesion; }
    public void setSesion(String sesion) { this.sesion = sesion; }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    public String getClasificacion() { return clasificacion; }
    public void setClasificacion(String clasificacion) { this.clasificacion = clasificacion; }
    public String getRangoEtario() { return rangoEtario; }
    public void setRangoEtario(String rangoEtario) { this.rangoEtario = rangoEtario; }
    public String getCompania() { return compania; }
    public void setCompania(String compania) { this.compania = compania; }
    public Boolean getTraeCompras() { return traeCompras; }
    public void setTraeCompras(Boolean traeCompras) { this.traeCompras = traeCompras; }
    public String getAccesoDestino() { return accesoDestino; }
    public void setAccesoDestino(String accesoDestino) { this.accesoDestino = accesoDestino; }
    public Boolean getAnulado() { return anulado; }
    public void setAnulado(Boolean anulado) { this.anulado = anulado; }
}
