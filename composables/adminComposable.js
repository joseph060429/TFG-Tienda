// Importo el store de autenticación desde su ubicación en el archivo authStore.js
import { adminStore } from "~/stores/adminStore";
import { storeToRefs } from 'pinia';

// Defino y exporto el composable useAuth
export const adminComposable = () => {

    // Obtengo la instancia de Pinia para acceder al store de autenticación
    const pinia = usePinia();

    // Obtengo la instancia del store de autenticación utilizando el hook productoStore
    const store = adminStore(pinia);

    // Para llamar las variables del store(admin) en el composable
    const { usuarios } = storeToRefs(store)



    const listarUsuarios = async () => {
        return await store.listarUsuarios()
    }

    const actualizarRol = async (id, nuevoRol) => {
        return await store.actualizarRol(id, nuevoRol)
    }

    const eliminarUsuario = async (email) => {
        return await store.eliminarUsuario(email)
    }

    // EXPORTO LAS FUNCIONES PARA PODER USARLA EN LAS VISTAS POR EJEMPLO
    return {
        listarUsuarios,
        actualizarRol,
        eliminarUsuario,
        usuarios
    }
}