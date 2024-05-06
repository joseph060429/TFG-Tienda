<template>
    <!-- Boton de volver atras -->
    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
    <div style="overflow: auto;">
        <!-- <div class="container"> -->
        <div class="q-pa-xs" style="width: 90%; max-height: 100vh; margin: auto">
            <div class="d-flex justify-center align-center" style="max-width: 60%; margin: auto;">
                <h1 class="text-h4 q-mb-md text-center q-mt-lg"
                    style="color: #333333; font-weight: bold; text-transform: uppercase; letter-spacing: 2px;">
                    Crear producto
                </h1>
                <q-form @submit.prevent="crearProductos" @reset="borrar" class="q-gutter-xs"
                    style="max-width: 100%; max-height: 60vh; margin: auto;">

                    <!-- Campo categoria producto -->
                    <q-select v-model="datosProducto.categoriaProducto" :options="opcionesCategorias"
                        label="Categoría del producto *" :rules="[v => !!v || 'Debes seleccionar una categoría']" />


                    <!-- Campo nombreProducto -->
                    <q-input filled v-model="datosProducto.nombreProducto" label="Nombre del producto *" lazy-rules
                        :rules="[
                            val => val && val.length > 0 || 'Por favor, introduce algo',
                            val => /^.{2,70}$/.test(val) || 'El nombre del producto debe tener entre 2 y 70 caracteres',
                            val => /^\S.*\S$/.test(val) || 'El nombre del producto no puede empezar ni terminar con espacios en blanco',
                        ]">
                        <template v-slot:prepend>
                            <q-icon name="mdi-label" />
                        </template>
                    </q-input>



                    <!-- Campo descripcionProducto -->
                    <q-input filled v-model="datosProducto.descripcionProducto" label="Descripción del producto *" type="textarea"
                        lazy-rules :rules="[
                            val => val && val.length > 0 || 'Por favor, introduce algo',
                            // val => /^.{2}$/.test(val) || 'La descripción del producto debe tener entre 2 y 70 caracteres',
                            val => /^\S.*\S$/.test(val) || 'La descripción del producto no puede empezar ni terminar con espacios en blanco',
                        ]">
                        <template v-slot:prepend>
                            <q-icon name="mdi-comment-text-outline" />
                        </template>
                    </q-input>

                    <!-- Campo precioProducto -->
                    <q-input filled v-model="datosProducto.precioProducto" label="Precio del producto *" lazy-rules
                        :rules="[
                            val => val && val.length > 0 || 'Por favor, introduce algo',
                            val => !isNaN(val) || 'El precio debe ser un número',
                            val => parseFloat(val) > 0 || 'El precio debe ser mayor que cero'
                        ]">
                        <template v-slot:prepend>
                            <q-icon name="mdi-currency-eur" />
                        </template>
                    </q-input>

                    <!-- Campo cantidadProducto -->
                    <q-input filled v-model="datosProducto.cantidadProducto" label="Cantidad del producto *" lazy-rules
                        :rules="[
                            val => val && val.length > 0 || 'Por favor, introduce algo',
                            val => !isNaN(val) || 'La cantidad debe ser un número',
                            val => parseInt(val) >= 0 || 'La cantidad debe ser mayor o igual que cero'
                        ]">
                        <template v-slot:prepend>
                            <q-icon name="mdi-numeric" />
                        </template>
                    </q-input>



                    <!-- Campo marcaProducto -->
                    <q-input filled v-model="datosProducto.marcaProducto" label="Marca del producto *" lazy-rules
                        :rules="[
                            val => val && val.length > 0 || 'Por favor, introduce algo',
                            val => /^.{2,50}$/.test(val) || 'La marca del producto debe tener entre 2 y 50 caracteres',
                            val => /^\S.*\S$/.test(val) || 'La marca del producto no puede empezar ni terminar con espacios en blanco',

                        ]">
                        <template v-slot:prepend>
                            <q-icon name="mdi-watermark" />
                        </template>
                    </q-input>


                    <!-- Campo especificacionesTecnicas -->
                    <q-input v-model="datosProducto.especificacionesTecnicas" filled
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
                    <q-file v-model="datosProducto.imagen" @update:model-value="onFileChange" filled
                        label="Imagen del Producto *" :rules="[val => !!val || 'Por favor, selecciona una imagen']">
                        <template v-slot:append>
                            <q-icon name="mdi-paperclip" />
                            <q-icon name="mdi-close" @click.stop.prevent="datosProducto.imagen = null" class="cursor-pointer" />

                        </template>
                    </q-file>

                    <!-- Botones de enviar y reiniciar -->
                    <div class="q-mt-lg d-flex justify-around">
                        <!-- Boton de enviar -->
                        <q-btn label="Crear Producto" type="submit" class="full-width enviar-button col-xs-12 col-sm-6">
                            <q-icon name="mdi-plus-circle" class="q-ml-md inline"></q-icon> <!-- Icono para el botón -->
                        </q-btn>

                        <!-- Boton de reiniciar -->
                        <q-btn label="Reiniciar" type="reset" flat
                            class="full-width q-ml-sm custom-button-reiniciar col-xs-12 col-sm-6">
                            <q-icon name="mdi-refresh" class="q-ml-md inline"></q-icon> <!-- Icono para el botón -->
                        </q-btn>
                    </div>

                </q-form>
            </div>
        </div>
    </div>
</template>

<script setup>

// IMPORTACIONES
import { useRouter } from 'vue-router'
import { mostrarAlertaExito, mostrarAlertaError } from '~/utils/alertas';
import { reactive } from "vue";
import { productoAdminComposable } from '~/composables/productoAdminComposable';

const { crearProducto } = productoAdminComposable();

// Acceso de la pagina
definePageMeta({
    role: ['ADMIN']
})


// RUTAS
const router = useRouter()

//USAR QUASAR
const quasar = useQuasar()

// DECLARACION DE VARIABLES
const datosProducto = reactive({
    categoriaProducto: '',
    nombreProducto: '',
    descripcionProducto: '',
    precioProducto: '',
    cantidadProducto: '',
    marcaProducto: '',
    especificacionesTecnicas: '',
    imagen: null
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
const crearProductos = async () => {
    console.log('datos productos', datosProducto);
    try {
        if (!datosProducto) {
            console.error('Los datos del producto no están definidos');
            mostrarAlertaError('Los datos del producto no están definidos', quasar);
            return;
        }

        const formData = new FormData();
        formData.append("categoriaProducto", datosProducto.categoriaProducto);
        formData.append("nombreProducto", datosProducto.nombreProducto);
        formData.append("descripcionProducto", datosProducto.descripcionProducto);
        formData.append("precioProducto", datosProducto.precioProducto);
        formData.append("cantidadProducto", datosProducto.cantidadProducto);
        formData.append("marcaProducto", datosProducto.marcaProducto);
        formData.append("especificacionesTecnicas", datosProducto.especificacionesTecnicas.replace(/\\\\/g, '\n'));
        formData.append("img", datosProducto.imagen);

        console.log("formData", formData);

        for (var pair of formData.entries()) {
            console.log(pair[0], pair[1]);
        }

        const response = await crearProducto(formData);
        
        if (response.data === 'Producto creado exitosamente') {
            mostrarAlertaExito('Producto creado exitosamente', quasar);
            borrar();
        } if (response.data === 'Ya existe un producto con el mismo identificador') {
            mostrarAlertaError('Ya existe un producto con el mismo identificador', quasar);
        }
    } catch (error) {
        // Error de red o algo parecido
        console.error('Error al crear el producto:', error);
        mostrarAlertaError('Error al crear el producto intentelo más tarde', quasar);
    }
}



// CUANDO SE BORRA EL FORMULARIO, ELIMINO LOS VALORES DEL EMAIL Y LA CONTRASEÑA.
const borrar = () => {
    datosProducto.categoriaProducto = ''
    datosProducto.nombreProducto = ''
    datosProducto.descripcionProducto = ''
    datosProducto.precioProducto = ''
    datosProducto.cantidadProducto = ''
    datosProducto.marcaProducto = ''
    datosProducto.especificacionesTecnicas = ''
    datosProducto.imagen = null
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
    /* padding: 2vh; */
    border: 5px solid #ccc;
    /* display: flex; */
    /* flex-direction: column; */
    max-height: 100vh;
    /* Máximo 80% del alto de la pantalla */
    overflow: hidden;
    /* Oculta el desplazamiento hacia abajo en el contenedor principal */
}


@media screen and (min-width: 600px) {

    /* Estilos específicos para pantallas con ancho mínimo de 600px */
    .container {
        max-width: 70%;
        /* Máximo 80% del ancho de la pantalla */
        margin: auto;
        /* Centrar el contenedor horizontalmente */
    }
}
</style>