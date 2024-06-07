<template>
    <q-dialog v-model="mostrarFormularioActualizarProducto"> <q-card>
            <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
            <div class="q-pa-xs">
                <div class="d-flex justify-center align-center" style="max-width: 80%; margin: auto;">
                    <h1 class="text-h4 q-mb-xs text-center q-mt-none"
                        style="color: #333333; font-weight: bold; text-transform: uppercase; letter-spacing: 2px;">
                        Actualizar Producto
                    </h1>
                    <div style="overflow: auto;">
                        <q-form @submit.prevent="actualizarDatosProducto" @reset="borrar" class="q-gutter-xs"
                            style="max-width: 100%; margin: auto; max-height: 51.5vh;">

                            <!-- Campo categoria producto -->
                            <q-select v-model="datosProductoActualizar.categoriaProducto" :options="opcionesCategorias"
                                label="Categoría del producto *"
                                :rules="[v => !!v || 'Debes seleccionar una categoría']" />


                            <!-- Campo nombreProducto -->
                            <q-input filled v-model="datosProductoActualizar.nombreProducto"
                                label="Nombre del producto *" lazy-rules :rules="[
                                    val => val && val.length > 0 || 'Por favor, introduce algo',
                                    val => /^.{2,70}$/.test(val) || 'El nombre del producto debe tener entre 2 y 70 caracteres',
                                    val => /^\S.*\S$/.test(val) || 'El nombre del producto no puede empezar ni terminar con espacios en blanco',
                                ]">
                                <template v-slot:prepend>
                                    <q-icon name="mdi-label" />
                                </template>
                            </q-input>



                            <!-- Campo descripcionProducto -->
                            <q-input filled v-model="datosProductoActualizar.descripcionProducto"
                                label="Descripción del producto *" type="textarea" lazy-rules :rules="[
                                    val => val && val.length > 0 || 'Por favor, introduce algo',
                                    // val => /^.{2}$/.test(val) || 'La descripción del producto debe tener entre 2 y 70 caracteres',
                                    val => /^\S.*\S$/.test(val) || 'La descripción del producto no puede empezar ni terminar con espacios en blanco',
                                ]">
                                <template v-slot:prepend>
                                    <q-icon name="mdi-comment-text-outline" />
                                </template>
                            </q-input>

                            <!-- Campo precioProducto -->
                            <q-input filled v-model="datosProductoActualizar.precioProducto"
                                label="Precio del producto *" lazy-rules :rules="[
                                    val => val !== null && val !== undefined || 'Por favor, introduce algo',
                                    val => !isNaN(val) || 'El precio debe ser un número',
                                    val => parseFloat(val) > 0 || 'El precio debe ser mayor que cero'
                                ]">
                                <template v-slot:prepend>
                                    <q-icon name="mdi-currency-eur" />
                                </template>
                            </q-input>

                            <!-- Campo cantidadProducto -->
                            <q-input filled v-model="datosProductoActualizar.cantidadProducto"
                                label="Cantidad del producto *" lazy-rules :rules="[
                                    val => val !== null && val !== undefined || 'Por favor, introduce algo',
                                    val => !isNaN(val) || 'La cantidad debe ser un número',
                                    val => parseInt(val) >= 0 || 'La cantidad debe ser mayor o igual que cero'
                                ]">
                                <template v-slot:prepend>
                                    <q-icon name="mdi-numeric" />
                                </template>
                            </q-input>
                            <!-- Campo marcaProducto -->
                            <q-input filled v-model="datosProductoActualizar.marcaProducto" label="Marca del producto *"
                                lazy-rules :rules="[
                                    val => val && val.length > 0 || 'Por favor, introduce algo',
                                    val => /^.{2,50}$/.test(val) || 'La marca del producto debe tener entre 2 y 50 caracteres',
                                    val => /^\S.*\S$/.test(val) || 'La marca del producto no puede empezar ni terminar con espacios en blanco',

                                ]">
                                <template v-slot:prepend>
                                    <q-icon name="mdi-watermark" />
                                </template>
                            </q-input>


                            <!-- Campo especificacionesTecnicas -->
                            <q-input v-model="datosProductoActualizar.especificacionesTecnicas" filled
                                label="Especificaciones técnicas del producto *" type="textarea" dense :rows="5" :rules="[
                                    val => val && val.length > 0 || 'Por favor, introduce algo',
                                    val => val.length >= 2 || 'Las especificaciones técnicas del producto deben tener al menos 2 caracteres',
                                    val => /^\S.*\S$/.test(val.replace(/\n|\r/g, '')) || 'Las especificaciones técnicas del producto no pueden empezar ni terminar con espacios en blanco'

                                ]">
                                <template v-slot:prepend>
                                    <q-icon name="mdi-text-box-multiple" />
                                </template>
                            </q-input>

                            <!-- Campo imagenProducto -->
                            <q-file ref="imgProducto" v-model="datosProductoActualizar.imagen" @update:model-value="onFileChange" filled
                                label="Imagen del Producto *">
                                <template v-slot:append>
                                    <q-icon name="mdi-paperclip" @click="pickFile()" class="cursor-pointer" />
                                    <q-icon name="mdi-close" @click.stop.prevent="datosProductoActualizar.imagen = null"
                                        class="cursor-pointer" />

                                </template>
                            </q-file>

                            <!-- Botones de enviar y reiniciar -->
                            <div class="q-mt-lg d-flex justify-around">
                                <!-- Boton de enviar -->
                                <q-btn label="Actualizar Producto" type="submit"
                                    class="full-width enviar-button col-xs-12 col-sm-6">
                                    <q-icon name="mdi-plus-circle" class="q-ml-md inline"></q-icon>
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
            </div>
        </q-card>
    </q-dialog>
</template>


<script setup>

// IMPORTACIONES
import { useRouter } from 'vue-router'
import { mostrarAlertaExito, mostrarAlertaError } from '~/utils/alertas';
import { reactive, ref, onMounted } from "vue";
import { productoAdminComposable } from '~/composables/productoAdminComposable';

const { actualizarProducto, productoAdmin } = productoAdminComposable();

const props = defineProps({
    formularioActualizarProducto: Boolean
})
const mostrarFormularioActualizarProducto = ref(props.formularioActualizarProducto)

// RUTAS
const router = useRouter()

//USAR QUASAR
const quasar = useQuasar()

const imgProducto = ref(null)


// pickFiles es de Quasar
function pickFile() {
    imgProducto.value.pickFiles();
}

// DECLARACION DE VARIABLES
const datosProductoActualizar = reactive({
    categoriaProducto: productoAdmin.value.categoria,
    nombreProducto: productoAdmin.value.nombreProducto,
    descripcionProducto: productoAdmin.value.descripcionProducto,
    precioProducto: productoAdmin.value.precioProducto,
    cantidadProducto: productoAdmin.value.cantidadProducto,
    marcaProducto: productoAdmin.value.marcaProducto,
    especificacionesTecnicas: productoAdmin.value.especificacionesTecnicas,
    imagen: null,
})

// Subir imagen
function onFileChange(event) {
    const file = event.target.files[0];
    datosProducto.imagen = file
    console.log("file", file);

}
const opcionesCategorias = ['Portatil', 'Sobremesa', 'Componentes'];


// FUNCIONES

// FUNCION PARA ENVIAR EL FORMULARIO
const actualizarDatosProducto = async () => {
    console.log("actualizarProducto", datosProductoActualizar);
    console.log('producto', productoAdmin._id);

    const _id = productoAdmin.value._id


    try {
        if (!datosProductoActualizar) {
            console.error('Los datos del producto no están definidos');
            mostrarAlertaError('Los datos del producto no están definidos', quasar);
            return;
        }

        const formData = new FormData();
        formData.append("categoriaProducto", datosProductoActualizar.categoriaProducto);
        formData.append("nombreProducto", datosProductoActualizar.nombreProducto);
        formData.append("descripcionProducto", datosProductoActualizar.descripcionProducto);
        formData.append("precioProducto", datosProductoActualizar.precioProducto);
        formData.append("cantidadProducto", datosProductoActualizar.cantidadProducto);
        formData.append("marcaProducto", datosProductoActualizar.marcaProducto);
        formData.append("especificacionesTecnicas", datosProductoActualizar.especificacionesTecnicas.replace(/\\\\/g, '\n'));
        formData.append("img", datosProductoActualizar.imagen);

        console.log("formData", formData);

        for (var pair of formData.entries()) {
            console.log(pair[0], pair[1]);
        }


        const response = await actualizarProducto(formData, _id);

        if (response.data === 'Producto actualizado exitosamente') {
            mostrarAlertaExito('Producto actualizado exitosamente', quasar);
            router.push({ path: '/admin/vistaInicioAdmin' });
        } if (response.data === 'Ya existe un producto con el mismo identificador') {
            mostrarAlertaError('Ya existe un producto con el mismo identificador', quasar);
        } if (response.data === 'Error al construir el producto: La imagen debe ser JPG, PNG, JPEG o WEBP') {
            mostrarAlertaError('La imagen debe ser JPG, PNG, JPEG o WEBP', quasar);
        }
    } catch (error) {
        // Error de red o algo parecido
        console.error('Error al actualizar el producto:', error);
        mostrarAlertaError('Error al actualizar el producto intentelo más tarde', quasar);
    }
}



// CUANDO SE BORRA EL FORMULARIO, ELIMINO LOS VALORES DEL EMAIL Y LA CONTRASEÑA.
const borrar = () => {
    datosProductoActualizar.categoriaProducto = ''
    datosProductoActualizar.nombreProducto = ''
    datosProductoActualizar.descripcionProducto = ''
    datosProductoActualizar.precioProducto = ''
    datosProductoActualizar.cantidadProducto = ''
    datosProductoActualizar.marcaProducto = ''
    datosProductoActualizar.especificacionesTecnicas = ''
    datosProductoActualizar.imagen = null
};

// CUANDO SE HACE CLIC EN EL BOTÓN DE REGRESAR, REDIRIJO AL USUARIO A LA PÁGINA DE INICIO.
const regresar = () => {
    router.push({ path: '/admin/vistaInicioAdmin' })
};



</script>


<!--  ESTILOS -->
<style scoped>
.container {
    max-width: 80%;
    border: 5px solid #ccc;
    /* max-height: 100vh; */
    /* min-height: 100vh;  */
}


@media screen and (max-width: 600px) {

    /* Estilos específicos para dispositivos móviles con pantallas pequeñas */
    .container {
        max-width: 100%;
        margin: auto;
        /* max-height: 600px; */
        max-height: 1vh;
        /* overflow-y: auto; */
    }
}
</style>