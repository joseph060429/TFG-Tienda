import axios from "axios";

export default defineNuxtPlugin(async () => {

  const { urlApi } = useRuntimeConfig().public;
  const axiosInstance = axios.create({
    baseURL: urlApi,
    headers: {
      "Content-Type": "application/json", // Ejemplo de encabezado personalizado
      // Aquí puedes agregar más encabezados según sea necesario
    }
  });

  return {
    provide: {
      axios: axiosInstance
    }
  };
});
