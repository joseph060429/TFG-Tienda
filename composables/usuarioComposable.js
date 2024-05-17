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

  const anadirAlCarrito = async (productoId, cantidad) => {
    return await store.anadirAlCarrito(productoId, cantidad);
  };

  const verMiCarrito = async (productoId, nuevaCantidad) => {
    return await store.verMiCarrito(productoId, nuevaCantidad);
  };

  // COMPOSABLE PARA LIMPIAR LOS PEDIDOS DEL USUARIO DE LA STORE
  const limpiarPedidos = () => {
    store.limpiarPedidos();
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
    reset
  };
};
