package es.uvigo.mei.facturaaas.service;

import es.uvigo.mei.facturaaas.dto.TipoIVADTO;
import es.uvigo.mei.facturaaas.model.TipoIVA;
import es.uvigo.mei.facturaaas.repository.TipoIVARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TipoIVAService {
    
    @Autowired
    private TipoIVARepository tipoIVARepository;
    
    public List<TipoIVADTO> obtenerTodos() {
        return tipoIVARepository.findAll().stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    public List<TipoIVADTO> obtenerActivos() {
        return tipoIVARepository.findByActivoTrue().stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    public TipoIVADTO obtenerPorId(Long id) {
        TipoIVA tipoIVA = tipoIVARepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tipo de IVA no encontrado"));
        return convertirADTO(tipoIVA);
    }
    
    public TipoIVADTO crear(TipoIVADTO dto) {
        TipoIVA tipoIVA = new TipoIVA();
        tipoIVA.setDescripcion(dto.getDescripcion());
        tipoIVA.setPorcentaje(dto.getPorcentaje());
        tipoIVA.setActivo(true);
        
        tipoIVA = tipoIVARepository.save(tipoIVA);
        return convertirADTO(tipoIVA);
    }
    
    public TipoIVADTO actualizar(Long id, TipoIVADTO dto) {
        TipoIVA tipoIVA = tipoIVARepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tipo de IVA no encontrado"));
        
        tipoIVA.setDescripcion(dto.getDescripcion());
        tipoIVA.setPorcentaje(dto.getPorcentaje());
        tipoIVA.setActivo(dto.getActivo());
        
        tipoIVA = tipoIVARepository.save(tipoIVA);
        return convertirADTO(tipoIVA);
    }
    
    public void eliminarLogicamente(Long id) {
        TipoIVA tipoIVA = tipoIVARepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tipo de IVA no encontrado"));
        tipoIVA.setActivo(false);
        tipoIVARepository.save(tipoIVA);
    }
    
    private TipoIVADTO convertirADTO(TipoIVA tipoIVA) {
        TipoIVADTO dto = new TipoIVADTO();
        dto.setId(tipoIVA.getId());
        dto.setDescripcion(tipoIVA.getDescripcion());
        dto.setPorcentaje(tipoIVA.getPorcentaje());
        dto.setActivo(tipoIVA.getActivo());
        return dto;
    }
}
