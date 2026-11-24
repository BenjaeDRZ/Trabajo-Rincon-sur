package cl.duocuc.miregistro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.roundToInt

class ReglasNegocioTest {



    @Test
    fun `calculo de conversion es correcto`() {

        val transacciones = 497
        val ingresos = 5735

        val conversion = (transacciones.toDouble() / ingresos * 100)

        val conversionRedondeada = (conversion * 10).roundToInt() / 10.0
        assertEquals(8.7, conversionRedondeada, 0.01)
    }



    @Test
    fun `sesion con menos de 30 registros no se puede validar`() {

        val registrosValidos = 29

        val puedeValidar = registrosValidos >= 30

        assertFalse("No debería permitir validar con menos de 30 registros", puedeValidar)
    }

    @Test
    fun `sesion con 30 o mas registros si se puede validar`() {

        val registrosValidos = 30

        val puedeValidar = registrosValidos >= 30

        assertTrue("Debería permitir validar con 30 o más registros", puedeValidar)
    }



    @Test
    fun `supervisor no puede validar su propia sesion`() {

        val observador = "C-03"
        val supervisor = "C-03"

        val esPropiaSesion = observador == supervisor
        val puedeValidar = !esPropiaSesion // Lógica: si es propia, no puede

        assertFalse("El supervisor no puede validar su propia sesión", puedeValidar)
    }



    @Test
    fun `sesion de 45 minutos es valida`() {

        val horaInicio = 13 * 60 + 0   // 780 minutos
        val horaTermino = 13 * 60 + 45 // 825 minutos

        val duracion = horaTermino - horaInicio
        val esValida = duracion in 30..60

        assertTrue("Una sesión de 45 min debería ser válida", esValida)
    }

    @Test
    fun `sesion de 20 minutos es invalida`() {

        val horaInicio = 13 * 60 + 0
        val horaTermino = 13 * 60 + 20

        val duracion = horaTermino - horaInicio
        val esValida = duracion in 30..60

        assertFalse("Una sesión de 20 min debería ser inválida", esValida)
    }
}