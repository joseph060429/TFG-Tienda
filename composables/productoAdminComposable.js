// Importo el store de autenticación desde su ubicación en el archivo authStore.js
import { productoAdminStore } from "~/stores/productoAdminStore";
import { storeToRefs } from 'pinia';

// Defino y exporto el composable useAuth
export const productoAdminComposable = () => {

    // Obtengo la instancia de Pinia para acceder al store de autenticación
    const pinia = usePinia();

    // Obtengo la instancia del store de autenticación utilizando el hook useAuthStore
    const store = productoAdminStore(pinia);

    // Para llamar las variables del store(usuario) en el composable
    const { productos, productoAdmin } = storeToRefs(store)



    const listarProductosAdmin = async () => {
        return await store.listarProductosAdmin()
    }

    const listarUnProductoAdmin = async (id) => {
        return await store.listarUnProductoAdmin(id)
    }

    const limpiarProductosAdmin = () => {
        store.limpiarProductosAdmin();
    };

    const crearProducto = (formData) => {
        return store.crearProducto(formData);
    };

    const actualizarProducto = async (formData, id) => {
        return await store.actualizarProducto(formData, id);
    };

    const eliminarProducto = async (id) => {
        return await store.eliminarProducto(id);
    };

    const limpiarProducto=()=>{
        return store.limpiarProducto();
    }



    // EXPORTO LAS FUNCIONES PARA PODER USARLA EN LAS VISTAS POR EJEMPLO
    return {
        listarProductosAdmin,
        listarUnProductoAdmin,
        limpiarProductosAdmin,
        crearProducto,
        actualizarProducto,
        eliminarProducto,
        limpiarProducto,
        productoAdmin,
        productos,


    }
}