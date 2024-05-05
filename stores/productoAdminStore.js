import { defineStore } from "pinia";
import { useAxiosInstance } from "~/utils/axios";

// DEFINO Y EXPORTO LA STORE 'useAuthStore'
export const productoAdminStore = defineStore({
  // Identificador unico para el store
  id: "productoAdmin",
  state: () => ({
    // Defino el estado inicial de la store, es este el nombre que le pongo para luego llamarlo en el composable
    productos: [],
    productoAdmin: {},
    loggedIn: false,
  }),

  /* Poner useAxiosInstance() con cada peticion al back,
      no se por que no se puede definir axios tal cual arriba del todo
      */

  // Defino las acciones (métodos) que pueden modificar el estado del store
  actions: {
    // STORE VER TODOS LOS PRODUCTOS SIENDO ADMIN
    async listarProductosAdmin() {
      try {
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().get(
          "/admin/productos/listarProductos",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        // Agrego los datos de los productos recibidos en la respuesta al array de productos
        this.productos = response.data;
        return response;
      } catch (error) {
        console.log("Error en VER LISTAR PRODUCTOS STORE ==> ", error);
        return error.response;
      }
    },

    limpiarProductosAdmin() {
      this.productos = [];
      this.productoAdmin = [];
    },

    // STORE VER UN PRODUCTO SIENDO USUARIO
    async listarUnProductoAdmin(id) {
      try {
        const response = await useAxiosInstance().get(
          "/admin/productos/listarUnProducto",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
            params: {
              _id: id,
            },
          }
        );
        // Agrego los datos del producto recibido en la respuesta al objeto producto
        this.productoAdmin = response.data;
        console.log(response, "response desde listarProductosAdmin");
        return response;
      } catch (error) {
        console.log("Error en VER LISTAR PRODUCTOS STORE ==> ", error);
        return error.response;
      }
    },

    
  },
});

export default productoAdminStore;
