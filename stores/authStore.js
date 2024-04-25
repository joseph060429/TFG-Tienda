import { defineStore } from "pinia";
import { useAxiosInstance } from "~/utils/axios";

// Defino un endopoint que es el mismo para todos los metodos
const endpoint = "/auth";

// DEFINO Y EXPORTO LA STORE 'useAuthStore'
export const useAuthStore = defineStore({
  // Identificador unico para el store
  id: "auth",
  state: () => ({
    // Defino el estado inicial de la store
    auth: {
      token: "", // Token de autorizacion
      roles: [], // Roles del usuario
      email: "",
      tiempoExpiracion: "", // Tiempo de expiración del token
      refreshToken: "", // Refrescoken del usuario
    },
    loggedIn: false,
  }),

  /* Poner useAxiosInstance() con cada peticion al back,
    no se por que no se puede definir axios tal cual arriba del todo
    */

  // Defino las acciones (métodos) que pueden modificar el estado del store
  actions: {

    // STORE PARA VER SI EL USUARIO ESTA LOGEADO Y TIENE TOKEN
    async checkLogin() {
      try {
        if (localStorage.getItem("token")) {
          this.loggedIn = true;
          return true;
        } else {
          this.loggedIn = false;
          return false;
        }
      } catch (e) {
        console.log(e);
      }
    },

    // STORE LOGIN
    async login(datosLogin) {
      try {
        // Realizo una petición POST al endpoint '/login' con los datos de login
        const response = await useAxiosInstance().post("/login", {
          email: datosLogin.email,
          password: datosLogin.password,
        });

        // Verifico si la respuesta es exitosa (código 200)
        if (response.status === 200) {
          localStorage.setItem("token", response.data.token);
          localStorage.setItem("refreshToken", response.data.refreshToken);
          localStorage.setItem("email", response.data.email);
          localStorage.setItem("tiempoExpiracion", response.data.tiempoExpiracion);
          localStorage.setItem("roles", response.data.roles);
          this.loggedIn = true;
          switch (true) {
            // Actualizo el token de autenticación y el nombre de usuario en pinia
            case response.data.hasOwnProperty("token"):
              this.auth.token = response.data.token;
            case response.data.hasOwnProperty("email"):
              this.auth.email = response.data.email;
            case response.data.hasOwnProperty("refreshToken"):
              this.auth.refreshToken = response.data.refreshToken;
            case response.data.hasOwnProperty("tiempoExpiracion"):
              this.auth.tiempoExpiracion = response.data.tiempoExpiracion;
            case response.data.hasOwnProperty("roles"):
              this.auth.roles = response.data.roles;

            default:
              break;
          }
        }
        // Devuelvo la respuesta de la petición
        return response;
      } catch (error) {
        // Si hay un error devuelvo la respuesta de la petición
        console.log("Error en LOGIN STORE ==> ", error);
        return error.response;
      }
    },

    // LAS DEMÁS STORES SON PRACTICAMENTE LO MISMO

    // STORE REGISTRO
    async registro(datosRegistro) {
      try {
        const response = await useAxiosInstance().post("/crearUsuario", {
          nombre: datosRegistro.nombre,
          apellido: datosRegistro.apellido,
          email: datosRegistro.email,
          password: datosRegistro.password,
        });
        return response;
      } catch (error) {
        console.log("Error en REGISTRO STORE ==> ", error);
        return error.response;
      }
    },

    // STORE CODIGO RECUPERACION
    async enviarCodigoRecuperacion(emailUsuario) {
      try {
        const response = await useAxiosInstance().post(
          "/envioCodigoRecuperacion",
          {
            email: emailUsuario.email,
          }
        );
        return response;
      } catch (error) {
        console.log("Error en envioCodigoRecuperacion STORE ==> ", error);
        return error.response;
      }
    },

    // STORE VERIFICAR CODIGO
    async verificaCodigo(codigoRecuperacion) {
      try {
        const response = await useAxiosInstance().post("/verificarCodigo", {
          codigoRecuperacion: codigoRecuperacion,
        });
        return response;
      } catch (error) {
        console.log("Error en VERIFICAR CODIGO STORE ==> ", error);
        return error.response;
      }
    },

    // STORE CAMBIAR CONTRASEÑA
    async cambioContrasenia(datosCambioPassword, code) {
      try {
        const response = await useAxiosInstance().post("/cambiarContrasenia", {
          password: datosCambioPassword.password,
          repitaPassword: datosCambioPassword.repitaPassword,
          recuperarContrasenia: code,
        });
        return response;
      } catch (error) {
        console.log("Error en CAMBIAR CONTRASEÑA STORE ==> ", error);
        return error.response;
      }
    },

    // STORE PARA VERIFICAR EL ROL DEL USUARIO 
    async checkUserRole() {
      console.log("hola email", localStorage.getItem("email"));
      try {
        // Realizo la solicitud GET al endopoint que he hecho en el backend  para obtener comprobar que el email de usuario tiene ese rol
        const response = await useAxiosInstance().get("/quienSoy", {
          params: {
            email: localStorage.getItem("email"),
          },
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        });
        console.log(response, "dsed hola");
        // Verifico si ese email, tiene ese rol
        if (!response.data) {
          console.log("El usuario no tiene el rol necesario");
          return false;
        }
        return true;
      } catch (error) {
        console.error("Error al verificar los roles del usuario:", error);
        throw error;
      }
    },
  },
});

export default useAuthStore;
