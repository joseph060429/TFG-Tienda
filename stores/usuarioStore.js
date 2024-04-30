import { defineStore } from "pinia";
import { useAxiosInstance } from "~/utils/axios";

// Defino un endopoint que es el mismo para todos los metodos
const endpoint = "/usuarios";

// DEFINO Y EXPORTO LA STORE 'useAuthStore'
export const usuarioStore = defineStore({
  // Identificador unico para el store
  id: "usuarios",
  state: () => ({
    // Defino el estado inicial de la store
    usuario: {
      direccionEnvio: "",
    },
    loggedIn: true,
  }),

  /* Poner useAxiosInstance() con cada peticion al back,
    no se por que no se puede definir axios tal cual arriba del todo
    */

  // Defino las acciones (métodos) que pueden modificar el estado del store
  actions: {
    // STORE ACTUALIZAR USUARIO
    async actualizacionUsuario(datosActualizar) {
      try {
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().patch(
          "/usuarios/actualizarUsuario",
          {
            nombre: datosActualizar.nombre,
            apellido: datosActualizar.apellido,
            email: datosActualizar.email,
            password: datosActualizar.password,
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        return response;
      } catch (error) {
        console.log("Error en ACTUALIZAR USUARIO STORE ==> ", error);
        return error.response;
      }
    },

    //STORE DARSE DE BAJA(ELIMINAR CUENTA)
    async borrarUsuario() {
      try {
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().delete(
          "/usuarios/borrarUsuario",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        return response;
      } catch (error) {
        console.log("Error en BORRAR USUARIO STORE ==> ", error);
        return error.response;
      }
    },

    // STORE VER TODOS LOS PEDIDOS
    async historialPedidos() {
      try {
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().get(
          "/usuarios/pedidos/historialPedidos",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        this.usuario.direccionEnvio = response.data[0].direccionEnvio
        return response;

      } catch (error) {
        console.log("Error en VER PEDIDOS STORE ==> ", error);
        return error.response;
      }
    },
  
  },

});

export default usuarioStore;
