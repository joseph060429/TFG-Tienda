<template>
    <q-input class="input-buscar" filled v-model="especificacion_del_producto" placeholder="Especificación del producto"
        @keyup.enter="buscarProductoEspecificacion">
        <template v-slot:prepend>
            <q-icon name="mdi-magnify" class="icono-buscar" />
        </template>
        <q-select v-model="tipo_busqueda" :options="opcionesBusqueda" dense outlined>
            <template v-slot:append>
                <q-icon name="mdi-format-list-checks" size="sm" />
            </template>
        </q-select>


        <q-select v-if="tipo_busqueda === 'nombre'" :options="filtrado" dense outlined>
        </q-select>

        <q-select v-else-if="tipo_busqueda === 'categoria'" :options="filtrado" dense outlined />


        <q-select v-else="tipo_busqueda === 'marca'" :options="filtrado" dense outlined />






    </q-input>

    <q-select v-if="tipo_busqueda === 'nombre'" :options="filtrado" dense outlined>
        <template v-slot:append>
            <q-icon name="mdi-label" size="sm" />
        </template>
    </q-select>
</template>


<script setup>
import { ref, watch } from 'vue';
import { productoComposable } from '~/composables/productoComposable';


const especificacion_del_producto = ref('');
// console.log("especificacion del producto", especificacion_del_producto);

const { buscarProductoPorEspecificacion, productos } = productoComposable();

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

// Watcher para la variable especificacion_del_producto
watch(especificacion_del_producto, (newValue, oldValue) => {
    buscarProductoEspecificacion();
});

const tipo_busqueda = ref('');

const opcionesBusqueda = [
    { label: 'Selecciona una opción', value: '' },
    { label: 'Nombre', value: 'nombre' },
    { label: 'Categoría', value: 'categoria' },
    { label: 'Marca', value: 'marca' },
];

const categoriaSeleccionada = ref('');


const filtrado = computed(() => {
    console.log('Esto es tipoBusqueda ==> ', tipo_busqueda.value);
    switch (tipo_busqueda.value.value) {
        case 'nombre':
            console.log("tipo busqueda", tipo_busqueda.value);
            return productos.value.map(producto => producto.nombreProducto);
        case 'categoria':
            // const categoriasUnicas = [...new Set(productos.value.map(producto => producto.categoriaProducto))];
            // return categoriasUnicas;
            const categoriasUnicas = [...new Set(productos.value.map(producto => producto.categoriaProducto))];
            if (categoriaSeleccionada.value !== '') {
                console.log("categoria seleccionada", categoriaSeleccionada.value);
                return productos.value.filter(producto => producto.categoriaProducto === categoriaSeleccionada.value);
            } else {
                return categoriasUnicas;
            }


        case 'marca':
            const marcasUnicas = [...new Set(productos.value.map(producto => producto.marcaProducto))];
            return marcasUnicas;
        default:
            return [];
    }
});

watch(tipo_busqueda, (x) => {
    console.log("x ==>", x);
});



















// const nombresFiltrados = computed(() => {
//     if (tipo_busqueda.value === 'nombre') {
//         return productos.value.map(producto => producto.categoriaProducto);
//     }
// });

// // FUNCION PARA FILTRAR POR CATEGORIA
// const filtrarPorCategoria = computed(() => {
//     if (tipo_busqueda.value === 'categoria') {
//         const categoriasUnicas = [...new Set(productos.value.map(producto => producto.categoriaProducto))];
//         return categoriasUnicas;
//     }
// });


// const filtrarPorMarca = computed(() => {
//     if (tipo_busqueda.value === 'marca') {
//         const marcasUnicas = [...new Set(productos.value.map(producto => producto.marcaProducto))];
//         return marcasUnicas;
//     }
// });















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
