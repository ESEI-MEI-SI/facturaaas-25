package es.uvigo.mei.facturaaas.security;

import es.uvigo.mei.facturaaas.model.*;
import es.uvigo.mei.facturaaas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Servicio de seguridad para verificar que el usuario autenticado
 * tenga acceso solo a sus propios recursos.
 */
@Component("resourceSecurity")
public class ResourceSecurityService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private FacturaRepository facturaRepository;
    
    @Autowired
    private FormaPagoRepository formaPagoRepository;
    
    @Autowired
    private PagoRepository pagoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Obtiene el login del usuario actualmente autenticado
     */
    private String getAuthenticatedUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }
    
    /**
     * Obtiene el ID del usuario actualmente autenticado
     */
    private Long getAuthenticatedUserId() {
        String login = getAuthenticatedUserLogin();
        if (login == null) {
            return null;
        }
        return usuarioRepository.findByLogin(login)
            .map(Usuario::getId)
            .orElse(null);
    }
    
    /**
     * Verifica si el usuario autenticado es ADMINISTRADOR
     */
    public boolean isAdmin() {
        String login = getAuthenticatedUserLogin();
        if (login == null) {
            return false;
        }
        return usuarioRepository.findByLogin(login)
            .map(u -> u.getRol() == Usuario.Rol.ADMINISTRADOR)
            .orElse(false);
    }
    
    /**
     * Verifica si el usuarioId corresponde al usuario autenticado
     */
    public boolean isOwner(Long usuarioId) {
        if (usuarioId == null) {
            return false;
        }
        Long authenticatedUserId = getAuthenticatedUserId();
        return usuarioId.equals(authenticatedUserId);
    }
    
    /**
     * Verifica si el cliente pertenece al usuario autenticado
     */
    public boolean isClienteOwner(Long clienteId) {
        if (clienteId == null) {
            return false;
        }
        Long authenticatedUserId = getAuthenticatedUserId();
        if (authenticatedUserId == null) {
            return false;
        }
        return clienteRepository.findById(clienteId)
            .map(cliente -> cliente.getUsuario().getId().equals(authenticatedUserId))
            .orElse(false);
    }
    
    /**
     * Verifica si la factura pertenece al usuario autenticado
     */
    public boolean isFacturaOwner(Long facturaId) {
        if (facturaId == null) {
            return false;
        }
        Long authenticatedUserId = getAuthenticatedUserId();
        if (authenticatedUserId == null) {
            return false;
        }
        return facturaRepository.findById(facturaId)
            .map(factura -> factura.getUsuario().getId().equals(authenticatedUserId))
            .orElse(false);
    }
    
    /**
     * Verifica si la forma de pago pertenece al usuario autenticado
     */
    public boolean isFormaPagoOwner(Long formaPagoId) {
        if (formaPagoId == null) {
            return false;
        }
        Long authenticatedUserId = getAuthenticatedUserId();
        if (authenticatedUserId == null) {
            return false;
        }
        return formaPagoRepository.findById(formaPagoId)
            .map(formaPago -> formaPago.getUsuario().getId().equals(authenticatedUserId))
            .orElse(false);
    }
    
    /**
     * Verifica si el pago pertenece al usuario autenticado
     * (a través de la factura a la que pertenece el pago)
     */
    public boolean isPagoOwner(Long pagoId) {
        if (pagoId == null) {
            return false;
        }
        Long authenticatedUserId = getAuthenticatedUserId();
        if (authenticatedUserId == null) {
            return false;
        }
        return pagoRepository.findById(pagoId)
            .map(pago -> pago.getFactura().getUsuario().getId().equals(authenticatedUserId))
            .orElse(false);
    }
    
    /**
     * Verifica si el usuario autenticado puede acceder al recurso
     * (es el propietario o es administrador)
     */
    public boolean canAccess(Long usuarioId) {
        return isAdmin() || isOwner(usuarioId);
    }
    
    /**
     * Verifica si el usuario autenticado puede acceder al cliente
     */
    public boolean canAccessCliente(Long clienteId) {
        return isAdmin() || isClienteOwner(clienteId);
    }
    
    /**
     * Verifica si el usuario autenticado puede acceder a la factura
     */
    public boolean canAccessFactura(Long facturaId) {
        return isAdmin() || isFacturaOwner(facturaId);
    }
    
    /**
     * Verifica si el usuario autenticado puede acceder a la forma de pago
     */
    public boolean canAccessFormaPago(Long formaPagoId) {
        return isAdmin() || isFormaPagoOwner(formaPagoId);
    }
    
    /**
     * Verifica si el usuario autenticado puede acceder al pago
     */
    public boolean canAccessPago(Long pagoId) {
        return isAdmin() || isPagoOwner(pagoId);
    }
}
