# CONTEXTO
_FACTURAaaS_ es una herramienta para la edición y gestión de facturas. 

Se trata de una solución _SaaS_ (_Software as a Service_) orientada a simplificar la creación y gestión de facturas y pagos a usuarios particulares, autónomos o pequeñas empresas.

Este perfil de usuario presenta un volumen de facturación reducido, necesitando emitir facturas de forma puntual, por lo que una solución más compleja, como un ERP o software de gestión empresarial similar, resulta excesiva.

# HISTORIAS DE USUARIO

## H1. Login de usuarios 
### Descripción
Como usuario registrado en la aplicación desde la página principal será posible loguearse en la aplicación
mediante un nombre de usuario (login) y una contraseña (password).

### Criterios de aceptación
* Un usuario registrado aportando las credenciales correctas accederá al _home_ que corresponda con su _rol_ de usuario (ADMINISTRADOR o USUARIO) 
* Un usuario que no aporte un nombre de usuario o una contraseña válida volverá a presentársele la página principal para introduzca sus credenciales.

### Detalles
| **Prioridad** |    **Estado**   | **Sprint** | **Estimación** | **Dedicación** |
|---------------|-----------------|------------|----------------|----------------|
| Alta          | Implementada    |   1        |  __ horas      |  __ horas      |

---


## H2. _Home_ de usuarios administradores 
### Descripción
Como usuario logueado con el _rol_ ADMINISTRADOR se tendrá acceso a al _home_ de administradores, desde el cual
se podrá acceder a las funcionalidades de _Gestión de Usuarios_, _Gestión de Tipos de IVA_, _Gestión de Formas de Pago_. También se tendrá una acción _Desconectar_ con la que cerrar la sesión.

### Criterios de aceptación
* Se mostrará a los usuarios con _rol_ ADMINISTRADOR un menú con las opciones
  * Usuarios
  * Tipos de IVA
  * Formas de Pago
  * Desconectar
* Desde la opción _Desconectar_ se finalizará la sesión, retornando a la página inicial de la aplicación

### Detalles
| **Prioridad** |    **Estado**   | **Sprint** | **Estimación** | **Dedicación** |
|---------------|-----------------|------------|----------------|----------------|
| Alta          | Implementada    |   1        |  __ horas      |  __ horas      |


## H3. Gestión de _Usuarios_ 
### Descripción
Como usuario logueado con el _rol_ ADMINISTRADOR, desde la opción _Usuarios_ se tendrá acceso a la lista de usuarios registrados. Seleccionando uno de esos usuarios se podrá eliminar el mismo o acceder a la opción de editar sus datos. También será posible seleccionar la creación de un nuevo usuario.

### Criterios de aceptación
* Se mostrará una lista con los usuarios actualmente registrados, indicando su datos esenciales (login, nombre de usuario, email, fecha de registro/creación, fecha de último acceso, _rol_ de usuario, estado [activo/inactivo])
* Se podrá seleccionar un usuario de esa lista y mediante un botón _Editar_ acceder a la edición de los datos del usuario seleccionado. Una vez completada esa edición se actualizará la lista de usuarios.
* Se podrá seleccionar un usuario de esa lista y mediante un botón _Borrar_ realizar la eliminación lógica de ese usuario de la aplicación, estableciento su estado a _inactivo_. Una vez completada esa edición se actualizará la lista de usuarios.
* Mediante un botón _Nuevo_ se accederá a la creación de un nuevo usuario


### Detalles
| **Prioridad** |    **Estado**   | **Sprint** | **Estimación** | **Dedicación** |
|---------------|-----------------|------------|----------------|----------------|
| Alta          | Implementada    |   1        |  __ horas      |  __ horas      |


## H4. Creación/edición de datos de un _Usuario_ 
### Descripción
Como usuario logueado con el _rol_ ADMINISTRADOR, desde la vista de creación/edición de usuarios se podrán editar los datos esenciales de un usuario, guardándolos una vez completada la creación/edición.

Datos a editar:
* login (sólo en el caso de usuarios nuevos)
* email (sólo en el caso de usuarios nuevos)
* nombre (sólo en el caso de usuarios nuevos)
* password (sólo en el caso de usuarios nuevos)
* rol del usuario (ADMINISTRADOR o USUARIO)
* estado [activo/inactivo]

### Criterios de aceptación
* Se mostrarán los datos del usuario selecionado (o campos vacíos en el caso de la creación de un nuevo usario) * Para los usuarios ya existentes sólo se permitirá modificar su _rol_ y su estado (activo o inactivo)
* En la creación de nuevos usuarios se podrá indicar su nombre de usuario (_login_) y una contrseña inicial.
* Al pulsar el botón _Guardar_ se actualizarán los datos en la BD en el caso de la edición de un usuario existente o se insertarán los datos de un nuevo usuario en el caso de la creación de uno nuevo.
* En el caso de intentar guardar un nuevo usuario con un nombre de usuario (_login_) ya existente se informará del error, dando opción a introducir un nombre de usuario alternativo.
* Al pulsar el botón _Cancelar_ se retornará a la vista anterior, desechando los cambios pendientes de actualizar.

### Detalles
| **Prioridad** |    **Estado**   | **Sprint** | **Estimación** | **Dedicación** |
|---------------|-----------------|------------|----------------|----------------|
| Alta          | Implementada    |   1        |  __ horas      |  __ horas      |

## H5. Gestión de _Tipos de IVA_ 
### Descripción
Como usuario logueado con el _rol_ ADMINISTRADOR, desde la opción _Tipos de IVA_ se tendrá acceso a la lista de tipos de IVA definidos en el sistema. Seleccionando uno de esos _Tipos de IVA_ se podrá eliminar el mismo o acceder a la opción de editar sus datos. También será posible seleccionar la creación de un nuevo _Tipo de IVA_.

### Criterios de aceptación
* Se mostrará una lista con los _Tipos de IVA_ actualmente registrados, indicando su datos (descripción, porcentaje a aplicar, estado [activo/inactivo])
* Se podrá seleccionar un _Tipo de IVA_ de esa lista y mediante un botón _Editar_ acceder a la edición del _Tipo de IVA_ seleccionado. Una vez completada esa edición se actualizará la lista de _Tipos de IVA_ definidos.
* Se podrá seleccionar un _Tipo de IVA_ de esa lista y mediante un botón _Borrar_ realizar la eliminación lógica de ese _Tipo de IVA_ de la aplicación, estableciento su estado a _inactivo_. Una vez completada esa edición se actualizará la lista de _Tipos de IVA_.
* Mediante un botón _Nuevo_ se accederá a la definción de un nuevo _Tipo de IVA_.


### Detalles
| **Prioridad** |    **Estado**   | **Sprint** | **Estimación** | **Dedicación** |
|---------------|-----------------|------------|----------------|----------------|
| Alta          | Implementada    |   1        |  __ horas      |  __ horas      |


## H6. Creación/edición de datos de un _Tipo de IVA_ 
### Descripción
Como usuario logueado con el _rol_ ADMINISTRADOR, desde la vista de creación/edición de _Tipos de IVA_ se podrán editar los datos esenciales de uno de estos tipos, guardándolos una vez completada la creación/edición.
Datos a editar.
* descripción ('IVA normal', 'IVA reducido', 'IVA superreducido', 'Sin IVA')
* porcentaje a aplicar (21%, 10%, 4%, 0%)
* esatdo (activo, inactivo)

### Criterios de aceptación
* Se mostrarán los datos del _Tipo de IVA_ selecionado (o campos vacíos en el caso de la creación de uno nuevo)
*  Al pulsar el botón _Guardar_ se actualizarán los datos en la BD en el caso de la edición de un _Tipo de IVA_ ya existente o se insertarán los datos de un nuevo _Tipo de IVA_ en el caso de la creación de uno nuevo.
* Al pulsar el botón _Cancelar_ se retornará a la vista anterior, desechando los cambios pendientes de actualizar.

### Detalles
| **Prioridad** |    **Estado**   | **Sprint** | **Estimación** | **Dedicación** |
|---------------|-----------------|------------|----------------|----------------|
| Alta          | Implementada    |   1        |  __ horas      |  __ horas      |




## H7. Gestión de _Formas de Pago_ 
### Descripción
Como usuario logueado con el _rol_ ADMINISTRADOR, desde la opción _Formas de Pago_ se tendrá acceso a la lista de _Formas de Pago_ definidas en el sistema. Seleccionando una de esas _Formas de Pago_ ésta se podrá eliminar  o acceder a la opción de editar sus datos. También será posible seleccionar la creación de una nueva _Forma de Pago_.

### Criterios de aceptación
* Se mostrará una lista con las _Formas de Pago_ actualmente registradas, indicando su datos (descripción, número de pagos, periodicidad, estado [activa/inactiva])
* Se podrá seleccionar una _Forma de Pago_ de esa lista y mediante un botón _Editar_ acceder a la edición de la _Forma de Pago_ seleccionada. Una vez completada esa edición se actualizará la lista de _Formas de Pago_ definidas.
* Se podrá seleccionar una _Forma de Pago_ de esa lista y mediante un botón _Borrar_ realizar la eliminación lógica de esa _Forma de Pago_ de la aplicación, estableciento su estado a _inactiva_. Una vez completada esa edición se actualizará la lista de _Formas de Pago_.
* Mediante un botón _Nueva_ se accederá a la definción de una nueva _Forma de Pago_.


### Detalles
| **Prioridad** |    **Estado**   | **Sprint** | **Estimación** | **Dedicación** |
|---------------|-----------------|------------|----------------|----------------|
| Alta          | Implementada    |   1        |  __ horas      |  __ horas      |


## H8. Creación/edición de datos de una _Forma de Pago_ 
### Descripción
Como usuario logueado con el _rol_ ADMINISTRADOR, desde la vista de creación/edición de _Tipos de IVA_ se podrán editar los datos esenciales de uno de estos tipos, guardándolos una vez completada la creación/edición.
Datos a editar.
* descripción ('contado', 'transferencia', 'transferencia a 30 días', 'transferencias a 30-60-90 días')
* número de pagos previstos
* peridicidad (en días)
* estado [activa/inactiva]

### Criterios de aceptación
* Se mostrarán los datos de la _Forma de Pago_ selecionada (o campos vacíos en el caso de la creación de una nueva) 
* Al pulsar el botón _Guardar_ se actualizarán los datos en la BD en el caso de la edición de una _Forma de Pago_ ya existente o se insertarán los datos de una nueva _Forma de Pago_ en el caso de la creación de una nueva.
* Al pulsar el botón _Cancelar_ se retornará a la vista anterior, desechando los cambios pendientes de actualizar.

### Detalles
| **Prioridad** |    **Estado**   | **Sprint** | **Estimación** | **Dedicación** |
|---------------|-----------------|------------|----------------|----------------|
| Alta          | Implementada    |   1        |  __ horas      |  __ horas      |


## H9. _Home_ de usuarios  
### Descripción
Como usuario logueado con el _rol_ USUARIO se tendrá acceso a al _home_ de usuario, desde el cual
se podrá acceder a las funcionalidades de _Perfil_, _Datos de Facturación_, _Gestión de Clientes_, _Gestión de Facturas_, _Gestión de Vencimientos/Pagos_. También se tendrá una acción _Desconectar_ con la que cerrar la sesión.

### Criterios de aceptación
* Se mostrará a los usuarios con _rol_ USUARIO un menú con las opciones
  * Perfil
  * Datos de Facturación
  * Clientes
  * Facturas
  * Pagos
  * Desconectar
* Desde la opción _Desconectar_ se finalizará la sesión, retornando a la página inicial de la aplicación

### Detalles
| **Prioridad** |    **Estado**   | **Sprint** | **Estimación** | **Dedicación** |
|---------------|-----------------|------------|----------------|----------------|
| Alta          | Implementada    |   1        |  __ horas      |  __ horas      |

## H10. Edición del _Perfil de Usuario_  
### Descripción
Como usuario logueado con el _rol_ USUARIO desde la opción de _Perfil de Usuario_ se tendrá acceso al formulario para cambiar la contraseña de acceso y el -email de contacto.

### Criterios de aceptación
* Se mostrará a los usuarios sus datos, dando opción a editarlos
  * login (no editable)
  * email (editable)
  * nombre (editable)
  * password (editable)
  * repetición de password (editable)
  * fecha de creación (no editable)
  * fecha de último acceso (no editable)
* Al pulsar en _Guardar_ se almacenarán los cambios introducidos, despues de verificar la coincidencia de las dos contraseñas introducidas
* En caso de que los dos pasword indicados no coincidan, se informará del error, dando opción a introducirlos correctamente
* Al pulsar _Cancelar_ se retornará al _home_ del usuario, desechando los cambios realizados

## H11. Edición de los _Datos de Facturación_  
### Descripción
Como usuario logueado con el _rol_ USUARIO desde la opción de _Datos de Facturación_ se tendrá acceso al formulario para cambiar los datos de facturación del usuario.

### Criterios de aceptación
* Se mostrará a los usuarios sus _Datos de Facturación_, dando opción a editarlos
  * nombre comercial
  * nif
  * dirección comercial (domicilio, localidad, cod. postal, provincia)
  * telefono de contacto
  * email de contacto
  * cuenta bancaria para pagos (como String simple)
  * tipo (particular, autónomo, sociedad limitada, sociedad anónima, cooperativa, ong, otro)
  * tipo de IVA por defecto
  * forma de pago por defecto
* La selección de tipo, tipo de IVA por defecto y forma de pago por defecto se realizará mediante la seleción en una lista desplegable
* Al pulsar en _Guardar_ se almacenarán los cambios introducidos
* En caso de que los datos introducidos presenten algún error o existan datos requeridos no introducidos, se informará del error, dando opción a introducirlos correctamente
* Al pulsar _Cancelar_ se retornará al _home_ del usuario, desechando los cambios realizados


### Detalles
| **Prioridad** |    **Estado**   | **Sprint** | **Estimación** | **Dedicación** |
|---------------|-----------------|------------|----------------|----------------|
| Alta          | Implementada    |   1        |  __ horas      |  __ horas      |


## H12. Gestión de _Clientes_ 
### Descripción
Como usuario logueado con el _rol_ USUARIO, desde la opción _Clientes_ se tendrá acceso a la lista de _Clientes_ vinculados al usuario actual. Seleccionando uno de esos _Clientes_ se podrán editar sus datos. También será posible seleccionar la creación de un nuevo _Cliente_ vinculado al usuario actual.

### Criterios de aceptación
* Se mostrará una lista con los _Clientes_ dados de alta por el usuario actual, indicando su datos esenciales (nombre , nif, dirección (domicilio, localidad, provincia), etc)
* Se podrá filtrar los _Clientes_ a mostrar en esa lista, indicando un patrón a buscar en su nombre o su localidad.
* Se podrá seleccionar un _Cliente_ de esa lista y mediante un botón _Editar_ acceder a la edición de  sus datos. Una vez completada esa edición se actualizará la lista de _Clientes_.
* No se contempla el borrado de _Clientes_
* Mediante un botón _Nuevo_ se accederá a la creación de un nuevo _Cliente_


### Detalles
| **Prioridad** |    **Estado**   | **Sprint** | **Estimación** | **Dedicación** |
|---------------|-----------------|------------|----------------|----------------|
| Alta          | Implementada    |   1        |  __ horas      |  __ horas      |


## H13. Creación/edición de datos de un _Cliente_ 
### Descripción
Como usuario logueado con el _rol_ USUARIO, desde la vista de creación/edición de _Clientes_ se podrán editar sus datos, guardándolos una vez completada la creación/edición.

Datos a editar:
* nombre
* nif
* dirección de facturación (domicilio, localidad, cod. postal, provincia)
* email de contacto
* teléfono de contacto
* cuenta bancaria (como String simple)

### Criterios de aceptación
* Se mostrarán los datos del _Cliente_ selecionado (o campos vacíos en el caso de la creación de uno nuevo) 
* Al pulsar el botón _Guardar_ se actualizarán los datos en la BD en el caso de la edición de un _Cliente_ existente o se insertarán los datos de un nuevo _Cliente_ en el caso de la creación de uno nuevo.
* En caso de que los datos introducidos presenten algún error o existan datos requeridos no introducidos, se informará del error, dando opción a introducirlos correctamente
* Al pulsar el botón _Cancelar_ se retornará a la vista anterior, desechando los cambios pendientes de actualizar.

### Detalles
| **Prioridad** |    **Estado**   | **Sprint** | **Estimación** | **Dedicación** |
|---------------|-----------------|------------|----------------|----------------|
| Alta          | Implementada    |   1        |  __ horas      |  __ horas      |




## H14. Gestión de _Facturas_ (pendiente, Sprint 2)
### Descripción
Como usuario logueado con el _rol_ USUARIO, desde la opción _Facturas_ se tendrá acceso a la lista de _Facturas_ creadas por usuario actual. Seleccionando una de estas _Facturas_, ésta se podrá eliminar  o acceder a la opción de editar sus datos. También será posible seleccionar la creación de una nueva _Factura_ vinculada al usuario actual.

### Criterios de aceptación
* Se mostrará una lista con las _Facturas_ creadas por el usuario actual, indicando su datos esenciales (número de factura, nombre y nif del cliente, estado (emitada, pagada, anulada), importe, iva, suma total, etc)
* Se podrá filtrar las _Facturas_ a mostrar en esa lista, seleccionando en una lista desplegable el _Cliente_
* Se podrá seleccionar una _Factura_ de esa lista y mediante un botón _Editar_ acceder a la edición de  sus datos. Una vez completada esa edición se actualizará la lista de _Facturas_.
* Se podrá seleccionar una _Factura_ de esa lista y mediante un botón _Generar Pagos_  se generarán en la BD las entidades _Pago_ que correspondan en función de la _Forma de Pago_ vincualda a la _Factura_ seleccionada
  * en caso de que ya existieran entidades  _Pago_ vinculadas a dicha _Factura_ estas se eliminarán para ser sustitutidas por los nuevos _Pagos_ generados 
  * se crearán tantas entidades _Pago_ como _número de pagos_ se indiquen en la correspondiente _Forma de Pago_
  * se calcularán las _fechas de vencimiento_ a vincular a cada _Pago_ en base a la _periodicidad_ indicada en la correspondiente _Forma de Pago_
  * incialmente esos _Pagos_ se crearán con el estado _pendiente_
* Por ahora no se contempla el borrado de _Facturas_
* Mediante un botón _Nueva_ se accederá a la creación de una nueva _Factura_


### Detalles
| **Prioridad** |    **Estado**   | **Sprint** | **Estimación** | **Dedicación** |
|---------------|-----------------|------------|----------------|----------------|
| Alta          | Pendiente       |   2        |  __ horas      |  __ horas      |


## H15. Creación/edición de datos de una _Factura_ (pendiente, Sprint 2)
### Descripción
Como usuario logueado con el _rol_ USUARIO, desde la vista de creación/edición de _Factura_ se podrán editar sus datos y vincularle líneas de factura, guardándolos una vez completada la creación/edición.

Datos a editar (cabecera de factura)
* número de factura (generado por el sistema, no editable)
* ejercicio
* cliente (seleccionado de una lista desplegable)
* fecha de emisión (editable)
* forma de pago (inicializado con la forma de pago por defecto del usuario actual, seleccionable de una lista desplegable)
* estado (emitida, anulada, pagada, reclamada, abonada)
* comentarios

Datos a editar (en cada línea de factura)
* número de línea (generado por el sistema, no editable)
* concepto
* cantidad (por defecto 1)
* precio unitario
* procentaje de descuento
* tipo de IVA (inicializado con el tipo de IVA por defecto del usuario actual, seleccionable de una lista desplegable)
* importe total (calculado, no editable)


### Criterios de aceptación
* Se mostrarán los datos de la _Factura_ selecionada (o campos vacíos en el caso de la creación de uno nuevo) 
* Se mostará la lista de _Líneas de Factura_ que componen la _Factura_ actual, permitiendo
  * añadir una nueva _Linea de Factura_ con los datos aportados
  * seleccionar y editar una _Línea de Factura_ existente
  * eliminar la _Línea de Factura_ seleccionada
* Al pulsar el botón _Guardar_ se actualizarán los datos en la BD en el caso de la edición de una _Factura_ existente o se insertarán los datos de una nueva _Factura_ en el caso de la creación de una nueva.
* En caso de que los datos introducidos presenten algún error o existan datos requeridos no introducidos, se informará del error, dando opción a introducirlos correctamente
* Al pulsar el botón _Cancelar_ se retornará a la vista anterior, desechando los cambios pendientes de actualizar.

### Detalles
| **Prioridad** |    **Estado**   | **Sprint** | **Estimación** | **Dedicación** |
|---------------|-----------------|------------|----------------|----------------|
| Alta          | Pendiente       |   2        |  __ horas      |  __ horas      |


## H16. Gestión de _Pagos_ (pendiente, Sprint 2)
### Descripción
Como usuario logueado con el _rol_ USUARIO, desde la opción _Pagos_ se tendrá acceso a la lista de _Pagos_ previstos para el usuario actual. Seleccionando uno de estos _Pagos_, ésta se podrá cambiar su estado.

### Criterios de aceptación
* Se mostrará una lista con los _Pagos_ previstos para el usuario actual, indicando sus datos esenciales (número de factura, nombre y nif del cliente, estado (pendiente, pagado, anulado), importe, fecha de vencimiento)
* Se podrá filtrar los _PAgos_ a mostrar en esa lista, seleccionando en una lista desplegable el _Cliente_ al que correspondan
* Se podrá seleccionar un _Pago_ de esa lista modificar su estado.
* No se contempla el borrado de _Pagos_ ni la creación manual de _Pagos_


### Detalles
| **Prioridad** |    **Estado**   | **Sprint** | **Estimación** | **Dedicación** |
|---------------|-----------------|------------|----------------|----------------|
| Alta          | Pendiente       |   2        |  __ horas      |  __ horas      |



