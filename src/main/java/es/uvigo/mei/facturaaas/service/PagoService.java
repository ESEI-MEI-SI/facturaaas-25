package es.uvigo.mei.facturaaas.service;

import es.uvigo.mei.facturaaas.dto.PagoDTO;
import es.uvigo.mei.facturaaas.model.Factura;
import es.uvigo.mei.facturaaas.model.Pago;
import es.uvigo.mei.facturaaas.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PagoService {
    
    @Autowired
    private PagoRepository pagoRepository;
    
    public List<PagoDTO> obtenerPorUsuario(Long usuarioId) {
        return pagoRepository.findByUsuarioId(usuarioId).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    public List<PagoDTO> obtenerPorUsuarioYCliente(Long usuarioId, Long clienteId) {
        return pagoRepository.findByUsuarioIdAndClienteId(usuarioId, clienteId).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    public PagoDTO obtenerPorId(Long id) {
        Pago pago = pagoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        return convertirADTO(pago);
    }
    
    public PagoDTO actualizarEstado(Long id, Pago.EstadoPago nuevoEstado) {
        Pago pago = pagoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        
        pago.setEstado(nuevoEstado);
        if (nuevoEstado == Pago.EstadoPago.PAGADO && pago.getFechaPago() == null) {
            pago.setFechaPago(LocalDate.now());
        }
        
        pago = pagoRepository.save(pago);
        return convertirADTO(pago);
    }
    
    public void generarPagosParaFactura(Factura factura) {
        Integer numeroPagos = factura.getFormaPago().getNumeroPagos();
        Integer periodicidadDias = factura.getFormaPago().getPeriodicidadDias();
        BigDecimal totalFactura = factura.getSumaTotal();
        
        // Calcular importe por pago
        BigDecimal importePorPago = totalFactura.divide(
            new BigDecimal(numeroPagos), 2, RoundingMode.HALF_UP);
        
        // Ajustar último pago para compensar redondeos
        BigDecimal sumaImportes = importePorPago.multiply(new BigDecimal(numeroPagos - 1));
        BigDecimal importeUltimoPago = totalFactura.subtract(sumaImportes);
        
        for (int i = 1; i <= numeroPagos; i++) {
            Pago pago = new Pago();
            pago.setFactura(factura);
            pago.setNumeroPago(i);
            pago.setFechaVencimiento(
                factura.getFechaEmision().plusDays((long) (i - 1) * periodicidadDias));
            pago.setImporte(i == numeroPagos ? importeUltimoPago : importePorPago);
            pago.setEstado(Pago.EstadoPago.PENDIENTE);
            
            factura.getPagos().add(pago);
        }
    }
    
    private PagoDTO convertirADTO(Pago pago) {
        PagoDTO dto = new PagoDTO();
        dto.setId(pago.getId());
        dto.setFacturaId(pago.getFactura().getId());
        dto.setNumeroFactura(pago.getFactura().getNumeroFactura());
        dto.setClienteNombre(pago.getFactura().getCliente().getNombre());
        dto.setClienteNif(pago.getFactura().getCliente().getNif());
        dto.setNumeroPago(pago.getNumeroPago());
        dto.setFechaVencimiento(pago.getFechaVencimiento());
        dto.setImporte(pago.getImporte());
        dto.setEstado(pago.getEstado());
        dto.setFechaPago(pago.getFechaPago());
        return dto;
    }
}
