import { useRouter } from 'vue-router'
import { decodificarTokenYSacarelRol } from '~/utils/decodificarToken.js';

export default defineNuxtRouteMiddleware((to, from) => {
    const router = useRouter();

    // !token && !to.meta.role.includes('PUBLIC')


    // if (!token && !to.meta.role.includes('PUBLIC')) {
    //     console.log('No hay token');
    //     // router.push('/auth/login');
    //     return abortNavigation()
    // } 

    if (process.client) {
        const token = localStorage.getItem('token');
        const role = decodificarTokenYSacarelRol(token);

        //Rol que proviene de la vista
        console.log('Esto es to ==> ', to.meta.role);
        // Si no hay token y la ruta no es pública, y no estamos ya en la página de inicio de sesión, redirige al usuario a la página de inicio de sesión
        if (!token) {
            if (!to.meta.role || !to.meta.role.includes('PUBLIC')) {
                console.log('No hay token, redirigiendo al login');
                return navigateTo('/auth/login')
            }
        }
        else {
            console.log('Hay token');
            if (!to.meta.role.includes(role)) {
                console.log('Rol permitido');

                if (role === 'ROLE_USER' && to.meta.role.includes('ROLE_ADMIN')) {
                    console.log("ROLE " + role);
                    console.log("TIENE ROL USUARIO");
                    return navigateTo('/usuario/vistaInicioUsuario');
                }
                else{
                    return navigateTo('/admin/vistaInicioAdmin');
                }
            }

        }
    }
})
