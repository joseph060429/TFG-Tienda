<template>
    <!-- <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" /> -->
    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
    <div class="container">
        <eliminar-direcciones v-model="mostrarBorrarDireccion" :indice="indice" :modo="modo" />
        <!-- <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" /> -->
        <div v-if="!loading">
            <div>
                <div v-if="usuario.direccionesEnvioFacturacion.direccionesEnvio.length > 0">
                    <h6 class="title">Direccion/es de envío</h6>
                    <formulario-anadir-direccion-envio @pokemon="test($event)"
                        v-model="mostrarFormularioAnadirDirecionEnvio" />
                    <q-btn @click="anadirDireccionEnvio" class="boton-anadir-direccion-envio"
                        label="Nueva dirección de envío">
                        <q-icon name="mdi-map-marker" />
                    </q-btn>
                    <q-card v-for="(direccion, index) in usuario.direccionesEnvioFacturacion.direccionesEnvio"
                        :key="index" class="address-card-envio">

                        <div class="checked" @click="selectDireccion(direccion, 'envio')">
                            <q-icon size=" 35px" name="mdi-checkbox-marked"
                                v-if="seleccionDireciones.envio === direccion" /> <q-icon size="35px"
                                name="mdi-checkbox-blank-outline" v-else />
                        </div>

                        <q-card-section>

                            <div class="info-direcciones">{{ direccion }}</div>
                            <q-btn @click="borrar(index, 'envio')" class="boton-borrar">
                                <q-icon name="mdi-delete" />
                            </q-btn>

                        </q-card-section>
                    </q-card>
                </div>
                <div v-else>
                    <q-card class="no-address-card">
                        <q-card-section>
                            <div class="title">No hay direcciones de envío disponibles</div>
                            <formulario-anadir-direccion-envio @pokemon="test($event)"
                                v-model="mostrarFormularioAnadirDirecionEnvio" />
                            <q-btn @click="anadirDireccionEnvio" class="boton-anadir-direccion-envio"
                                label="Nueva dirección de envío">
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
                    <!-- EMPRESA-AUTONOMO -->
                    <formulario-anadir-direccion-facturacion-empre-auto @pokemon="test($event)"
                        v-model="mostrarFormularioAnadirDirecionFacturacionEmpreAuto" />
                    <q-btn @click="anadirDireccionFacturacionEmpreAuto" class="boton-anadir-facturacion-empreAuto"
                        label="Empresa-Autónomo">
                        <q-icon name="mdi-briefcase-account" />
                    </q-btn>
                    <!-- PARTICULAR -->
                    <formulario-anadir-direccion-facturacion-particular @pokemon="test($event)"
                        v-model="mostrarFormularioAnadirDirecionFacturacionParticular" />
                    <q-btn @click="anadirDireccionFacturacionParticular" class="boton-anadir-facturacion-particular"
                        label="Particular">
                        <q-icon name="mdi-home-account" />
                    </q-btn>
                    <q-card v-for="(direccion, index) in usuario.direccionesEnvioFacturacion.direccionesFacturacion"
                        :key="index" class="address-card">
                        <div class="checked" @click="selectDireccion(direccion, 'facturacion')">
                            <q-icon size=" 35px" name="mdi-checkbox-marked"
                                v-if="seleccionDireciones.facturacion === direccion" /> <q-icon size="35px"
                                name="mdi-checkbox-blank-outline" v-else />
                        </div>
                        <q-card-section>
                            <div class="info-direcciones">{{ direccion }}</div>
                        </q-card-section>

                        <q-btn @click="borrar(index, 'facturacion')" class="boton-borrar">
                            <q-icon name="mdi-delete" />
                        </q-btn>

                    </q-card>
                </div>
                <div v-else>
                    <q-card class="no-address-card">
                        <q-card-section>
                            <div class="title">No hay direcciones de facturación disponibles</div>
                            <formulario-anadir-direccion-facturacion-empre-auto @pokemon="test($event)"
                                v-model="mostrarFormularioAnadirDirecionFacturacionEmpreAuto" />
                            <q-btn @click="anadirDireccionFacturacionEmpreAuto"
                                class="boton-anadir-facturacion-empreAuto" label="Empresa-Autónomo">
                                <q-icon name="mdi-briefcase-account" />
                            </q-btn>
                            <!-- PARTICULAR -->
                            <formulario-anadir-direccion-facturacion-particular @pokemon="test($event)"
                                v-model="mostrarFormularioAnadirDirecionFacturacionParticular" />
                            <q-btn @click="anadirDireccionFacturacionParticular"
                                class="boton-anadir-facturacion-particular" label="Particular">
                                <q-icon name="mdi-home-account" />
                            </q-btn>
                        </q-card-section>

                    </q-card>
                </div>
            </div>
        </div>
    </div>
    <q-btn class="btn-crearPedido" label="crear pedido"
        :disable="(seleccionDireciones.envio == '' || seleccionDireciones.facturacion == '')"
        @click="crearPedido()"></q-btn>
</template>



<script setup>
import { ref, defineProps, onBeforeMount, computed } from 'vue';
import { usuarioComposable } from '~/composables/usuarioComposable';
import { useRouter } from 'vue-router';

const { usuario } = usuarioComposable()

const quasar = useQuasar()

const mostrarFormularioAnadirDirecionEnvio = ref(false);
const mostrarFormularioAnadirDirecionFacturacionEmpreAuto = ref(false);
const mostrarFormularioAnadirDirecionFacturacionParticular = ref(false);

const seleccionDireciones = ref({
    envio: '',
    facturacion: ''
})

const mostrarBorrarDireccion = ref(false);
const indice = ref(null);
const modo = ref('');
const paypalUrl = ref('');

function selectDireccion(direccion, tipo) {
    seleccionDireciones.value[tipo] = direccion
    console.log(seleccionDireciones.value)
}

function crearPedido() {
    if (seleccionDireciones.value.envio == '' || seleccionDireciones.value.facturacion == '') {
        return;
    }
    let emergente;
    let partesEnvio = seleccionDireciones.value.envio.split(",");
    let partesFact = seleccionDireciones.value.facturacion.split(",");
    let factSplit = []
    for (let dato of partesFact) {
        factSplit.push(dato.split(':'))
    }
    console.log(factSplit, partesEnvio, 'por favor')
    let form = {
        productos: usuario.value.carrito.map((obj) => ({ _id: obj.productoId, cantidadProducto: obj.cantidadAnadidaAlCarrito })),
        tipoPago: "Paypal",
        numero: partesEnvio[1].split(' ')[2],

        codigoPostal: partesEnvio[2].split(' ')[1].toLowerCase().includes('piso') ? partesEnvio[4].trim() : partesEnvio[2].trim(),
        provincia: partesEnvio[2].split(' ')[1].toLowerCase().includes('piso') ? partesEnvio[5].trim() : partesEnvio[3].trim(),
        direccion: partesEnvio[0],
        piso: partesEnvio[2].split(' ')[1].toLowerCase().includes('piso') ? partesEnvio[2].split(' ')[2] : '',
        puerta: partesEnvio[3].split(' ')[1].toLowerCase().includes('puerta') ? partesEnvio[3].split(' ')[2] : '',
        tipoFacturacion: factSplit[0][1].toLowerCase().trim().includes('particular') ? factSplit[0][1].toLowerCase().trim() : factSplit[0][1].toLowerCase().trim().split('/')[0] + '_' + factSplit[0][1].toLowerCase().trim().split('/')[1].normalize('NFD').replace(/[\u0300-\u036f]/g, ""),
    }

    if (form.tipoFacturacion.includes('particular')) {
        form.numTelefono = parseInt(factSplit[3][1])
        form.particularDireccionFacturacionDTO = {
            nombreFacturacion: factSplit[1][1].trim(),
            apellidoFacturacion: factSplit[2][1].trim(),
            direccionDeFacturacion: factSplit[4][0].trim(),
            numeroDeFacturacion: factSplit[5][0].split(' ')[2].trim(),
            pisoDeFacturacion: factSplit[6][0].split(' ')[1].trim().toLowerCase().includes('piso') ? factSplit[6][0].split(' ')[2] : '',
            numTelefonoFacturacion: form.numTelefono = parseInt(factSplit[3][1]),
            puertaDeFacturacion: factSplit[7][0].split(' ')[1].trim().toLowerCase().includes('puerta') ? factSplit[7][0].split(' ')[2] : '',
            codigoPostalDeFacturacion: factSplit[6][0].split(' ')[1].trim().toLowerCase().includes('piso') ? factSplit[8][0].trim() : factSplit[6][0].trim(),
            provinciaDeFacturacion: factSplit[6][0].split(' ')[1].trim().toLowerCase().includes('piso') ? factSplit[9][0].trim() : factSplit[7][0].trim()
        }
    } else {
        form.numTelefono = parseInt(factSplit[2][1])
        form.empresaAutonomoDireccionFacturacionDTO = {
            cifONifFacturacion: factSplit[1][1].trim(),
            numTelefonoFacturacion: form.numTelefono = parseInt(factSplit[2][1]),
            direccionDeFacturacion: factSplit[3][0].trim(),
            numeroDeFacturacion: factSplit[4][0].split(' ')[2].trim(),
            pisoDeFacturacion: factSplit[5][0].split(' ')[1].trim().toLowerCase().includes('piso') ? factSplit[5][0].split(' ')[2] : '',
            puertaDeFacturacion: factSplit[6][0].split(' ')[1].trim().toLowerCase().includes('puerta') ? factSplit[6][0].split(' ')[2] : '',
            codigoPostalDeFacturacion: factSplit[5][0].split(' ')[1].trim().toLowerCase().includes('piso') ? factSplit[7][0].trim() : factSplit[5][0].trim(),
            provinciaDeFacturacion: factSplit[5][0].split(' ')[1].trim().toLowerCase().includes('piso') ? factSplit[8][0].trim() : factSplit[6][0].trim()
        }
    }

    console.log(form)
    useAxiosInstance().post('/usuarios/pedidos/crearPedido', form, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
    }).then((response) => {


        if (response.status == 400) {
            emits('error', response.data)
        }

        emergente = window.open(response.data, '_self')


    }).catch((error) => {
        console.log(error, ' error desde axios meter ñedodo retdx')
        emits('error', error.response.data)
    })


}

function borrar(index, mode) {
    indice.value = index
    modo.value = mode
    mostrarBorrarDireccion.value = true
}

const router = useRouter()


const emits = defineEmits(['error', 'loading'])



const { direccionesUsuario } = usuarioComposable();


const loading = ref(true)

// FUNCION PARA TRAER LOS PEDIDOS DEL USUARIO 
onBeforeMount(async () => {
    await direccionesEnvioFacturacion();
    console.log("Direcciones envio", usuario.value.direccionesEnvioFacturacion);
    loading.value = false
    if (usuarioComposable().usuario.value.carrito.length == 0) {
        await usuarioComposable().verMiCarrito()
    }
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
const test = (e) => {
    console.log('Me llega el valor?? ==> ', e);
    mostrarFormularioAnadirDirecionEnvio.value = e;
}

const anadirDireccionEnvio = () => {
    // Abro el formulario si no esta abierto
    if (!mostrarFormularioAnadirDirecionEnvio.value) {
        mostrarFormularioAnadirDirecionEnvio.value = true;
    }
};


const anadirDireccionFacturacionEmpreAuto = () => {
    // Abro el formulario si no esta abierto
    if (!mostrarFormularioAnadirDirecionFacturacionEmpreAuto.value) {
        mostrarFormularioAnadirDirecionFacturacionEmpreAuto.value = true;
    }
};


const anadirDireccionFacturacionParticular = () => {
    // Abro el formulario si no esta abierto
    if (!mostrarFormularioAnadirDirecionFacturacionParticular.value) {
        mostrarFormularioAnadirDirecionFacturacionParticular.value = true;
    }
};

const regresar = () => {
    router.push({ path: '/pedido/carritoCompra' })
};


const mostrarEliminarDireccionEnvio = ref(false);
const mostrarEliminarDireccionFacturacion = ref(false);

const eliminarDireccionEnvio = () => {
    // Abro el dialogo si no esta abierto
    if (!mostrarEliminarDireccionEnvio.value) {
        mostrarEliminarDireccionEnvio.value = true;
    }
};


const eliminarDireccionFacturacion = () => {
    if (!mostrarEliminarDireccionFacturacion.value) {
        mostrarEliminarDireccionFacturacion.value = true;
    }
}




</script>

<style lang="scss" scoped>
.container {
    max-width: 100%;
    max-height: 60vh;
    overflow: auto;
    text-align: center;
    // flex-direction: column;
    // justify-content: center; /* Centrar verticalmente */
    align-items: center;
    /* Centrar horizontalmente */
    // background-color: #3A7;
    // border: 2px solid black;
}

@media (max-width: 600px) {
    .container {
        max-width: 99%;
        height: 85vh;
        margin-top: 1%;
        // overflow: auto;
    }
}

.no-address-card {
    margin: 1em;
    background-color: #e0e0e0;
    border-radius: 5px;
    border: 2px solid gray;
    text-align: center;
    height: 22vh;
}


.address-card-envio {
    margin: 1em;
    background-color: #e0e0e0;
    border-radius: 5px;
    width: 99%;
    border: 2px solid gray;
    text-align: center;
    height: 20vh;
}

.address-card {
    margin: 1em;
    background-color: #e0e0e0;
    border-radius: 5px;
    border: 2px solid gray;
    height: 20vh;
    width: 99%;
    margin-bottom: 1em;


}

@media (max-width: 600px) {
    .no-address-card {
        margin-top: 1%;
        background-color: #e0e0e0;
        border-radius: 5px;
        border: 2px solid gray;
        text-align: center;
        height: 25vh;
        width: 90%;
    }

    .address-card {
        margin: 1em;
        background-color: #e0e0e0;
        border-radius: 5px;
        border: 2px solid gray;
        height: 27vh;
        width: 97%;
        margin-bottom: 1em;
        margin-top: 1em;
    }

    .address-card-envio {
        margin-top: 1em;
        background-color: #e0e0e0;
        border-radius: 5px;
        // border: 2px solid gray;
        text-align: center;
        height: 18vh;
        width: 97%;


    }
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
    margin-top: 0.5em;
}

.boton-anadir-direccion-envio {
    background-color: #5A9;
    color: white;
    margin-bottom: 1%;
    transition: background-color 0.3s, box-shadow 0.3s;
    width: 50%;
    max-height: 5vh;

}

.boton-anadir-direccion-envio:hover {
    background-color: #3A7;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

@media (max-width: 600px) {
    .boton-anadir-direccion-envio {
        width: 90%;
        margin: auto;
        max-height: 15vh;
        margin-top: 0.5em;
    }
}

.custom-regresar-button {
    margin-right: 80em;
    margin-top: 1em;
}

@media (max-width: 600px) {
    .custom-regresar-button {
        margin-right: 20em;
        margin-top: 0.5em;
    }
}

.boton-anadir-facturacion-particular,
.boton-anadir-facturacion-empreAuto {
    background-color: #5A9;
    // margin-left: 2em;
    margin-right: 2em;
    margin-top: 0.5em;
    margin-bottom: 0.5%;
    color: white;
    transition: background-color 0.3s, box-shadow 0.3s;
    // margin-top: 0;
    width: 50%;
    max-height: 5vh;
}

.boton-anadir-facturacion-particular:hover,
.boton-anadir-facturacion-empreAuto:hover {
    background-color: #3A7;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

@media (max-width: 600px) {

    .boton-anadir-facturacion-particular,
    .boton-anadir-facturacion-empreAuto {
        width: 90%;
        margin: auto;
        max-height: 15vh;
        margin-top: 0.5em;
    }
}

.boton-borrar {
    background-color: #FF4500;
    border: 1px solid;
    margin-left: 90%;
    // display: flex
}

.boton-borrar:hover {
    background-color: rgba(255, 69, 0, 0.8);

}

@media (max-width: 600px) {

    .boton-borrar {
        width: 5%;
        margin: auto;
        height: 1vh;
        margin-left: 75%;
    }
}


.btn-crearPedido {
    margin-top: 1%;
    margin-left: 84.5%;
    background-color: #005a8b;
}

@media (max-width: 600px) {
    .btn-crearPedido {
        margin-top: 1%;
        margin-left: 60%;
        background-color: #005a8b;
    }
}

.checked {
    width: 2%;
    height: 2.8em;
    margin-top: 1%;
    margin-left: 2%;
    cursor: pointer;
}

.checked .q-icon {
    pointer-events: none;
}

@media (max-width: 600px) {
    .checked {
        width: 5%;
        height: 2.8em;
        margin-top: 1%;
        margin-left: 2%;
        cursor: pointer;
    }
}
</style>
