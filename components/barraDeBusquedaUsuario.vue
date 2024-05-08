<template>
    <q-input class="input-buscar" filled v-model="especificacion_del_producto" placeholder="Especificación del producto"
        @keyup.enter="buscarProductoEspecificacion">
        <template v-slot:prepend>
            <q-icon name="mdi-magnify" class="icono-buscar" />
        </template>
        <template v-slot:append>
            <q-select v-model="tipo_busqueda" :options="opcionesBusqueda" dense outlined />
        </template>
    </q-input>
</template>



<script setup>
import { ref, watch } from 'vue';
import { productoComposable } from '~/composables/productoComposable';


const especificacion_del_producto = ref('');
console.log("especificacion del producto", especificacion_del_producto);

const { buscarProductoPorEspecificacion, productos, buscarProductosPorCampos } = productoComposable();

const buscarProductoEspecificacion = async () => {
    console.log(especificacion_del_producto.value);
    try {
        const response = await buscarProductoPorEspecificacion(especificacion_del_producto.value);
        productos.value = response.data;
        console.log("se filtra", response.data);

    } catch (error) {
        console.error(error);
    }
}


// const opcionesBusqueda = [
//   { label: 'Categoria', value: 'nombre' },
//   { label: 'Nombre', value: 'marca' },
//   { label: 'Descripción', value: 'descripcion' }
//   { label: 'Marca', value: 'marca' },
// ];

// // Variables reactivas para cada campo de búsqueda
// const categoria = ref('');
// const nombre = ref('');
// const descripcion = ref('')
// const marca = ref('');


// const buscarProductosPorCamposImportantes = async () => {
//     try {
//         let response;
//         switch (tipo_busqueda.value) {
//             case 'nombre':
//                 // Obtener todos los nombres de productos disponibles
//                 const nombresProductos = productos.value.map(producto => producto.nombre);
//                 // Actualizar las opciones del q-select con los nombres de productos
//                 opcionesBusqueda = nombresProductos.map(nombre => ({ label: nombre, value: nombre }));
//                 break;
//             case 'marca':
//                 response = await buscarProductosPorCampos(null, null, null, especificacion_del_producto.value);
//                 break;
//             case 'precio':
//                 // Ajusta según cómo manejas el campo de precio
//                 // Ejemplo: response = await buscarProductosPorCampos(null, null, especificacion_del_producto.value, null);
//                 break;
//             case 'descripcion':
//                 response = await buscarProductosPorCampos(null, especificacion_del_producto.value, null, null);
//                 break;
//             default:
//                 console.error('Opción de búsqueda no válida');
//                 return;
//         }
//         console.log(response.data);
//     } catch (error) {
//         console.error(error);
//     }
// }

// Watcher para la variable especificacion_del_producto
watch(especificacion_del_producto, (newValue, oldValue) => {
    buscarProductoEspecificacion();
});













</script>


<style lang="scss" scoped>
.barra-busqueda {
    width: 100%;
    height: 8vh;
    // display: flex;
    text-align: center;
    margin: auto;
    // background-color: #333;
}

.input-buscar {
    width: 100%;

    // height: 6vh;
}

.btn-buscar {
    width: 1%;
    height: 5.5vh;
}
</style>
