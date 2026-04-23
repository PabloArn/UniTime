package com.example.unitime.domain.usecase.clase

import com.example.unitime.data.local.entity.ClaseEntity
import com.example.unitime.domain.repository.ClaseRepository
import javax.inject.Inject

class GuardarClaseUseCase @Inject constructor(
    private val repository: ClaseRepository
) {
    suspend operator fun invoke(clase: ClaseEntity): Long {
        return repository.insertClase(clase)
    }
}