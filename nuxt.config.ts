// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: { enabled: true },
  modules: [
    'nuxt-quasar-ui'
  ],
  quasar: {
    iconSet: "mdi-v7",
    framework: {
      config: {
        dark: false, // Forzar el modo claro
      },
    }
  },
})
