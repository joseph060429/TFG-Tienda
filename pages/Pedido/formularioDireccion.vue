<template>
    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
    <div class="scrollable-container">
        <q-form @submit.prevent="envioFormulario">
            <h3>Direccion de envío </h3>
            <!-- CAMPO TIPO PAGO PAYPAL readonly class="readonly" para que no se pueda editar-->
            <q-input v-model="datosEnvio.tipoPago" label="Tipo de Pago*" readonly class="readonly">
                <template v-slot:prepend>
                    <q-icon name="mdi-credit-card" />
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
            <q-input v-model="datosEnvio.puerta" label="Puerta" dense lazy-rules minlength="1" maxlength="10" :rules="[
                val => !val || /^\S.{0,8}\S$/.test(val) || 'La puerta debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco'
            ]">
                <template v-slot:prepend>
                    <q-icon name="mdi-door" />
                </template>
            </q-input>

            <!--CAMPO numero de telefono-->
            <q-input v-model="datosEnvio.numTelefono" label="Número de teléfono" dense minlength="9" maxlength="9"
                :rules="[
                    esNumero,
                    val => val && val.trim().length > 0 || 'El número de teléfono no puede estar en blanco',
                    val => /^(?!\s)(?=\S).{9}(?!\s)$/.test(val) || 'El número teléfono debe tener 9 caracteres y no puede empezar ni terminar con espacios en blanco'
                ]">
                <template v-slot:prepend>
                    <q-icon name="mdi-phone" />
                </template>
            </q-input>
            <q-btn type="submit" label="Enviar" color="primary" />
        </q-form>
    </div>

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
    puerta: '',
    numTelefono: ''
});

definePageMeta({
    role: ['ROLE_USER']
});

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


const envioFormulario = () => {
    console.log("formulario enviado");
};

const regresar = () => {
    router.push({ path: '/pedido/carritoCompra' })
};

</script>

<style lang="scss" scoped>
.scrollable-container {
    max-height: 70vh;
    overflow: auto;
}
</style>