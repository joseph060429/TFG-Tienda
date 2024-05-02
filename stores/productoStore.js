import { defineStore } from "pinia";
import { useAxiosInstance } from "~/utils/axios";

// DEFINO Y EXPORTO LA STORE 'useAuthStore'
export const productoStore = defineStore({
    // Identificador unico para el store
    id: "producto",
    state: () => ({
        // Defino el estado inicial de la store, es este el nombre que le pongo para luego llamarlo en el composable
        productos: [],
        loggedIn: false,
    }),

    /* Poner useAxiosInstance() con cada peticion al back,
      no se por que no se puede definir axios tal cual arriba del todo
      */

    // Defino las acciones (métodos) que pueden modificar el estado del store
    actions: {
        // STORE VER TODOS LOS PRODUCTOS
        async listarProductos() {
            try {
                const response = await useAxiosInstance().get("/listarProductos");
                // Agrego los datos de los productos recibidos en la respuesta al array de productos
                this.productos =response.data;
                return response;
            } catch (error) {
                console.log("Error en VER LISTAR PRODUCTOS STORE ==> ", error);
                return error.response;
            }

        },













        
    },
});

export default productoStore;
