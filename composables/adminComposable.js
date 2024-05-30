// Importo el store de autenticación desde su ubicación en el archivo authStore.js
import { adminStore } from "~/stores/adminStore";
import { storeToRefs } from "pinia";

// Defino y exporto el composable useAuth
export const adminComposable = () => {
  // Obtengo la instancia de Pinia para acceder al store de autenticación
  const pinia = usePinia();

  // Obtengo la instancia del store de autenticación utilizando el hook productoStore
  const store = adminStore(pinia);

  // Para llamar las variables del store(admin) en el composable
  const { usuarios, pedidos } = storeToRefs(store);

  const listarUsuarios = async () => {
    return await store.listarUsuarios();
  };

  const actualizarRol = async (id, nuevoRol) => {
    return await store.actualizarRol(id, nuevoRol);
  };

  const eliminarUsuario = async (email) => {
    return await store.eliminarUsuario(email);
  };
  const actualizacionUsuarioAdmin = async (id, datosActualizar) => {
    return await store.actualizacionUsuarioAdmin(id, datosActualizar);
  };

  const listarPedidos = async () => {
    return await store.listarPedidos();
  };

  const actualizarEstadoEnviado = async (pedidoId, estado) => {
    return await store.actualizarEstadoEnviado(pedidoId, estado);
  };

  const actualizarEstadoDireccionErronea = async (pedidoId, estado) => {
    return await store.actualizarEstadoDireccionErronea(pedidoId, estado);
  };


  const actualizarEstadoEntregado = async (pedidoId, estado) => {
    return await store.actualizarEstadoEntregado(pedidoId, estado);
  };


  const actualizarEstadoReproParaEntrega = async (pedidoId, estado) => {
    return await store.actualizarEstadoReproParaEntrega(pedidoId, estado);
  };


  const enviarCorreoRetraso = async (email) => {
    return await store.enviarCorreoRetraso(email);
  };

  



  // EXPORTO LAS FUNCIONES PARA PODER USARLA EN LAS VISTAS POR EJEMPLO
  return {
    listarUsuarios,
    actualizarRol,
    eliminarUsuario,
    actualizacionUsuarioAdmin,
    listarPedidos,
    usuarios,
    pedidos,
    actualizarEstadoEnviado,
    actualizarEstadoDireccionErronea,
    actualizarEstadoEntregado,
    actualizarEstadoReproParaEntrega,
    enviarCorreoRetraso
  };
};
