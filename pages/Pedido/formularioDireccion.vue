<template>
    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />

    <h1>Direccion de envío </h1>
    <q-form @submit="envioFormulario">



        <q-input filled v-model="datosEnvio.tipoPago" label="Tipo de Pago*"readonly class="readonly">
            <template v-slot:prepend>
                <q-icon name="mdi-credit-card" />
            </template>
        </q-input>










        <q-input v-model="datosEnvio.codigoPostal" label="Código Postal" dense />




        
        <q-input v-model="datosEnvio.direccion" label="Dirección" dense />
        <q-input v-model="datosEnvio.provincia" label="Provincia" dense />
        <q-input v-model="datosEnvio.numero" label="Número" dense />
        <q-input v-model="datosEnvio.piso" label="Piso" dense />
        <q-input v-model="datosEnvio.puerta" label="Puerta" dense />
        <q-btn type="submit" label="Enviar" color="primary" />
    </q-form>

</template>

<script setup>
// RUTAS
const router = useRouter()

const datosEnvio = ref({
    tipoPago: 'PayPal',
    codigoPostal: '',
    direccion: '',
    provincia: '',
    numero: '',
    piso: '',
    puerta: ''
});

definePageMeta({
    role: ['ROLE_USER']
});

const regresar = () => {
    router.push({ path: '/pedido/carritoCompra' })
};

</script>

<style lang="scss" scoped></style>