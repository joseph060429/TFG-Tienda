<template>
    <q-dialog v-model="mostrarFormularioAnadirDirecionEnvio"> <q-card>
            <!-- <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" /> -->
            <div class="container">
                <div class="scrollable-container">
                    <q-form @submit.prevent="envioFormulario" @reset="borrar">
                        <h3>Direccion de envío </h3>
                        <!-- CAMPO TIPO PAGO PAYPAL readonly class="readonly" para que no se pueda editar-->
                        <!-- <q-input v-model="datosEnvio.tipoPago" label="Tipo de Pago*" readonly class="readonly">
                            <template v-slot:prepend>
                                <q-icon name="mdi-credit-card" />
                            </template>
                        </q-input> -->
                        <!--CAMPO  DIRECCION-->
                        <q-input v-model="datosEnvio.direccion" label="Dirección" dense lazy-rules :rules="[
                            val => val && val.length > 0 || 'Por favor, introduce algo',
                            val => /^.{2,100}$/.test(val) || 'La dirección debe tener entre 2 y 100 caracteres',
                            val => /^\S.*\S$/.test(val) || 'La dirección no puede empezar ni terminar con espacios en blanco',
                        ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-map-marker" />
                            </template>
                        </q-input>

                        <!--CAMPO numero de dirección-->
                        <q-input v-model="datosEnvio.numero" label="Número" dense minlength="1" maxlength="10" :rules="[
                            esNumero,
                            val => val && val.trim().length > 0 || 'El número no puede estar en blanco',
                            val => /^(?!\s)(?=\S).{1,10}(?!\s)$/.test(val) || 'El número debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco'
                        ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-numeric" />
                            </template>
                        </q-input>

                        <!--CAMPO piso de dirección-->
                        <q-input v-model="datosEnvio.piso" label="Piso" dense minlength="1" maxlength="10" :rules="[
                            esNumeroCampoNoObligatorio,
                            val => !val || /^(?!\s).{1,10}(?<!\s)$/.test(val) || 'El número debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco'
                        ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-stairs" />
                            </template>
                        </q-input>

                        <!--CAMPO puerta de dirección-->
                        <q-input v-model="datosEnvio.puerta" label="Puerta" dense lazy-rules minlength="1"
                            maxlength="10" :rules="[
                                val => !val || /^\S.{0,8}\S$/.test(val) || 'La puerta debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco'
                            ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-door" />
                            </template>
                        </q-input>



                        <!--CAMPO CODIGO POSTAL-->
                        <q-input v-model="datosEnvio.codigoPostal" label="Código Postal" dense lazy-rules maxlength="10"
                            minlength="5" :rules="[
                                esNumero,
                                val => /^\S.*\S$/.test(val) || 'El código postal no puede empezar ni terminar con espacios en blanco y debe tener entre 5 y 10 caracteres',
                                val => val && val.length > 0 || 'Por favor, introduce algo',
                            ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-mailbox" />
                            </template>
                        </q-input>

                        <!--CAMPO PROVINCIA-->
                        <q-input v-model="datosEnvio.provincia" label="Provincia" dense lazy-rules :rules="[
                            val => val && val.length > 0 || 'Por favor, introduce algo',
                            val => /^.{2,100}$/.test(val) || 'La provincia debe tener entre 2 y 100 caracteres',
                            val => /^\S.*\S$/.test(val) || 'La provincia no puede empezar ni terminar con espacios en blanco',
                        ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-map" />
                            </template>
                        </q-input>

                        <!--CAMPO numero de telefono-->
                        <!-- <q-input v-model="datosEnvio.numTelefono" label="Número de teléfono" dense minlength="9"
                            maxlength="9" :rules="[
                                esNumero,
                                val => val && val.trim().length > 0 || 'El número de teléfono no puede estar en blanco',
                                val => /^(?!\s)(?=\S).{9}(?!\s)$/.test(val) || 'El número teléfono debe tener 9 caracteres y no puede empezar ni terminar con espacios en blanco'
                            ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-phone" />
                            </template>
                        </q-input> -->

                        <!-- <q-select v-model="tipo_facturacion" :options="opcionesFacturacion" dense outlined />

                        <q-select v-if="tipo_facturacion === 'particular'" />


                        <template v-if="tipo_facturacion === 'empresa_autonomo'">
                            <q-select v-model="marcaProductoSeleccionado" :options="marcasUnicas" dense outlined
                                @update:model-value="actualizarMarcas" />
                        </template> -->

                        <!-- Botones de enviar y reiniciar -->
                        <div class="q-mt-lg d-flex justify-around">
                            <!-- Boton de enviar -->
                            <q-btn label="Añadir dirección envío" type="submit"
                                class="full-width enviar-button col-xs-12 col-sm-6">
                                <q-icon name="mdi-plus-circle-outline" class="q-ml-md inline"></q-icon>
                                <!-- Icono para el botón -->
                            </q-btn>

                            <!-- Boton de reiniciar -->
                            <q-btn label="Reiniciar" type="reset" flat
                                class="full-width q-ml-sm custom-button-reiniciar col-xs-12 col-sm-6">
                                <q-icon name="mdi-refresh" class="q-ml-md inline"></q-icon>
                                <!-- Icono para el botón -->
                            </q-btn>
                        </div>
                    </q-form>
                </div>
            </div>
        </q-card>
    </q-dialog>
</template>

<script setup>
import { ref, defineProps, onBeforeMount, toRefs } from 'vue';
import { useRouter } from 'vue-router';
import { usuarioComposable } from '~/composables/usuarioComposable';

// const tipo_facturacion = ref('');

// const opcionesFacturacion = [
//     { label: 'Escoge un tipo de facturación', value: '' },
//     { label: 'Facturación particular', value: 'particular' },
//     { label: 'Empresa-Autónomo', value: 'empresa_autonomo' },
// ];

const { anadirDireccionEnvio, usuario } = usuarioComposable();

const quasar = useQuasar()
const router = useRouter()

const datosEnvio = reactive({
    // tipoPago: 'PayPal',
    direccion: '',
    numero: '',
    piso: '',
    puerta: '',
    codigoPostal: '',
    provincia: '',
    numTelefono: ''
});

const borrar = () => {
    datosEnvio.direccion = '',
    datosEnvio.numero = '',
    datosEnvio.piso = '',
    datosEnvio.puerta = '',
    datosEnvio.codigoPostal = ''
    datosEnvio.provincia = ''
    // datosEnvio.numTelefono = ''
};



const props = defineProps({
    formularioAñadirDireccionEnvio: Boolean,
})

const emit = defineEmits(['pokemon'])
const esNumero = (val) => {
    if (!val || isNaN(val)) {
        return 'Por favor, introduce solo números.';
    }
    return true;
};

const esNumeroCampoNoObligatorio = (val) => {
    if (val && isNaN(val)) {
        return 'Por favor, introduce solo números.';
    }
    return true;
};


const envioFormulario = async () => {
    try {
      const response = await anadirDireccionEnvio(datosEnvio);
    if(response.data === 'Dirección añadida exitosamente'){
        mostrarAlertaExito('Dirección anadida exitosamente', quasar);
        emit('pokemon', false)
        usuario.value.direccionesEnvioFacturacion.direccionesEnvio.push(datosEnvio)

    }
      console.log("response " , response);
    
    } catch (error) {
      // Error de red o algo parecido
      console.error('Error al crear el usuario:', error);
      mostrarAlertaError('Error al crear el usuario intentelo más tarde', quasar);
    }
  }

</script>

<style lang="scss" scoped>
.scrollable-container {
    max-height: 90vh;
    overflow: auto;
}

.container {
    max-width: 90%;
    margin: auto;
    text-align: center;
}

@media (max-width: 600px) {
    .scrollable-container {
        max-height: 65vh; /* Ajusta este valor según tus necesidades */
    }

    .container {
        max-width: 100%;
        padding: 0 1em; /* Añade un poco de padding si es necesario */
    }
}

</style>