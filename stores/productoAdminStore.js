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
    // Para crear los datos del producto
    // categoriaProducto: "",
    // nombreProducto: "",
    // descripcionProducto: "",
    // precioProducto: "",
    // cantidadProducto: "",
    // marcaProducto: "",
    // especificacionesTecnicas: "",




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
      const token = localStorage.getItem("token");
      try {
        const response = await useAxiosInstance().get(
          "/admin/productos/listarUnProducto",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
            params: {
              id: id,
            },
          }
        );
        // Agrego los datos del producto recibido en la respuesta al objeto producto
        this.productoAdmin = response.data;
        return response;
      } catch (error) {
        console.log("Error en VER LISTAR PRODUCTOS STORE ==> ", error);
        return error.response;
      }
    },

    // STORE PARA CREAR PRODUCTO
    async crearProducto(formData) {
      const token = localStorage.getItem("token");
      try {

        const response = await useAxiosInstance().post(
          "/admin/productos/crearProducto",
          formData,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "multipart/form-data"
            }
          });
        // Devuelvo la respuesta de la petición
        return response;
      } catch (error) {
        // Si hay un error devuelvo la respuesta de la petición
        console.log("Error en CREAR PRODUCTO STORE ==> ", error);
        return error.response;
      }
    },

    // STORE PARA ACTUALIZAR PRODUCTO
    async actualizarProducto(formData, id) {
      const token = localStorage.getItem("token");
      try {
        const response = await useAxiosInstance().patch(
          "/admin/productos/actualizarProducto",
          formData,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "multipart/form-data"
            },
            params: {
              id: id,
            },
          });
        // Devuelvo la respuesta de la petición
        return response;
      } catch (error) {
        // Si hay un error devuelvo la respuesta de la petición
        console.log("Error en ACTUALIZAR PRODUCTO STORE ==> ", error);
        return error.response;
      }
    },

  //STORE PARA ELIMINAR PRODUCTO
  async actualizarProducto(id) {
    const token = localStorage.getItem("token");
    try {
      const response = await useAxiosInstance().delete(
        "/admin/productos/actualizarProducto",
        formData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            // "Content-Type": "multipart/form-data"
          },
          params: {
            id: id,
          },
        });
      // Devuelvo la respuesta de la petición
      return response;
    } catch (error) {
      // Si hay un error devuelvo la respuesta de la petición
      console.log("Error en ELIMINAR PRODUCTO STORE ==> ", error);
      return error.response;
    }
  },









  },
});

export default productoAdminStore;
