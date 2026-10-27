package cl.duocuc.miregistro.model

data class Colaborador(
    val codigo: String,        
    val nombre: String,        
    val rut: String,           
    val cargo: String,         
    val codigoPuesto: String,  
    val email: String,         
    val telefono: String       
)

val colaboradores = listOf(
    Colaborador(
        codigo = "C-01",
        nombre = "Carolina Fuentes Araya",
        rut = "15.234.871-1",
        cargo = "Jefa de tienda",
        codigoPuesto = "P-01",
        email = "carolina.fuentes@rinconsur-ficticio.ejemplo",
        telefono = "+56 9 0000 0011"
    ),
    Colaborador(
        codigo = "C-02",
        nombre = "Rodrigo Saavedra Muñoz",
        rut = "13.987.442-0",
        cargo = "Subgerente comercial",
        codigoPuesto = "P-02",
        email = "rodrigo.saavedra@rinconsur-ficticio.ejemplo",
        telefono = "+56 9 0000 0012"
    ),
    Colaborador(
        codigo = "C-03",
        nombre = "Daniela Pizarro Toro",
        rut = "17.654.320-5",
        cargo = "Supervisora de piso (turno mañana)",
        codigoPuesto = "P-03",
        email = "daniela.pizarro@rinconsur-ficticio.ejemplo",
        telefono = "+56 9 0000 0013"
    ),
    Colaborador(
        codigo = "C-04",
        nombre = "Matías Contreras Vidal",
        rut = "16.443.019-7",
        cargo = "Supervisor de piso (turno tarde)",
        codigoPuesto = "P-03",
        email = "matias.contreras@rinconsur-ficticio.ejemplo",
        telefono = "+56 9 0000 0014"
    ),
    Colaborador(
        codigo = "C-05",
        nombre = "Tamara Rojas Espinoza",
        rut = "19.876.345-4",
        cargo = "Vendedora, Vestuario Mujer",
        codigoPuesto = "P-04",
        email = "tamara.rojas@rinconsur-ficticio.ejemplo",
        telefono = "+56 9 0000 0015"
    ),
    Colaborador(
        codigo = "C-06",
        nombre = "Felipe Ibarra Soto",
        rut = "20.145.678-9",
        cargo = "Vendedor, Hogar y Deco",
        codigoPuesto = "P-04",
        email = "felipe.ibarra@rinconsur-ficticio.ejemplo",
        telefono = "+56 9 0000 0016"
    ),
    Colaborador(
        codigo = "C-07",
        nombre = "Javiera Morales Cáceres",
        rut = "18.322.904-4",
        cargo = "Cajera, Cajas Centrales",
        codigoPuesto = "P-04",
        email = "javiera.morales@rinconsur-ficticio.ejemplo",
        telefono = "+56 9 0000 0017"
    )
)