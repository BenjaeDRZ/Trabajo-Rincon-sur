package cl.duocuc.rinconsur.ms_auth.dto;

import cl.duocuc.rinconsur.ms_auth.model.Colaborador;

/** Lo que se devuelve a la app: nunca incluye la contraseña. */
public record ColaboradorDto(String codigo, String nombre, String rut, String cargo,
                             String codigoPuesto, String email, String telefono) {

    public static ColaboradorDto de(Colaborador c) {
        return new ColaboradorDto(c.getCodigo(), c.getNombre(), c.getRut(), c.getCargo(),
                c.getCodigoPuesto(), c.getEmail(), c.getTelefono());
    }
}
