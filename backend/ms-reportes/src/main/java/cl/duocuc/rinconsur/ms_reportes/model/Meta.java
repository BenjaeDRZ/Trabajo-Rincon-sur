package cl.duocuc.rinconsur.ms_reportes.model;

import jakarta.persistence.*;

@Entity
@Table(name = "metas")
public class Meta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String mes; 
    private Double metaConversion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMes() { return mes; }
    public void setMes(String mes) { this.mes = mes; }
    public Double getMetaConversion() { return metaConversion; }
    public void setMetaConversion(Double metaConversion) { this.metaConversion = metaConversion; }
}