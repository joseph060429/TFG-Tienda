
import { useAuth } from '~/composables/useAuth.js';

export default defineNuxtRouteMiddleware((to, from) => {

  if (process.client) {
    // Obtén el token del almacenamiento local
    const token = localStorage.getItem("token");
    const roles = localStorage.getItem("roles");
    const email = localStorage.getItem("email");
    const refreshToken = localStorage.getItem("refreshToken");
    const tiempoExpiracion = localStorage.getItem("tiempoExpiracion");

    const { checkUserRole } = useAuth();



    if (!token) {

      // to.meta.role es la ruta que me viene de la vista actual
      if (!to.meta.role || !to.meta.role.includes("PUBLIC")) {
        console.log("No hay token, redirigiendo al login");
        return navigateTo("/auth/login");
      }




    } else {
      console.log(to, 'to')
      if (to.meta.role.includes("ROLE_ADMIN")) {
        console.log('hola')
        let estado;
        checkUserRole().then((res) => {
          console.log(res, 'res')
          if (res == false) {
            if (localStorage.getItem("roles") == "ROLE_ADMIN") {
              localStorage.removeItem("token");
              return navigateTo("/auth/login");
            }
            console.log("No hayfsf");
            return navigateTo("/usuario/vistaInicioUsuario");
          }
          // return navigateTo("/admin/vistaInicioAdmin");
        })
        console.log(estado, 'estado checkeo')
        // return navigateTo("/admin/vistaInicioAdmin");


      };

      // console.log("Hay token");
      // Si el rol del usuario no está permitido para acceder a la ruta actual redirijo según su rol
      // if (!to.meta.role.includes(roles)) {
      //   console.log("Rol permitido");
      //   // Si el usuario es ROLE_USER y la ruta requiere ROLE_ADMIN lo redirijo a la vista de inicio de usuario
      //   if (roles === "ROLE_USER" && to.meta.role.includes("ROLE_ADMIN")) {
      //     console.log("ROLE " + roles);
      //     console.log("TIENE ROL USUARIO");
      //   }
      //   else {
      //     // Si el usuario tiene ROL_ADMIN y la ruta requiere ROLE_USER lo redirijo a la vista de inicio de administrador
      //     return navigateTo("/admin/vistaInicioAdmin");
      //   }
      // }

      // if (checkUserRole("ROLE_USER", email)) {
      //   console.log("No hay token, redirigiendo al login");
      //   return navigateTo("/auth/login");
      // }
    }

  }




});




//   // Obtengo el rol del usuario utilizando el token
//   // const role = decodificarTokenYSacarelRol(token);

//   // Imprimo en consola el rol que proviene de la vista actual to.meta.role, es la vista actual
//   console.log("Esto es to ==> ", to.meta.role);
//   // Si no hay token y la ruta no es pública, redirijo al usuario a la vista de login
//   if (!token) {
//     if (!to.meta.role || !to.meta.role.includes("PUBLIC")) {
//       console.log("No hay token, redirigiendo al login");
//       return navigateTo("/auth/login");
//     }
//   } else {
//     console.log("Hay token");
//     // Si el rol del usuario no está permitido para acceder a la ruta actual redirijo según su rol
//     if (!to.meta.role.includes(role)) {
//       console.log("Rol permitido");
//       // Si el usuario es ROLE_USER y la ruta requiere ROLE_ADMIN lo redirijo a la vista de inicio de usuario
//       if (role === "ROLE_USER" && to.meta.role.includes("ROLE_ADMIN")) {
//         console.log("ROLE " + role);
//         console.log("TIENE ROL USUARIO");
//         return navigateTo("/usuario/vistaInicioUsuario");
//       } else {
//         // Si el usuario tiene ROL_ADMIN y la ruta requiere ROLE_USER lo redirijo a la vista de inicio de administrador
//         return navigateTo("/admin/vistaInicioAdmin");
//       }
//     }
//   }
