import { defineStore } from "pinia";
import { useAxiosInstance } from "~/utils/axios";

const endpoint = "/auth";

export const useAuthStore = defineStore({
    id: "auth",
    state: () => ({
        auth: {
            token: "",
            user: {
                username: ''
            }
        }
    }),

    /* Poner useAxiosInstance() con cada peticion al back,
    no se por que no se puede definir axios tal cual arriba del todo
    */
    actions: {

        // STORE LOGIN
        async login(datosLogin) {
            try {
                const response = await useAxiosInstance().post('/login', {
                    email: datosLogin.email,
                    password: datosLogin.password
                });

                if (response.status === 200) {
                    switch (true) {
                        case response.data.hasOwnProperty('token'):
                            this.auth.token = response.data.token;
                        case response.data.hasOwnProperty('user'):
                            this.auth.user.username = response.data.user.username;
                        default:
                            break;
                    }
                }
                return response;
            } catch (error) {
                console.log('Error en LOGIN STORE ==> ', error);
                return error.response;
            }
        },

        // STORE REGISTRO
        async registro(datosRegistro) {
            try {
                const response = await useAxiosInstance().post('/crearUsuario', {
                    nombre: datosRegistro.nombre,
                    apellido: datosRegistro.apellido,
                    email: datosRegistro.email,
                    password: datosRegistro.password
                });
                return response;
            } catch (error) {
                console.log('Error en REGISTRO STORE ==> ', error);
                return error.response;
            }
        },

        // STORE CODIGO RECUPERACION
        async enviarCodigoRecuperacion(emailUsuario) {
            try {
                const response = await useAxiosInstance().post('/envioCodigoRecuperacion', {
                    email: emailUsuario.email,
                });
                return response;
            } catch (error) {
                console.log('Error en envioCodigoRecuperacion STORE ==> ', error);
                return error.response;
            }
        },

        // STORE VERIFICAR CODIGO
        async verificaCodigo(codigoRecuperacion) {
            try {
                const response = await useAxiosInstance().post('/verificarCodigo', {
                    codigoRecuperacion: codigoRecuperacion
                });
                return response;
            } catch (error) {
                console.log('Error en VERIFICAR CODIGO STORE ==> ', error);
                return error.response;
            }
        },

        // STORE CAMBIAR CONTRASEÑA
        async cambioContrasenia(datosCambioPassword, code) {
            try {
                const response = await useAxiosInstance().post('/cambiarContrasenia', {
                    password: datosCambioPassword.password,
                    repitaPassword: datosCambioPassword.repitaPassword,
                    recuperarContrasenia: code
                });
                return response;
            } catch (error) {
                console.log('Error en CAMBIAR CONTRASEÑA STORE ==> ', error);
                return error.response;
            }
        }




        

    
    }
});

export default useAuthStore;
