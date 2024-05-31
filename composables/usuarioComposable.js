// Importo el store de autenticación desde su ubicación en el archivo authStore.js
import { usuarioStore } from "~/stores/usuarioStore";
import { storeToRefs } from "pinia";

// Defino y exporto el composable useAuth
export const usuarioComposable = () => {
  // Obtengo la instancia de Pinia para acceder al store de autenticación
  const pinia = usePinia();

  // Obtengo la instancia del store de autenticación utilizando el hook useAuthStore
  const store = usuarioStore(pinia);

  // Para llamar las variables del store(usuario) en el composable
  const { usuario } = storeToRefs(store);

  // FUNCION PARA REALIZAR EL LOGIN
  const actualizacionUsuario = async (datosActualizar) => {
    return await store.actualizacionUsuario(datosActualizar);
  };

  const reset = () => {
    store.$reset();
  };

  // COMPOSABLE PARA BORRAR USUARIO
  const borrarUsuario = async () => {
    return await store.borrarUsuario();
  };

  // COMPOSABLE PARA VER EL HISTORIAL DE PEDIDOS DEL USUARIO
  const historialPedidos = async () => {
    return await store.historialPedidos();
  };

  // COMPOSABLE PARA QUE EL USUARIO CANCELE SUS PEDIDOS
  const cancelarPedidos = async (numPedido) => {
    return await store.cancelarPedidos(numPedido);
  };

  // COMPOSABLE PARA QUE EL USUARIO AÑADA AL CARRITO
  const anadirAlCarrito = async (productoId, cantidad) => {
    return await store.anadirAlCarrito(productoId, cantidad);
  };

  // COMPOSABLE PARA QUE EL USUARIO VEA SU CARRITO
  const verMiCarrito = async (productoId, nuevaCantidad) => {
    return await store.verMiCarrito(productoId, nuevaCantidad);
  };

  // COMPOSABLE PARA QUE EL USUARIO VEA SU CARRITO
  const eliminarProductoCarrito = async (_idCarrito) => {
    return await store.eliminarProductoCarrito(_idCarrito);
  };

  //COMPOSABLE PARA TRAER LAS DIRECCIONES DEL USUARIO
  const direccionesUsuario = async () => {
    return await store.direccionesUsuario();
  };

  //COMPOSABLE PARA AÑADIR LAS DIRECCIONES DE ENVIO DEL USUARIO
  const anadirDireccionEnvio = async (direccionEnvio) => {
    return await store.anadirDireccionEnvio(direccionEnvio);
  };


  //COMPOSABLE PARA AÑADIR LAS DIRECCIONES DE FACTURACION EMPRESA-AUTONOMO DEL USUARIO
  const anadirDireccionFacturacionEmpreAuto = async (direccionFacturacionEmpreAuto) => {
    return await store.anadirDireccionFacturacionEmpreAuto(direccionFacturacionEmpreAuto);
  };


  //COMPOSABLE PARA AÑADIR LAS DIRECCIONES DE FACTURACION PARTICULAR DEL USUARIO
  const anadirDireccionFacturacionParticular = async (direccionFacturacionParticular) => {
    return await store.anadirDireccionFacturacionParticular(direccionFacturacionParticular);
  };


  //COMPOSABLE PARA ELIMINAR LAS DIRECCIONES DE ENVIO DEL USUARIO
  const eliminarDireccionEnvio = async (index) => {
    return await store.eliminarDireccionEnvio(index);
  };

  //COMPOSABLE PARA ELIMINAR LAS DIRECCIONES DE FACTURACION DEL USUARIO
  const eliminarDireccionFacturacion = async (index) => {
    return await store.eliminarDireccionFacturacion(index);
  };


    //COMPOSABLE PARA ACTUALIZAR LAS DIRECCIONES DE ENVIO DEL USUARIO
    const actualizarDireccionEnvio = async (direccionEnvio) => {
      return await store.actualizarDireccionEnvio(direccionEnvio);
    };
  


  // COMPOSABLE PARA LIMPIAR LOS PEDIDOS DEL USUARIO DE LA STORE
  const limpiarPedidos = () => {
    store.limpiarPedidos();
  };

  const limpiarCarrito = () => {
    store.limpiarCarrito();
  };







  // EXPORTO LAS FUNCIONES PARA PODER USARLA EN LAS VISTAS POR EJEMPLO
  return {
    actualizacionUsuario,
    borrarUsuario,
    historialPedidos,
    usuario,
    limpiarPedidos,
    cancelarPedidos,
    anadirAlCarrito,
    verMiCarrito,
    eliminarProductoCarrito,
    limpiarCarrito,
    reset,
    direccionesUsuario,
    anadirDireccionEnvio,
    anadirDireccionFacturacionEmpreAuto,
    anadirDireccionFacturacionParticular,
    eliminarDireccionEnvio,
    eliminarDireccionFacturacion,
    actualizarDireccionEnvio
  };
};
