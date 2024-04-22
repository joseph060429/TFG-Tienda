import { useNuxtApp } from '#app';
 
export const useAxiosInstance = () => {
    const { $axios } = useNuxtApp();
    return $axios;
}