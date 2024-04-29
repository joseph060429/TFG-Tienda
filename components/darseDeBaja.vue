<template>
    <q-dialog v-model="dialogVisible" persistent>
      <q-card>
        <q-card-section class="text-center">
          <q-card-title>Confirmación</q-card-title>
          <p>¿Estás seguro de que quieres darte de baja?</p>
        </q-card-section>
        <q-card-actions align="center">
          <q-btn label="Cancelar" color="primary" @click="cerrarDialogo" />
          <q-btn label="Darte de baja" color="negative" @click="confirmarDarseDeBaja" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </template>
  
  <script setup>
  import { ref, defineProps } from 'vue';
  
  const props = defineProps({
    dialogVisible: Boolean
  });
  
  const dialogVisible = ref(props.dialogVisible);
  
  const cerrarDialogo = () => {
    dialogVisible.value == false; // Oculta el diálogo
    console.log("Cerrando el diálogo");
  };
  
  const confirmarDarseDeBaja = () => {
    dialogVisible.value = false; // Oculta el diálogo
    console.log("Dándose de baja");
  };
  </script>
  