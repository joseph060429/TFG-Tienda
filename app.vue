<template>
  <div class="container">
    <AppHeader />
    <NuxtPage />
    <q-layout class="layout" view="lHh lpr lFf">
      <AppFooter />
    </q-layout>
  </div>
</template>

<style scoped>
.container {
  max-width: 100vw;
  max-height: 100vh;
}
</style>