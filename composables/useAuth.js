import { useAuthStore } from "~/stores/authStore.js";

export const useAuth = () => {

    const pinia = usePinia();
    const store = useAuthStore(pinia);

    const login = async(datosAuth) => {
        return await store.login(datosAuth);
    }

    const registro = async(datosRegistro) => {
        return await store.registro(datosRegistro);
    }

    const enviarCodigoRecuperacion = async(emailUsuario) => {
        return await store.enviarCodigoRecuperacion(emailUsuario);
    }

    const verificaCodigo = async(codigoRecuperacion) => {
        return await store.verificaCodigo(codigoRecuperacion);
    }

    const cambioContrasenia = async(datosCambioPassword, code) => {
        return await store.cambioContrasenia(datosCambioPassword, code);
    }

    return {
        login,
        registro,
        enviarCodigoRecuperacion, 
        verificaCodigo,
        cambioContrasenia
    }
}