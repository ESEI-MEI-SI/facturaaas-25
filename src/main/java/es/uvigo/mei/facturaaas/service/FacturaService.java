package es.uvigo.mei.facturaaas.service;

import es.uvigo.mei.facturaaas.dto.FacturaDTO;
import es.uvigo.mei.facturaaas.dto.LineaFacturaDTO;
import es.uvigo.mei.facturaaas.model.*;
import es.uvigo.mei.facturaaas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FacturaService {
    
    @Autowired
    private FacturaRepository facturaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private FormaPagoRepository formaPagoRepository;
    
    @Autowired
    private TipoIVARepository tipoIVARepository;
    
    @Autowired
    private PagoService pagoService;
    
    public List<FacturaDTO> obtenerPorUsuario(Long usuarioId) {
        return facturaRepository.findByUsuarioId(usuarioId).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    public List<FacturaDTO> obtenerPorUsuarioYCliente(Long usuarioId, Long clienteId) {
        return facturaRepository.findByUsuarioIdAndClienteId(usuarioId, clienteId).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    public FacturaDTO obtenerPorId(Long id) {
        Factura factura = facturaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
        return convertirADTO(factura);
    }
    
    public FacturaDTO crear(FacturaDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        FormaPago formaPago = formaPagoRepository.findById(dto.getFormaPagoId())
            .orElseThrow(() -> new RuntimeException("Forma de pago no encontrada"));
        
        // Generar número de factura
        String numeroFactura = generarNumeroFactura(dto.getEjercicio());
        
        Factura factura = new Factura();
        factura.setNumeroFactura(numeroFactura);
        factura.setEjercicio(dto.getEjercicio());
        factura.setUsuario(usuario);
        factura.setCliente(cliente);
        factura.setFechaEmision(dto.getFechaEmision());
        factura.setFormaPago(formaPago);
        factura.setEstado(Factura.EstadoFactura.EMITIDA);
        factura.setComentarios(dto.getComentarios());
        
        // Agregar líneas
        if (dto.getLineas() != null) {
            for (LineaFacturaDTO lineaDTO : dto.getLineas()) {
                TipoIVA tipoIVA = tipoIVARepository.findById(lineaDTO.getTipoIVAId())
                    .orElseThrow(() -> new RuntimeException("Tipo de IVA no encontrado"));
                
                LineaFactura linea = new LineaFactura();
                linea.setFactura(factura);
                linea.setNumeroLinea(factura.getLineas().size() + 1);
                linea.setConcepto(lineaDTO.getConcepto());
                linea.setCantidad(lineaDTO.getCantidad());
                linea.setPrecioUnitario(lineaDTO.getPrecioUnitario());
                linea.setPorcentajeDescuento(lineaDTO.getPorcentajeDescuento() != null ? 
                    lineaDTO.getPorcentajeDescuento() : BigDecimal.ZERO);
                linea.setTipoIVA(tipoIVA);
                linea.calcularImporteTotal();
                
                factura.getLineas().add(linea);
            }
        }
        
        factura.calcularTotales();
        factura = facturaRepository.save(factura);
        return convertirADTO(factura);
    }
    
    public FacturaDTO actualizar(Long id, FacturaDTO dto) {
        Factura factura = facturaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
        
        FormaPago formaPago = formaPagoRepository.findById(dto.getFormaPagoId())
            .orElseThrow(() -> new RuntimeException("Forma de pago no encontrada"));
        
        factura.setFechaEmision(dto.getFechaEmision());
        factura.setFormaPago(formaPago);
        factura.setEstado(dto.getEstado());
        factura.setComentarios(dto.getComentarios());
        
        // Actualizar líneas
        factura.getLineas().clear();
        if (dto.getLineas() != null) {
            for (LineaFacturaDTO lineaDTO : dto.getLineas()) {
                TipoIVA tipoIVA = tipoIVARepository.findById(lineaDTO.getTipoIVAId())
                    .orElseThrow(() -> new RuntimeException("Tipo de IVA no encontrado"));
                
                LineaFactura linea = new LineaFactura();
                linea.setFactura(factura);
                linea.setNumeroLinea(factura.getLineas().size() + 1);
                linea.setConcepto(lineaDTO.getConcepto());
                linea.setCantidad(lineaDTO.getCantidad());
                linea.setPrecioUnitario(lineaDTO.getPrecioUnitario());
                linea.setPorcentajeDescuento(lineaDTO.getPorcentajeDescuento() != null ? 
                    lineaDTO.getPorcentajeDescuento() : BigDecimal.ZERO);
                linea.setTipoIVA(tipoIVA);
                linea.calcularImporteTotal();
                
                factura.getLineas().add(linea);
            }
        }
        
        factura.calcularTotales();
        factura = facturaRepository.save(factura);
        return convertirADTO(factura);
    }
    
    public void generarPagos(Long facturaId) {
        Factura factura = facturaRepository.findById(facturaId)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
        
        // Eliminar pagos existentes
        factura.getPagos().clear();
        
        // Generar nuevos pagos
        pagoService.generarPagosParaFactura(factura);
        facturaRepository.save(factura);
    }
    
    private String generarNumeroFactura(Integer ejercicio) {
        long contador = facturaRepository.count() + 1;
        return String.format("%d/%04d", ejercicio, contador);
    }
    
    private FacturaDTO convertirADTO(Factura factura) {
        FacturaDTO dto = new FacturaDTO();
        dto.setId(factura.getId());
        dto.setNumeroFactura(factura.getNumeroFactura());
        dto.setEjercicio(factura.getEjercicio());
        dto.setUsuarioId(factura.getUsuario().getId());
        dto.setClienteId(factura.getCliente().getId());
        dto.setClienteNombre(factura.getCliente().getNombre());
        dto.setClienteNif(factura.getCliente().getNif());
        dto.setFechaEmision(factura.getFechaEmision());
        dto.setFormaPagoId(factura.getFormaPago().getId());
        dto.setFormaPagoDescripcion(factura.getFormaPago().getDescripcion());
        dto.setEstado(factura.getEstado());
        dto.setComentarios(factura.getComentarios());
        dto.setImporteTotal(factura.getImporteTotal());
        dto.setIvaTotal(factura.getIvaTotal());
        dto.setSumaTotal(factura.getSumaTotal());
        
        if (factura.getLineas() != null) {
            dto.setLineas(factura.getLineas().stream()
                .map(this::convertirLineaADTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private LineaFacturaDTO convertirLineaADTO(LineaFactura linea) {
        LineaFacturaDTO dto = new LineaFacturaDTO();
        dto.setId(linea.getId());
        dto.setFacturaId(linea.getFactura().getId());
        dto.setNumeroLinea(linea.getNumeroLinea());
        dto.setConcepto(linea.getConcepto());
        dto.setCantidad(linea.getCantidad());
        dto.setPrecioUnitario(linea.getPrecioUnitario());
        dto.setPorcentajeDescuento(linea.getPorcentajeDescuento());
        dto.setTipoIVAId(linea.getTipoIVA().getId());
        dto.setTipoIVADescripcion(linea.getTipoIVA().getDescripcion());
        dto.setTipoIVAPorcentaje(linea.getTipoIVA().getPorcentaje());
        dto.setImporteTotal(linea.getImporteTotal());
        return dto;
    }
}
