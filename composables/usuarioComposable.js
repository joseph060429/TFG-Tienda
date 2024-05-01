// Importo el store de autenticación desde su ubicación en el archivo authStore.js
import { usuarioStore } from "~/stores/usuarioStore";
import { storeToRefs } from 'pinia';

// Defino y exporto el composable useAuth
export const usuarioComposable = () => {

    // Obtengo la instancia de Pinia para acceder al store de autenticación
    const pinia = usePinia();

    // Obtengo la instancia del store de autenticación utilizando el hook useAuthStore
    const store = usuarioStore(pinia);

    // Para llamar las variables del store(usuario) en el composable
    const { usuario } = storeToRefs(store)

    // FUNCION PARA REALIZAR EL LOGIN
    const actualizacionUsuario = async (datosActualizar) => {
        return await store.actualizacionUsuario(datosActualizar)
    }

    const borrarUsuario = async () => {
        return await store.borrarUsuario()
    }

    const historialPedidos = async () => {
        return await store.historialPedidos()
    }


    // EXPORTO LAS FUNCIONES PARA PODER USARLA EN LAS VISTAS POR EJEMPLO
    return {
        actualizacionUsuario,
        borrarUsuario,
        historialPedidos,
        usuario
    }
}