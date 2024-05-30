// Importo el store de autenticación desde su ubicación en el archivo authStore.js
import { productoStore } from "~/stores/productoStore";
import { storeToRefs } from 'pinia';

// Defino y exporto el composable useAuth
export const productoComposable = () => {

    // Obtengo la instancia de Pinia para acceder al store de autenticación
    const pinia = usePinia();

    // Obtengo la instancia del store de autenticación utilizando el hook productoStore
    const store = productoStore(pinia);

    // Para llamar las variables del store(usuario) en el composable
    const { productos, producto } = storeToRefs(store)



    const listarProductos = async () => {
        return await store.listarProductos()
    }

    const listarUnProducto = async (id) => {
        return await store.listarUnProducto(id)
    }

    const buscarProductoPorEspecificacion = async (especificacionProducto) => {
        return await store.buscarProductoPorEspecificacion(especificacionProducto)
    }

    const buscarProductosPorRangoPrecio = async(precioMinimo, precioMaximo) =>{
        return await store.buscarProductosPorRangoPrecio(precioMinimo,precioMaximo)
    }



    // EXPORTO LAS FUNCIONES PARA PODER USARLA EN LAS VISTAS POR EJEMPLO
    return {
        listarProductos,
        listarUnProducto,
        buscarProductoPorEspecificacion,
        buscarProductosPorRangoPrecio,
        // limpiarProductos,
        productos,
        producto


    }
}