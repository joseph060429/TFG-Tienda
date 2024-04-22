
// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: { enabled: true },
  modules: ["nuxt-quasar-ui"],
  runtimeConfig: {
    public: {
      urlApi: process.env.URL_API
    }
  },
  quasar: {
    iconSet: "mdi-v7",
    config: {
      dark: false,
      notify: {
        position: "top", // Configuración de la posición de la notificación
        timeout: 3000, // Tiempo de espera en milisegundos (5 segundos)
      },
    },
    plugins: ["Notify"],
    
  },

  plugins: [
    '~/plugins/axios.js', // Ruta del archivo del plugin de Axios
    
  ],
});
