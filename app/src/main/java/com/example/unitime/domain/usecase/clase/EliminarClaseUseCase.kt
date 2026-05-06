package com.example.unitime.domain.usecase.clase

import com.example.unitime.data.local.entity.ClaseEntity
import com.example.unitime.domain.repository.ClaseRepository
import javax.inject.Inject

class EliminarClaseUseCase @Inject constructor(
    private val repository: ClaseRepository
) {
    suspend operator fun invoke(clase: ClaseEntity) {
        repository.deleteClase(clase)
    }
}