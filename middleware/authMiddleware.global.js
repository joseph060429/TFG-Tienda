import { useAuth } from "~/composables/useAuth.js";
import useAuthStore from "~/stores/authStore.js";

export default defineNuxtRouteMiddleware((to, from) => {
  if (process.client) {
    
    const { checkUserRole } = useAuth();

    let authStore = useAuthStore();

    // Obtén el token del almacenamiento local
    const token = localStorage.getItem("token");
    const roles = localStorage.getItem("roles");
    const nombre = localStorage.getItem("nombre");
    const apellido = localStorage.getItem("apellido");
    const email = localStorage.getItem("email");
    const refreshToken = localStorage.getItem("refreshToken");
    const tiempoExpiracion = localStorage.getItem("tiempoExpiracion");

    if (!token) {
      // to.meta.role es la ruta que me viene de la vista actual
      // Si no tiene ningún rol requerido o la ruta no es pública
      if (!to.meta.role || !to.meta.role.includes("PUBLIC")) {
        console.log("No hay token, redirigiendo al login");
        // Redirijo al usuario a la página de inicio de sesión
        return navigateTo("/auth/login");
      }
    } else {
      // Lo pongo en la store para que si actualiza se mantengan los datos en el store
      authStore.auth.token = token 
      authStore.auth.email = email
      authStore.auth.refreshToken = refreshToken
      authStore.auth.tiempoExpiracion = tiempoExpiracion
      authStore.auth.roles = roles
      authStore.auth.nombre = nombre
      authStore.auth.apellido = apellido

      // Si hay un token de autenticación
      console.log(to, "to");
      // Verifico si la ruta actual requiere el rol de administrador
      // to.meta.role && to.meta.role.includes("ROLE_ADMIN")
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
              localStorage.removeItem("nombre");
              localStorage.removeItem("apellido");
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
