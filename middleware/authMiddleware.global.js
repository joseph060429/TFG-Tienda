import { useAuth } from "~/composables/useAuth.js";
import useAuthStore from "~/stores/authStore.js";

export default defineNuxtRouteMiddleware((to, from) => {
  if (process.client) {
    
    let authStore = useAuthStore();

    // Obtén el token del almacenamiento local
    const token = localStorage.getItem("token");
    const roles = localStorage.getItem("roles");
    const email = localStorage.getItem("email");
    const refreshToken = localStorage.getItem("refreshToken");
    const tiempoExpiracion = localStorage.getItem("tiempoExpiracion");

    const { checkUserRole } = useAuth();

    if (!token) {
      // to.meta.role es la ruta que me viene de la vista actual
      // Si no tiene ningún rol requerido o la ruta no es pública
      if (!to.meta.role || !to.meta.role.includes("PUBLIC")) {
        console.log("No hay token, redirigiendo al login");
        // Redirijo al usuario a la página de inicio de sesión
        return navigateTo("/auth/login");
      }
    } else {
      // Si hay un token de autenticación
      console.log(to, "to");
      // Verifico si la ruta actual requiere el rol de administrador
      if (to.meta.role.includes("ROLE_ADMIN")) {
        // Variable donde almaceno el estado del usuario
        let estado;
        // Verifico el rol del usuario
        checkUserRole().then((res) => {
          // Si la verificación falla o el usuario no es un administrador
          if (res == false) {
            // Si el usuario tiene almacenado el rol de administrador pero no está autenticado como administrador
            // Borro todos los datos de la sesión
            if (localStorage.getItem("roles") == "ROLE_ADMIN") {
              localStorage.removeItem("token");
              localStorage.removeItem("roles");
              localStorage.removeItem("email");
              localStorage.removeItem("refreshToken");
              localStorage.removeItem("tiempoExpiracion");
              // Le quito la autenticación a la store para que me salgan los botones de registro y de login
              authStore.loggedIn = false;
              // Redirijo al usuario a la página de inicio de sesión
              // return navigateTo("/auth/login");
              // return navigateTo("/layouts/errores");
              // return navigateTo("/usuario/vistaInicioUsuario");
              // return true;
            }
            // Preguntar si el usuario tiene el rol de administrador lo puedo redireccionar a la vista de usuario, o a la vista de admin
            return navigateTo("/usuario/vistaInicioUsuario");
            // return navigateTo("/admin/vistaInicioAdmin");
          }
          // return navigateTo("/admin/vistaInicioAdmin");
        });
        console.log(estado, "estado checkeo");
        // return navigateTo("/admin/vistaInicioAdmin");
      }
      // return navigateTo("/admin/vistaInicioAdmin");
    }
  }
});
