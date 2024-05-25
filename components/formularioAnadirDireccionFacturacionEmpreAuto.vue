<template>
    <q-dialog> <q-card>
            <div class="container">
                <div class="scrollable-container">
                    <q-form @submit.prevent="envioFormulario" @reset="borrar">
                        <h3>Facturación Empresa-Autónomo </h3>

                        <!-- Campo cif o nif -->
                        <q-input v-model="direccionFacturacionEmpreAuto.cifONifFacturacion" label="Cif o Nif" dense
                            lazy-rules maxlength="9" minlength="9" :rules="[
                                esNumero,
                                val => val && val.length > 0 || 'Por favor, introduce algo',
                                val => /^\S.*\S$/.test(val) || 'El Cif o Nif no puede empezar ni terminar con espacios en blanco y debe contener exactamente 9 dígitos',
                                val => /^\d{9}$/.test(val) || 'El Cif o Nif debe debe contener exactamente 9 dígitos'
                            ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-account-card" />
                            </template>
                        </q-input>


                        <!-- Campo numero de telefono -->
                        <q-input v-model="direccionFacturacionEmpreAuto.numTelefonoFacturacion"
                            label="Número de telefono" dense lazy-rules maxlength="9" minlength="9" :rules="[
                                esNumero,
                                val => val && val.length > 0 || 'Por favor, introduce algo',
                                val => /^\S.*\S$/.test(val) || 'El número de teléfono no puede empezar ni terminar con espacios en blanco y debe contener exactamente 9 dígitos',
                                val => /^\d{9}$/.test(val) || 'El número de telfono debe contener exactamente 9 dígitos'
                            ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-phone-outline" />
                            </template>
                        </q-input>

                        <!--CAMPO  DIRECCION-->
                        <q-input v-model="direccionFacturacionEmpreAuto.direccionDeFacturacion" label="Dirección" dense
                            minlength="2" maxlength="100" lazy-rules :rules="[
                                val => val && val.length > 0 || 'Por favor, introduce algo',
                                val => /^.{2,100}$/.test(val) || 'La dirección debe tener entre 2 y 100 caracteres',
                                val => /^\S.*\S$/.test(val) || 'La dirección no puede empezar ni terminar con espacios en blanco',
                            ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-map-marker" />
                            </template>
                        </q-input>

                        <!--CAMPO numero de dirección-->
                        <q-input v-model="direccionFacturacionEmpreAuto.numeroDeFacturacion" label="Número" dense
                            minlength="1" maxlength="10" :rules="[
                                val => val && val.length > 0 || 'Por favor, introduce algo',
                                val => /^.{1,10}$/.test(val) || 'El número debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco',
                                val => /^\S.*\S$|^\S$/.test(val) || 'El número no puede empezar ni terminar con espacios en blanco',
                                // val => /^\S.*\S$/.test(val) 

                            ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-numeric" />
                            </template>
                        </q-input>

                        <!--CAMPO piso de dirección-->
                        <q-input v-model="direccionFacturacionEmpreAuto.pisoDeFacturacion" label="Piso" dense
                            minlength="1" maxlength="10" :rules="[
                                esNumeroCampoNoObligatorio,
                                val => !val || /^(?!\s).{1,10}(?<!\s)$/.test(val) || 'El número debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco'
                            ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-stairs" />
                            </template>
                        </q-input>

                        <!--CAMPO puerta de dirección-->
                        <q-input v-model="direccionFacturacionEmpreAuto.puertaDeFacturacion" label="Puerta" dense
                            lazy-rules minlength="1" maxlength="10" :rules="[
                                val => !val || (/^.{1,10}$/.test(val) && !/^\s|\s$/.test(val)) || 'La puerta debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco',
                            ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-door" />
                            </template>
                        </q-input>


                        <!--CAMPO CODIGO POSTAL-->
                        <q-input v-model="direccionFacturacionEmpreAuto.codigoPostalDeFacturacion" label="Código Postal"
                            dense lazy-rules maxlength="10" minlength="5" :rules="[
                                esNumero,
                                val => val && val.length > 0 || 'Por favor, introduce algo',
                                val => /^\S.*\S$/.test(val) || 'El código postal no puede empezar ni terminar con espacios en blanco y debe contener 5 a 10 dígitos',
                                val => /^\d{5,10}$/.test(val) || 'El código postal debe tener entre 5 y 10 dígitos'
                            ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-mailbox" />
                            </template>
                        </q-input>

                        <!--CAMPO PROVINCIA-->
                        <q-input v-model="direccionFacturacionEmpreAuto.provinciaDeFacturacion" minlength="2"
                            maxlength="100" label="Provincia" dense lazy-rules :rules="[
                                val => val && val.length > 0 || 'Por favor, introduce algo',
                                val => /^.{2,100}$/.test(val) || 'La provincia debe tener entre 2 y 100 caracteres',
                                val => /^\S.*\S$/.test(val) || 'La provincia no puede empezar ni terminar con espacios en blanco',
                                val => /^[a-zA-ZáéíóúÁÉÍÓÚüÜ\s]+$/.test(val) || 'La provincia solo puede contener letras y tildes'
                            ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-map" />
                            </template>
                        </q-input>

                        <!-- Botones de enviar y reiniciar -->
                        <div class="q-mt-lg d-flex justify-around">
                            <!-- Boton de enviar -->
                            <q-btn label="Añadir dirección facturación" type="submit"
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


const { anadirDireccionFacturacionEmpreAuto, usuario } = usuarioComposable();

const quasar = useQuasar()
const router = useRouter()

const direccionFacturacionEmpreAuto = reactive({
    cifONifFacturacion: '',
    numTelefonoFacturacion: '',
    direccionDeFacturacion: '',
    numeroDeFacturacion: '',
    pisoDeFacturacion: '',
    puertaDeFacturacion: '',
    codigoPostalDeFacturacion: '',
    provinciaDeFacturacion: ''
});

const borrar = () => {

    direccionFacturacionEmpreAuto.cifONifFacturacion = '',
        direccionFacturacionEmpreAuto.numTelefonoFacturacion = '',
        direccionFacturacionEmpreAuto.direccionDeFacturacion = '',
        direccionFacturacionEmpreAuto.numeroDeFacturacion = '',
        direccionFacturacionEmpreAuto.pisoDeFacturacion = '',
        direccionFacturacionEmpreAuto.puertaDeFacturacion = '',
        direccionFacturacionEmpreAuto.codigoPostalDeFacturacion = '',
        direccionFacturacionEmpreAuto.provinciaDeFacturacion = ''
};


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
        const response = await anadirDireccionFacturacionEmpreAuto(direccionFacturacionEmpreAuto);
        if (response.data === 'Dirección de facturación añadida exitosamente') {
            emit('pokemon', false)
            mostrarAlertaExito('Dirección anadida exitosamente', quasar);
            const direccionFormateada = formatearDireccion(direccionFacturacionEmpreAuto);
            usuario.value.direccionesEnvioFacturacion.direccionesFacturacion.push(direccionFormateada);
    
        }

        if (response.data === 'La dirección de facturación ya existe') {
            mostrarAlertaError('La dirección de facturación ya existe', quasar);
        }
        console.log("response ", response);

    } catch (error) {
        // Error de red o algo parecido
        console.error('Error al crear el usuario:', error);
        mostrarAlertaError('Error al crear el usuario intentelo más tarde', quasar);
    }
}


function formatearDireccion(direccionFacturacionEmpreAuto) {
    const { cifONifFacturacion, numTelefonoFacturacion, direccionDeFacturacion, numeroDeFacturacion, pisoDeFacturacion, puertaDeFacturacion, codigoPostalDeFacturacion, provinciaDeFacturacion } = direccionFacturacionEmpreAuto;

    // Formatear la dirección base
    let direccionFormateada = `FACTURACIÓN: EMPRESA/AUTÓNOMO, CIF/NIF: ${cifONifFacturacion.toUpperCase()}, NÚMERO DE TELÉFONO: ${numTelefonoFacturacion.toUpperCase()},  ${direccionDeFacturacion.toUpperCase()}, Nº ${numeroDeFacturacion}`;

    // Agregar piso y puerta si están presentes
    if (pisoDeFacturacion && pisoDeFacturacion.trim() !== '') {
        direccionFormateada += `, PISO ${pisoDeFacturacion.toUpperCase()}`;
    }
    if (puertaDeFacturacion && puertaDeFacturacion.trim() !== '') {
        direccionFormateada += `, PUERTA ${puertaDeFacturacion.toUpperCase()}`;
    }

    // Agregar código postal y provincia
    direccionFormateada += `, ${codigoPostalDeFacturacion}, ${provinciaDeFacturacion.toUpperCase()}`;

    return direccionFormateada;
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
        max-height: 65vh;
        /* Ajusta este valor según tus necesidades */
    }

    .container {
        max-width: 100%;
        padding: 0 1em;
        /* Añade un poco de padding si es necesario */
    }
}
</style>