package com.midinero.service;

import com.midinero.dto.ApiResponse;
import com.midinero.entity.Glosario;
import com.midinero.repository.GlosarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GlosarioService {

    @Autowired
    private GlosarioRepository glosarioRepository;

    public ApiResponse<List<Glosario>> obtenerGlosario() {
        try {
            List<Glosario> glosario = glosarioRepository.findAll();
            return ApiResponse.success("Glosario obtenido exitosamente", glosario);
        } catch (Exception e) {
            return ApiResponse.error("Error al obtener el glosario");
        }
    }
}
