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
    // STORE ACTUALIZAR USUARIO
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
  },
});

export default adminStore;
