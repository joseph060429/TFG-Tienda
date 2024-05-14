import { defineStore } from "pinia";
import { useAxiosInstance } from "~/utils/axios";

// DEFINO Y EXPORTO LA STORE 'useAuthStore'
export const adminStore = defineStore({
  // Identificador unico para el store
  id: "admin",
  state: () => ({
    // Defino el estado inicial de la store, es este el nombre que le pongo para luego llamarlo en el composable
    usuarios: [],
    loggedIn: true,
  }),

  /* Poner useAxiosInstance() con cada peticion al back,
    no se por que no se puede definir axios tal cual arriba del todo
    */

  // Defino las acciones (métodos) que pueden modificar el estado del store
  actions: {
    // STORE LSITAR TODOS LOS USUARIO
    async listarUsuarios() {
      try {
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().get("/admin/listarUsuarios", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        // Agrego los datos de los usuarios recibidos en la respuesta al array de usuarios
        this.usuarios = response.data;
        return response;
      } catch (error) {
        console.log("Error en LISTAR USUARIOS STORE ==> ", error);
        return error.response;
      }
    },


    // STORE PARA ACTUALIZARLE EL ROL A UN USUARIO
    async actualizarRol(id, nombreRol) {
      try {
        const token = localStorage.getItem("token");
        console.log("NOMBRE ROL STORE", nombreRol);
        const response = await useAxiosInstance().patch(
          "/admin/actualizarRolUsuario",
          { nombreRol: nombreRol },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
            params: {
              id: id,
            },
          }
        );
        console.log("NOMBRE ROL STOREaaa", nombreRol);
        this.usuarios = response.data;

        return response;
      } catch (error) {
        console.log("Error en ACTUALIZAR ROL  STORE ==> ", error);
        return error.response;
      }
    },

    // STORE PARA ELIMINAR UN USUARIO
    async eliminarUsuario(email) {
      try {
        const token = localStorage.getItem("token");
        console.log("NOMBRE EMAIL STORE", email);
        const response = await useAxiosInstance().delete(
          "/admin/borrarUsuario",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
            params: {
              email: email,
            }
          }
        );
        console.log("NOMBRE EMAIL STOREAA", email);
        this.usuarios = response.data;

        return response;
      } catch (error) {
        console.log("Error en ELIMINAR USUARIO STORE ==> ", error);
        return error.response;
      }
    },



    async actualizacionUsuarioAdmin(id, datosActualizar) {
      try {
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().patch(
          "/admin/actualizarUsuario",
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
            params: {
              id: id,
            }
          }
        );
        console.log("id del usuario a actualizar desde store", id);
        return response;
      } catch (error) {
        console.log("Error en ACTUALIZAR USUARIO ADMIN STORE ==> ", error);
        return error.response;
      }
    },










  },
});

export default adminStore;
