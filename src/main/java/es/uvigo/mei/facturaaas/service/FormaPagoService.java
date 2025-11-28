package es.uvigo.mei.facturaaas.service;

import es.uvigo.mei.facturaaas.dto.FormaPagoDTO;
import es.uvigo.mei.facturaaas.model.FormaPago;
import es.uvigo.mei.facturaaas.model.Usuario;
import es.uvigo.mei.facturaaas.repository.FormaPagoRepository;
import es.uvigo.mei.facturaaas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FormaPagoService {
    
    @Autowired
    private FormaPagoRepository formaPagoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public List<FormaPagoDTO> obtenerPorUsuario(Long usuarioId) {
        return formaPagoRepository.findByUsuarioId(usuarioId).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    public List<FormaPagoDTO> obtenerActivasPorUsuario(Long usuarioId) {
        return formaPagoRepository.findByUsuarioIdAndActivaTrue(usuarioId).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    public FormaPagoDTO obtenerPorId(Long id) {
        FormaPago formaPago = formaPagoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Forma de pago no encontrada"));
        return convertirADTO(formaPago);
    }
    
    public FormaPagoDTO crear(FormaPagoDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        FormaPago formaPago = new FormaPago();
        formaPago.setUsuario(usuario);
        formaPago.setDescripcion(dto.getDescripcion());
        formaPago.setNumeroPagos(dto.getNumeroPagos());
        formaPago.setPeriodicidadDias(dto.getPeriodicidadDias());
        formaPago.setActiva(true);
        
        formaPago = formaPagoRepository.save(formaPago);
        return convertirADTO(formaPago);
    }
    
    public FormaPagoDTO actualizar(Long id, FormaPagoDTO dto) {
        FormaPago formaPago = formaPagoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Forma de pago no encontrada"));
        
        formaPago.setDescripcion(dto.getDescripcion());
        formaPago.setNumeroPagos(dto.getNumeroPagos());
        formaPago.setPeriodicidadDias(dto.getPeriodicidadDias());
        formaPago.setActiva(dto.getActiva());
        
        formaPago = formaPagoRepository.save(formaPago);
        return convertirADTO(formaPago);
    }
    
    public void eliminarLogicamente(Long id) {
        FormaPago formaPago = formaPagoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Forma de pago no encontrada"));
        formaPago.setActiva(false);
        formaPagoRepository.save(formaPago);
    }
    
    private FormaPagoDTO convertirADTO(FormaPago formaPago) {
        FormaPagoDTO dto = new FormaPagoDTO();
        dto.setId(formaPago.getId());
        dto.setUsuarioId(formaPago.getUsuario().getId());
        dto.setDescripcion(formaPago.getDescripcion());
        dto.setNumeroPagos(formaPago.getNumeroPagos());
        dto.setPeriodicidadDias(formaPago.getPeriodicidadDias());
        dto.setActiva(formaPago.getActiva());
        return dto;
    }
}
