package cl.duocuc.rinconsur.ms_analitica.model;

import jakarta.persistence.*;

@Entity
@Table(name = "conteos")
public class Conteo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fecha;
    private String acceso;
    private String bloque;
    private Integer cantidad;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getAcceso() { return acceso; }
    public void setAcceso(String acceso) { this.acceso = acceso; }
    public String getBloque() { return bloque; }
    public void setBloque(String bloque) { this.bloque = bloque; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}