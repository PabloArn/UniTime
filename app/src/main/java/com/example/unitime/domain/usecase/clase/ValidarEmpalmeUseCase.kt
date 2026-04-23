package com.example.unitime.domain.usecase.clase

import com.example.unitime.data.local.entity.ClaseEntity
import com.example.unitime.domain.repository.ClaseRepository
import javax.inject.Inject

class ValidarEmpalmeUseCase @Inject constructor(
    private val repository: ClaseRepository
) {
    // Retorna la clase con la que choca, o null si no hay conflicto
    suspend operator fun invoke(
        diasNueva: List<String>,
        horaInicio: String,
        horaFin: String,
        excludeId: Long = 0
    ): ClaseEntity? {

        val inicioNueva = horaAMinutos(horaInicio)
        val finNueva = horaAMinutos(horaFin)

        for (dia in diasNueva) {
            val clasesDelDia = repository.getClasesByDiaParaValidacion(dia, excludeId)

            for (clase in clasesDelDia) {
                val inicioExistente = horaAMinutos(clase.horaInicio)
                val finExistente = horaAMinutos(clase.horaFin)

                // Hay empalme si los rangos se solapan
                val hayEmpalme = inicioNueva < finExistente && finNueva > inicioExistente

                if (hayEmpalme) return clase
            }
        }
        return null
    }

    // Convierte "14:30" a 870 minutos para comparar fácilmente
    private fun horaAMinutos(hora: String): Int {
        val partes = hora.split(":")
        return partes[0].toInt() * 60 + partes[1].toInt()
    }
}