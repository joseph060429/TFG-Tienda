<template>
    <div class="container">
        <div v-if="!loading">
            <div>
                <div v-if="usuario.direccionesEnvioFacturacion.direccionesEnvio.length > 0">
                    <h6 class="title">Direccion/es de envío</h6>
                    <formulario-anadir-direccion-envio v-model="mostrarFormularioAnadirDirecionEnvio" />
                    <q-btn @click="anadirDireccionEnvio" class="boton-anadir-direccion-envio"
                        label="Añadir nueva dirección de envío">
                        <q-icon name="mdi-map-marker" />
                    </q-btn>
                    <q-card v-for="(direccion, index) in usuario.direccionesEnvioFacturacion.direccionesEnvio"
                        :key="index" class="address-card">
                        <q-card-section>
                            <div class="info-direcciones">{{ direccion }}</div>
                        </q-card-section>
                    </q-card>
                </div>
                <div v-else>
                    <q-card class="no-address-card">
                        <q-card-section>
                            <div class="title">No hay direcciones de envío disponibles</div>
                            <formulario-anadir-direccion-envio v-model="mostrarFormularioAnadirDirecionEnvio" />
                            <q-btn @click="anadirDireccionEnvio" class="boton-anadir-direccion-envio"
                                label="Añadir nueva dirección de envío">
                                <q-icon name="mdi-map-marker" />
                            </q-btn>
                        </q-card-section>
                    </q-card>
                </div>
            </div>

            <q-separator />

            <div>
                <div v-if="usuario.direccionesEnvioFacturacion.direccionesFacturacion.length > 0">
                    <h6 class="title">Direccion/es Facturación</h6>
                    <formulario-anadir-direccion-envio v-model="mostrarFormularioAnadirDirecionEnvio" />
                    <q-btn @click="anadirDireccionEnvio" class="boton-anadir-direccion-envio"
                        label="Añadir nueva dirección de facturación">
                        <q-icon name="mdi-map-marker" />
                    </q-btn>
                    <q-card v-for="(direccion, index) in usuario.direccionesEnvioFacturacion.direccionesFacturacion"
                        :key="index" class="address-card">
                        <q-card-section>
                            <div class="info-direcciones">{{ direccion }}</div>
                        </q-card-section>
                    </q-card>
                </div>
                <div v-else>
                    <q-card class="q-ma-md">
                        <q-card-section>
                            <div class="title">No hay direcciones de facturación disponibles</div>
                            <formulario-anadir-direccion-envio v-model="mostrarFormularioAnadirDirecionEnvio" />
                            <q-btn @click="anadirDireccionEnvio" class="boton-anadir-direccion-envio"
                                label="Añadir nueva dirección de facturación">
                                <q-icon name="mdi-map-marker" />
                            </q-btn>
                        </q-card-section>

                    </q-card>
                </div>
            </div>
        </div>
    </div>
</template>



<script setup>
import { ref, defineProps, onBeforeMount, computed } from 'vue';
import { usuarioComposable } from '~/composables/usuarioComposable';




//USAR QUASAR
const quasar = useQuasar()


const { direccionesUsuario, usuario } = usuarioComposable();


const loading = ref(true)

// FUNCION PARA TRAER LOS PEDIDOS DEL USUARIO 
onBeforeMount(async () => {
    await direccionesEnvioFacturacion();
    console.log("Direcciones envio", usuario.value.direccionesEnvioFacturacion);
    loading.value = false
})


const direccionesEnvioFacturacion = async () => {
    try {
        const response = await direccionesUsuario();
        console.log("RESPONSE DIRECCION FACTURACION: ", response.data);
    } catch (error) {
        // Error de red u otro error
        console.error('Error al ver las direcciones de envio y facturación', error);
        mostrarAlertaError('Error al ver las direcciones de envio y facturación', quasar);
    }
}

const mostrarFormularioAnadirDirecionEnvio = ref(false);

const anadirDireccionEnvio = () => {
    // Abro el formulario si no esta abierto
    if (!mostrarFormularioAnadirDirecionEnvio.value) {
        mostrarFormularioAnadirDirecionEnvio.value = true;
    }
};




</script>

<style lang="scss" scoped>
.container {
    max-height: 79vh;
    overflow: auto;
}

.no-address-card {
    margin: 10px 0;
}

.address-card {
    margin: 1em;
    background-color: #e0e0e0;
    border-radius: 5px;
    border: 2px solid gray;
}

.info-direcciones {
    font-size: 1.2em;
}

.title {
    font-size: 1.5em;
    text-transform: uppercase;
    font-weight: bold;
    color: #005a8b;
    margin-bottom: 0.5em;
}

.boton-anadir-direccion-envio {
  background-color: #5A9; 
  color: white; 
  margin-top: 0; 
  transition: background-color 0.3s, box-shadow 0.3s; 
}

.boton-anadir-direccion-envio:hover {
  background-color: #3A7; /* Slightly darker color on hover */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* Shadow effect on hover */
}
</style>
