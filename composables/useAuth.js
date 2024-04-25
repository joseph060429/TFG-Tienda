// Importo el store de autenticación desde su ubicación en el archivo authStore.js
import { useAuthStore } from "~/stores/authStore.js";


// Defino y exporto el composable useAuth
export const useAuth = () => {

    // Obtengo la instancia de Pinia para acceder al store de autenticación
    const pinia = usePinia();

    // Obtengo la instancia del store de autenticación utilizando el hook useAuthStore
    const store = useAuthStore(pinia);

    // FUNCION PARA REALIZAR EL LOGIN
    const login = async(datosAuth) => {
        return await store.login(datosAuth);
    }

    // FUNCION PARA REALIZAR EL REGISTRO
    const registro = async(datosRegistro) => {
        return await store.registro(datosRegistro);
    }

    // FUNCION PARA ENVIAR EL CODIGO DE RECUPERACION
    const enviarCodigoRecuperacion = async(emailUsuario) => {
        return await store.enviarCodigoRecuperacion(emailUsuario);
    }

    // FUNCION PARA VERIFICAR EL CODIGO
    const verificaCodigo = async(codigoRecuperacion) => {
        return await store.verificaCodigo(codigoRecuperacion);
    }


    // FUNCION PARA CAMBIAR LA CONTRASEÑA
    const cambioContrasenia = async(datosCambioPassword, code) => {
        return await store.cambioContrasenia(datosCambioPassword, code);
    }

    const checkUserRole = async(roles, email) => {
        return await store.checkUserRole(roles, email);
    }



    // EXPORTO LAS FUNCIONES PARA PODER USARLA EN LAS VISTAS POR EJEMPLO
    return {
        login,
        registro,
        enviarCodigoRecuperacion, 
        verificaCodigo,
        cambioContrasenia,
        checkUserRole
    }
}