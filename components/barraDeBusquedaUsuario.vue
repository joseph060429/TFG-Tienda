<template>
    <div class="contenedor-input-select">
        <q-input class="input-buscar" filled v-model="especificacion_del_producto"
            placeholder="Especificación del producto" @keyup.enter="buscarProductoEspecificacion">
            <template v-slot:prepend>
                <q-icon name="mdi-magnify" class="icono-buscar" />
            </template>
        </q-input>
        <q-select v-model="tipo_busqueda" :options="opcionesBusqueda" dense outlined
            @update:model-value="actualizarFiltros">
            <template v-slot:append>
                <span style="font-size: 0.8em;">Selecciona una opción</span><q-icon name="mdi-format-list-checks"
                    size="sm" />
            </template>
        </q-select>


        <q-select v-if="tipo_busqueda === 'categoria'" v-model="categoriaSeleccionada" :options="categoriasUnicas" dense
            outlined @update:model-value="actualizarCategoria" />

        <!-- q-select para 'marca' -->
        <template v-if="tipo_busqueda === 'marcas'">
            <q-select v-model="marcaProductoSeleccionado" :options="marcasUnicas" dense outlined
                @update:model-value="actualizarMarcas" />
        </template>
    </div>
    <div class="boton-precio">
        <busqueda-precio v-model="mostrarRangoPrecio" />
        <q-btn @click="mostrarPrecios" class="boton-precio" label="Buscar por precios">
            <q-icon name="mdi-cash-multiple" /> <!-- Icono de precios -->
        </q-btn>
    </div>
</template>


<script setup>
import { ref, watch, onUnmounted, onBeforeMount, onMounted } from 'vue';
import { productoComposable } from '~/composables/productoComposable';
// import {actualizarFiltros, actualizarMarcas, actualizarCategoria} from '~/utils/filtros'


//USAR QUASAR
const quasar = useQuasar()
const { buscarProductoPorEspecificacion, productos } = productoComposable();

const especificacion_del_producto = ref('');
const buscarProductoEspecificacion = async () => {
    console.log(especificacion_del_producto.value);
    try {
        const response = await buscarProductoPorEspecificacion(especificacion_del_producto.value);
        console.log("response de especificacion", response);

        if (response.data.length === 0) {
            mostrarAlertaError('No hay productos que coincidan con la busqueda', quasar)
        } else {
            productos.value = response.data;
            console.log("se filtra", response.data);
        }

    } catch (error) {
        console.error(error);
    }
}

// Watcher para la variable especificacion_del_producto
watch(especificacion_del_producto, (newValue, oldValue) => {
    buscarProductoEspecificacion();
});



const marcasUnicas = ref(null);
const categoriasUnicas = ref(null);

// Array para copiar los productos
const copiaProductos = [...productos.value];



const tipo_busqueda = ref('');

let categoriasUnicasIniciales = [];

onMounted(() => {
    console.log('ONMOUNTEDDD => ', categoriaSeleccionada.value);
    categoriaSeleccionada.value = null;
    console.log('ONMOUNTEDDD 222 => ', categoriaSeleccionada.value);
});


// const cargarCategoriasUnicas = () => {
//     categoriasUnicas.value = [...new Set(copiaProductos.map(producto => producto.categoriaProducto))];
//     console.log('Categorías únicas cargadas: ', categoriasUnicas.value);
// };

onBeforeMount(() => {
    categoriasUnicasIniciales = [...new Set(copiaProductos.map(producto => producto.categoriaProducto))];
    categoriasUnicas.value = categoriasUnicasIniciales;
    console.log('categorias unicas iniciales onBeforeMount==> ', categoriasUnicasIniciales);




    marcasUnicas.value = [...new Set(productos.value.map(producto => producto.marcaProducto))];



    // cargarCategoriasUnicas()
   
})

onUnmounted(() => {
    console.log('Copia de productos en onUNMOUNTED ==> ', copiaProductos);
    categoriasUnicasIniciales = [...new Set(copiaProductos.map(producto => producto.categoriaProducto))];
    console.log("categorias unicas iniciales ONUNMOUNTED", categoriasUnicasIniciales);

    // cargarCategoriasUnicas();
});

const opcionesBusqueda = [
    { label: 'Categoría', value: 'categoria' },
    { label: 'Marca', value: 'marcas' },
];

const marcaProductoSeleccionado = ref(null)

const categoriaSeleccionada = ref(null);

watch(categoriaSeleccionada, (x) => {
    console.log('Categoria seleccionada WATCH ==> ', x);
})

const actualizarFiltros = (e) => {
    tipo_busqueda.value = e.value;
}


// FUNCION PARA BUSCAR POR MARCAS
const actualizarMarcas = (e) => {
    console.log("marca seleccionada", marcaProductoSeleccionado.value);
    console.log("copiaProductos", copiaProductos);
    const filtroPorMarca = copiaProductos.filter(producto => producto.marcaProducto === marcaProductoSeleccionado.value)
    console.log('response desde marca', filtroPorMarca);
    console.log("filtro por marca", filtroPorMarca);
    productos.value = filtroPorMarca
    // resetearTipoBusqueda();
}

// FUNCION PARA BUSCAR POR CATEGORIAS
const actualizarCategoria = (e) => {
    console.log('EJECUTANDO');
    console.log("categoria seleccionada 😁😁😁😁", categoriaSeleccionada.value);
    console.log("copiaProductos", copiaProductos);
    const filtroPorCategoria = copiaProductos.filter(producto => producto.categoriaProducto === categoriaSeleccionada.value)
    console.log("Filtro por categoria 😁😁😁😁", filtroPorCategoria);
    // console.log('response desde categoria', filtroPorCategoria);
    productos.value = filtroPorCategoria
}



const mostrarRangoPrecio = ref(false);

const mostrarPrecios = () => {
    // Abro el formulario si no esta abierto
    if (!mostrarRangoPrecio.value) {
        mostrarRangoPrecio.value = true;
        console.log('boton mosstrar precios presionado');
    }
};
</script>


<style lang="scss" scoped>
.input-buscar {
    width: 100%;
    flex: 1;
    max-height: 100%;
}

.q-select {
    width: 100%;
    max-height: 5vh;
    height: auto;
    background-color: #d2e8e6;
}

.boton-precio {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    color: black;

}

.boton-precio .q-btn {
    background-color: #d2e8e6; /* Color de fondo verde */
    transition: background-color 0.3s, color 0.3s; /* Transición suave */
}
@media (max-width: 600px) {
    .boton-precio .q-btn {
        padding: 8px 16px; /* Espaciado interno más pequeño */
        font-size: 12px; /* Tamaño de fuente más pequeño */
    }
}
</style>