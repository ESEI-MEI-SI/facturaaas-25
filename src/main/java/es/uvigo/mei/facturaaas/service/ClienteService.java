package es.uvigo.mei.facturaaas.service;

import es.uvigo.mei.facturaaas.dto.ClienteDTO;
import es.uvigo.mei.facturaaas.model.Cliente;
import es.uvigo.mei.facturaaas.model.Usuario;
import es.uvigo.mei.facturaaas.repository.ClienteRepository;
import es.uvigo.mei.facturaaas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public List<ClienteDTO> obtenerPorUsuario(Long usuarioId) {
        return clienteRepository.findByUsuarioId(usuarioId).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    public List<ClienteDTO> buscarPorUsuarioYPatron(Long usuarioId, String patron) {
        return clienteRepository.buscarPorUsuarioYPatron(usuarioId, patron).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    public ClienteDTO obtenerPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return convertirADTO(cliente);
    }
    
    public ClienteDTO crear(ClienteDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setNombre(dto.getNombre());
        cliente.setNif(dto.getNif());
        cliente.setDomicilio(dto.getDomicilio());
        cliente.setLocalidad(dto.getLocalidad());
        cliente.setCodigoPostal(dto.getCodigoPostal());
        cliente.setProvincia(dto.getProvincia());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setCuentaBancaria(dto.getCuentaBancaria());
        
        cliente = clienteRepository.save(cliente);
        return convertirADTO(cliente);
    }
    
    public ClienteDTO actualizar(Long id, ClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        cliente.setNombre(dto.getNombre());
        cliente.setNif(dto.getNif());
        cliente.setDomicilio(dto.getDomicilio());
        cliente.setLocalidad(dto.getLocalidad());
        cliente.setCodigoPostal(dto.getCodigoPostal());
        cliente.setProvincia(dto.getProvincia());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setCuentaBancaria(dto.getCuentaBancaria());
        
        cliente = clienteRepository.save(cliente);
        return convertirADTO(cliente);
    }
    
    private ClienteDTO convertirADTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setUsuarioId(cliente.getUsuario().getId());
        dto.setNombre(cliente.getNombre());
        dto.setNif(cliente.getNif());
        dto.setDomicilio(cliente.getDomicilio());
        dto.setLocalidad(cliente.getLocalidad());
        dto.setCodigoPostal(cliente.getCodigoPostal());
        dto.setProvincia(cliente.getProvincia());
        dto.setEmail(cliente.getEmail());
        dto.setTelefono(cliente.getTelefono());
        dto.setCuentaBancaria(cliente.getCuentaBancaria());
        return dto;
    }
}
