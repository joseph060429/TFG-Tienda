import { defineStore } from "pinia";
import { useAxiosInstance } from "~/utils/axios";

// DEFINO Y EXPORTO LA STORE 'useAuthStore'
export const usuarioStore = defineStore({
  // Identificador unico para el store
  id: "usuarios",
  state: () => ({
    // Defino el estado inicial de la store, es este el nombre que le pongo para luego llamarlo en el composable
    usuario: {
      pedidos: [],
      carrito: [],
      direccionesEnvioFacturacion: [],
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
        // Limpio la lista de pedidos antes de agregar nuevos a ella
        this.usuario.pedidos = [];

        // For each para iterar sobre cada pedido en la respuesta
        response.data.forEach((pedido) => {
          const pedidoFormateado = {
            direccionEnvio: pedido.direccionEnvio,
            tipoPago: pedido.tipoPago,
            estado: pedido.estado,
            fecha: pedido.fechaPedido,
            numPedido: pedido.numPedido,
            productos: pedido.productos.map((producto) => ({
              cantidadPedida: producto.cantidadPedida,
              nombre: producto.nombre,
              marca: producto.marca,
              precio: producto.precioProducto,
            })),
          };
          this.usuario.pedidos.push(pedidoFormateado);
        });
        return response;
      } catch (error) {
        console.log("Error en VER PEDIDOS STORE ==> ", error);
        return error.response;
      }
    },

    // STORE PARA CANCELAR PEDIDOS
    async cancelarPedidos(numPedido) {
      try {
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().delete(
          "/usuarios/pedidos/eliminarPedido",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
            params: {
              numeroPedido: numPedido,
            },
          }
        );
        return response;
      } catch (error) {
        console.log("Error en CANCELAR PEDIDOS STORE ==> ", error);
        return error.response;
      }
    },

    // Para limpiar los pedidos de la store del usuario
    limpiarPedidos() {
      this.usuario.pedidos = [];
    },

    limpiarCarrito() {
      this.usuario.carrito = [];
    },


    // STORE PARA ANADIR AL CARRITO
    async anadirAlCarrito(productoId, cantidad) {
      try {
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().post(
          "/carrito/anadirAlCarrito", null,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
            params: {
              productoId: productoId,
              cantidad: cantidad,
            },
          }
        );
        // this.usuario.carrito = [];
        // this.usuario.carrito = response.data;
        return response;
      } catch (error) {
        console.log("Error en AÑADIR AL CARRITO STORE ==> ", error);
        return error.response;
      }


    },

    // STORE PARA VER EL CARRITO
    async verMiCarrito(productoId, nuevaCantidad) {
      try {
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().get(
          "/carrito/verCarrito",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
            params: {
              productoId: productoId,
              nuevaCantidad: nuevaCantidad,
            },
          }
        );
        this.usuario.carrito = response.data;
        console.log("carrito de store", this.usuario.carrito);
        return response;
      } catch (error) {
        console.log("Error en VER CARRITO STORE ==> ", error);
        return error.response;
      }
    },

    // Eliminar del carrito
    async eliminarProductoCarrito(_idCarrito) {
      try {
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().delete(
          "/carrito/eliminarDelCarrito",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
            params: {
              _idCarrito: _idCarrito
            },
          }
        );
        // this.usuario.carrito = response.data;
        // console.log("carrito de eliminar store", this.usuario.carrito);
        return response;
      } catch (error) {
        console.log("Error en ELIMINAR CARRITO STORE ==> ", error);
        return error.response;
      }
    },

    // STORE PARA ELIMINAR PRODUCTOS DEL CARRITO DEL USUARIO
    // async eliminarProductoCarrito(_idCarrito) {
    //   try {
    //     const token = localStorage.getItem("token");
    //     const response = await useAxiosInstance().get(
    //       "/carrito/eliminarDelCarrito",
    //       {
    //         headers: {
    //           Authorization: `Bearer ${token}`,
    //         },
    //         params: {
    //           _idCarrito: _idCarrito
    //         },
    //       }
    //     );
    //     // this.usuario.carrito = response.data;
    //     // console.log("carrito de eliminar store", this.usuario.carrito);
    //     return response;
    //   } catch (error) {
    //     console.log("Error en ELIMINAR CARRITO STORE ==> ", error);
    //     return error.response;
    //   }
    // },

    // STORE PARA TRAERME LAS DIRECCIONES DEL USUARIO
    async direccionesUsuario() {
      try {
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().get(
          "/usuarios/direccionesUsuarios",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        this.usuario.direccionesEnvioFacturacion = response.data;
        return response;
      } catch (error) {
        console.log("Error en VER DIRECCIONES DE USUARIO STORE ==> ", error);
        return error.response;
      }
    },

    // STORE PARA AÑADIR UNA DIRECCION DE ENVIO
    async anadirDireccionEnvio(direccionEnvio) {
      try {
        console.log(direccionEnvio);
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().post(
          "/usuarios/anadirDireccionEnvio",
          {
            direccion: direccionEnvio.direccion,
            numero: direccionEnvio.numero,
            piso: direccionEnvio.piso,
            puerta: direccionEnvio.puerta,
            codigoPostal: direccionEnvio.codigoPostal,
            provincia: direccionEnvio.provincia
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        // this.usuario.direccionesEnvioFacturacion.direccionesEnvio = response.data;
        return response;
      } catch (error) {
        console.log("Error en ANADIR DIRECCION DE ENVIO USUARIO STORE ==> ", error);
        return error.response;
      }
    },

    // AÑADIR DIRECCION FACTURACION EMPRESA AUTONOMO
    async anadirDireccionFacturacionEmpreAuto(direccionFacturacionEmpreAuto) {
      try {
        console.log(direccionFacturacionEmpreAuto);
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().post(
          "/usuarios/anadirDireccionFacturacionEmpresaAutonomo",
          {
            cifONifFacturacion: direccionFacturacionEmpreAuto.cifONifFacturacion,
            numTelefonoFacturacion: direccionFacturacionEmpreAuto.numTelefonoFacturacion,
            direccionDeFacturacion: direccionFacturacionEmpreAuto.direccionDeFacturacion,
            numeroDeFacturacion: direccionFacturacionEmpreAuto.numeroDeFacturacion,
            pisoDeFacturacion: direccionFacturacionEmpreAuto.pisoDeFacturacion,
            puertaDeFacturacion: direccionFacturacionEmpreAuto.puertaDeFacturacion,
            codigoPostalDeFacturacion: direccionFacturacionEmpreAuto.codigoPostalDeFacturacion,
            provinciaDeFacturacion: direccionFacturacionEmpreAuto.provinciaDeFacturacion
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        // this.usuario.direccionesEnvioFacturacion.direccionesEnvio = response.data;
        return response;
      } catch (error) {
        console.log("Error en ANADIR DIRECCION DE FACTURACION EMPRESA AUTONOMO USUARIO STORE ==> ", error);
        return error.response;
      }
    },



    // AÑADIR DIRECCION FACTURACION EMPRESA AUTONOMO
    async anadirDireccionFacturacionParticular(direccionFacturacionParticular) {
      try {
        console.log(direccionFacturacionParticular);
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().post(
          "/usuarios/anadirDireccionFacturacionParticular",
          {
            nombreFacturacion: direccionFacturacionParticular.nombreFacturacion,
            apellidoFacturacion: direccionFacturacionParticular.apellidoFacturacion,
            numTelefonoFacturacion: direccionFacturacionParticular.numTelefonoFacturacion,
            direccionDeFacturacion: direccionFacturacionParticular.direccionDeFacturacion,
            numeroDeFacturacion: direccionFacturacionParticular.numeroDeFacturacion,
            pisoDeFacturacion: direccionFacturacionParticular.pisoDeFacturacion,
            puertaDeFacturacion: direccionFacturacionParticular.puertaDeFacturacion,
            codigoPostalDeFacturacion: direccionFacturacionParticular.codigoPostalDeFacturacion,
            provinciaDeFacturacion: direccionFacturacionParticular.provinciaDeFacturacion
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        // this.usuario.direccionesEnvioFacturacion.direccionesEnvio = response.data;
        return response;
      } catch (error) {
        console.log("Error en ANADIR DIRECCION DE FACTURACION PARTICULAR USUARIO STORE ==> ", error);
        return error.response;
      }
    },


    // STORE PARA ELIMINAR LA DIRECCION ENVIO AL USUARIO
    async eliminarDireccionEnvio(index) {
      try {
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().delete(
          "/usuarios/eliminarDireccionesEnvio",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
            params: {
              index: index
            },
          }
        );
        this.usuario.direccionesEnvioFacturacion.direccionesEnvio.splice(index, 1);
        return response;
      } catch (error) {
        console.log("Error en ELIMINAR DIRECCION ENVIO USUARIO STORE ==> ", error);
        return error.response;
      }
    },

    // STORE PARA ELIMINAR LA DIRECCION FACTURACION AL USUARIO
    async eliminarDireccionFacturacion(index) {
      try {
        const token = localStorage.getItem("token");
        const response = await useAxiosInstance().delete(
          "/usuarios/eliminarDireccionesFacturacion",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
            params: {
              index: index
            },
          }
        );
        this.usuario.direccionesEnvioFacturacion.direccionesFacturacion.splice(index, 1);
        return response;
      } catch (error) {
        console.log("Error en ELIMINAR DIRECCION ENVIO USUARIO STORE ==> ", error);
        return error.response;
      }
    },


































  },
});

export default usuarioStore;
