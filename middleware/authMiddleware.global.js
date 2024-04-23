import { useRouter } from "vue-router";
import { decodificarTokenYSacarelRol } from "~/utils/decodificarToken.js";

export default defineNuxtRouteMiddleware((to, from) => {
  const router = useRouter();

  // Pongo process.cliente para que no se ejecute en el servidor y solo en el cliente
  if (process.client) {
    // Obtengo el token del almacenamiento local
    const token = localStorage.getItem("token");
    // Obtengo el rol del usuario utilizando el token
    const role = decodificarTokenYSacarelRol(token);

    // Imprimo en consola el rol que proviene de la vista actual to.meta.role, es la vista actual
    console.log("Esto es to ==> ", to.meta.role);
    // Si no hay token y la ruta no es pública, redirijo al usuario a la vista de login
    if (!token) {
      if (!to.meta.role || !to.meta.role.includes("PUBLIC")) {
        console.log("No hay token, redirigiendo al login");
        return navigateTo("/auth/login");
      }
    } else {
      console.log("Hay token");
      // Si el rol del usuario no está permitido para acceder a la ruta actual redirijo según su rol
      if (!to.meta.role.includes(role)) {
        console.log("Rol permitido");
        // Si el usuario es ROLE_USER y la ruta requiere ROLE_ADMIN lo redirijo a la vista de inicio de usuario
        if (role === "ROLE_USER" && to.meta.role.includes("ROLE_ADMIN")) {
          console.log("ROLE " + role);
          console.log("TIENE ROL USUARIO");
          return navigateTo("/usuario/vistaInicioUsuario");
        } else {
          // Si el usuario tiene ROL_ADMIN y la ruta requiere ROLE_USER lo redirijo a la vista de inicio de administrador
          return navigateTo("/admin/vistaInicioAdmin");
        }
      }
    }
  }
});
