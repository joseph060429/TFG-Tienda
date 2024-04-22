import { useAuthStore } from "~/stores/authStore.js"
import { useRouter } from 'vue-router'
export default defineNuxtRouteMiddleware(() => {
    
    const authStore = useAuthStore();

    const router = useRouter();

    if(!authStore.auth.token){;
        console.log('No hay token');
        router.push('/auth/login')
    }else{
        console.log("Token" , authStore.auth.token);
    }
  })
  