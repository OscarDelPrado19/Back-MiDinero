package com.midinero.service;

import com.midinero.dto.ApiResponse;
import com.midinero.entity.Tip;
import com.midinero.repository.TipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipService {

    @Autowired
    private TipRepository tipRepository;

    public ApiResponse<List<Tip>> obtenerTips() {
        try {
            List<Tip> tips = tipRepository.findAll();
            return ApiResponse.success("Tips obtenidos exitosamente", tips);
        } catch (Exception e) {
            return ApiResponse.error("Error al obtener los tips");
        }
    }
}
