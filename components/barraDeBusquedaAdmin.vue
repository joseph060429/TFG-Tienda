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
import { productoAdminComposable } from '~/composables/productoAdminComposable';


const especificacion_del_producto = ref('');
console.log("especificacion del producto", especificacion_del_producto);

const { buscarProductoPorEspecificacion, productos, productoAdmin } = productoAdminComposable();

const buscarProductoEspecificacion = async () => {
    console.log(especificacion_del_producto.value);
    try {
        const response = await buscarProductoPorEspecificacion(especificacion_del_producto.value);
        productos.value = response.data;
        console.log("producto admin" , productoAdmin.value);
        console.log("se filtra", response.data);

    } catch (error) {
        console.error(error);
    }
}

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
