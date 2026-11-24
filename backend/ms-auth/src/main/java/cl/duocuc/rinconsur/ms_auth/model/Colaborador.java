package cl.duocuc.rinconsur.ms_auth.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "colaboradores")
public class Colaborador {
    @Id
    private String codigo;
    private String nombre;
    private String contrasena;
    private String rut;
    private String cargo;
    private String codigoPuesto;
    private String email;
    private String telefono;

    public Colaborador() {}

    public Colaborador(String codigo, String nombre, String contrasena, String rut, String cargo,
                       String codigoPuesto, String email, String telefono) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.rut = rut;
        this.cargo = cargo;
        this.codigoPuesto = codigoPuesto;
        this.email = email;
        this.telefono = telefono;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getCodigoPuesto() { return codigoPuesto; }
    public void setCodigoPuesto(String codigoPuesto) { this.codigoPuesto = codigoPuesto; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
