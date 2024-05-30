import { defineStore } from "pinia";
import { useAxiosInstance } from "~/utils/axios";

// DEFINO Y EXPORTO LA STORE 'useAuthStore'
export const adminStore = defineStore({
  // Identificador unico para el store
  id: "admin",
  state: () => ({
    // Defino el estado inicial de la store, es este el nombre que le pongo para luego llamarlo en el composable
    usuarios: [],
    pedidos: [],
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
            },
          }
        );
        console.log("NOMBRE EMAIL STOREAA", email);
        // this.usuarios = response.data;

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
            },
          }
        );
        console.log("id del usuario a actualizar desde store", id);
        return response;
      } catch (error) {
        console.log("Error en ACTUALIZAR USUARIO ADMIN STORE ==> ", error);
        return error.response;
      }
    },

    // STORE PARA LISTAR TODOS LOS PEDIDOS
    async listarPedidos() {
      try {
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().get(
          "/admin/pedidos/listarPedidos",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        // Agrego los datos de los usuarios recibidos en la respuesta al array de usuarios
        this.pedidos = response.data;
        return response;
      } catch (error) {
        console.log("Error en LISTAR PEDIDOS ADMIN STORE ==> ", error);
        return error.response;
      }
    },

    //STORE PARA ACTUALIZAR EL ESTADO DE PEDIDO A ENVIADO
    async actualizarEstadoEnviado(pedidoId, estado) {
      try {
        const token = localStorage.getItem("token");
        console.log("NOMBRE ESTADO STORE", estado);
        const response = await useAxiosInstance().patch(
          "/admin/pedidos/actualizarEstadoPedidoEnviado",
          { pedidoId: pedidoId, estado: estado },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        console.log("NOMBRE ESTADO ENVIADO STORE", estado);
        return response;
      } catch (error) {
        console.log("Error en ESTADO ENVIADO ROL  STORE ==> ", error);
        return error.response;
      }
    },

    // STORE PARA ACTUALIZAR EL ESTADO DEL ENVIO A DIRECCION ERRONEA
    async actualizarEstadoDireccionErronea(pedidoId, estado) {
      try {
        const token = localStorage.getItem("token");
        console.log("NOMBRE ESTADO STORE", estado);
        const response = await useAxiosInstance().patch(
          "/admin/pedidos/envioEmailDireccionErronea",
          { pedidoId: pedidoId, estado: estado },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        return response;
      } catch (error) {
        console.log("Error en DIRECCION ERRONEA ROL  STORE ==> ", error);
        return error.response;
      }
    },

    //STORE PARA ACTUALIZAR PEDIDO A ENTREGADO, LO PONDRÉ CUANDO EL REPARTIDOR VENGA CON TODS LOS PEDIDOS
    async actualizarEstadoEntregado(pedidoId, estado) {
      try {
        const token = localStorage.getItem("token");
        console.log("NOMBRE ESTADO STORE", estado);
        const response = await useAxiosInstance().patch(
          "/admin/pedidos/actualizarEstadoPedidoEntregado",
          { pedidoId: pedidoId, estado: estado },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        console.log("NOMBRE ESTADO ENTREGADO STORE", estado);
        return response;
      } catch (error) {
        console.log("Error en ENTREGADO STORE ==> ", error);
        return error.response;
      }
    },

    //STORE PARA ACTUALIZAR PEDIDO A REPROGRAMADO_PARA_ENTREGA
    async actualizarEstadoReproParaEntrega(pedidoId, estado) {
      try {
        const token = localStorage.getItem("token");
        console.log("NOMBRE ESTADO STORE", estado);
        const response = await useAxiosInstance().patch(
          "/admin/pedidos/envioEmailReprogramadoEntrega",
          { pedidoId: pedidoId, estado: estado },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        console.log("NOMBRE ESTADO REPROGRAMADO_PARA_ENTREGA STORE", estado);
        return response;
      } catch (error) {
        console.log("Error en REPROGRAMADO_PARA_ENTREGA STORE ==> ", error);
        return error.response;
      }
    },

    //STORE PARA ENVIAR UN EMAIL CUANDO UN PEDIDO SE HA RETRASADO
    async enviarCorreoRetraso(email) {
      try {
        const token = localStorage.getItem("token");
        console.log("NOMBRE EMAIL STORE", email);
        const response = await useAxiosInstance().post(
          "/admin/pedidos/envioEmailRetraso",
          { email: email },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        console.log("NOMBRE EMAIL STORE", email);
        return response;
      } catch (error) {
        console.log("Error en enviarCorreoRetraso STORE ==> ", error);
        return error.response;
      }
    },















  },
});

export default adminStore;
