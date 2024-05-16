<template>
    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
    <h2 class="titulo">Carrito de Compras</h2>
    <q-table class="tabla" flat bordered :rows="productos" row-key="index" virtual-scroll :virtual-scroll-item-size="48"
        :virtual-scroll-sticky-size-start="48" :pagination="{ rowsPerPage: 50 }" :rows-per-page-options="[50]">

        <template v-slot:header="props">
            <q-tr :props="props">
                <q-th key="nombreProducto">Producto</q-th>
                <q-th key="marcaProducto">Marca</q-th>
                <q-th key="precioProducto">Precio</q-th>
                <q-th key="imagendProducto">Imágen</q-th>
                <q-th key="cantidadProducto">Cantidad</q-th>
                <q-th key="acciones">Aciones</q-th>
            </q-tr>
        </template>
        <template v-slot:body="props">
            <q-tr :props="props">
                <q-td>{{ props.row.nombreProducto }}</q-td>
                <q-td>{{ props.row.marcaProducto }}</q-td>
                <q-td>{{ props.row.precioProducto }}</q-td>
                <q-td><q-img :src="props.row.imagenProducto" width="10vh"></q-img></q-td>
                <q-td>
                    <q-input v-model="props.row.cantidadProducto" type="number" min="1" dense
                        style="width: 50px; height: 20px; font-size: 12px; text-align: right;"
                        lazy-rules :rules="[
                                    val => val !== null && val !== undefined || 'Por favor, introduce algo',
                                    val => !isNaN(val) || 'La cantidad debe ser un número',
                                    val => parseInt(val) >= 1 || 'La cantidad debe ser mayor o igual a 1'
                                ]"></q-input>
                </q-td>

                <q-td class="text-center">
                    <q-btn @click="eliminarProducto()" class="boton-borrar">
                        <q-icon name="mdi-delete" />
                    </q-btn>
                </q-td>
            </q-tr>
        </template>
    </q-table>
</template>
<script setup>
import { ref, onMounted } from 'vue';

const required = (val) => !!val || 'Campo requerido';
const minAmount = (val, row) => {
    if (!val || val < 1) {
        return 'La cantidad debe ser al menos 1';
    }
    if (val > row.cantidadProductoStore) {
        return 'La cantidad excede el stock disponible';
    }
    return true;
};







definePageMeta({
    role: ['ROLE_USER']
});


onMounted(() => {
    obtenerProductosDelCarrito();

    console.log();

});

// RUTAS
const router = useRouter()



// Referencia a los productos almacenados en el localStorage
const productos = ref([]);


const obtenerProductosDelCarrito = () => {
    const detalleProductoCarrito = localStorage.getItem('detalleProductoCarrito') || [];
    console.log("productos");
    productos.value = JSON.parse(detalleProductoCarrito);
    console.log('productos del carrito', detalleProductoCarrito);
};

const eliminarProducto = (index) => {
    productos.value.splice(index, 1); // Elimina el producto seleccionado de la lista
    if (productos.value.length > 0) {
        // Actualizo el localStorage si hay productos otros productos
        localStorage.setItem('detalleProductoCarrito', JSON.stringify(productos.value));
    } else {
        // Elimino todo si no hay productos
        localStorage.removeItem('detalleProductoCarrito');
    }
};

const regresar = () => {
    router.push({ path: '/usuario/vistaInicioUsuario' })
};


const verificarCantidad = (producto) => {
    if (producto.cantidadProducto > producto.cantidadProductoStore) {
        mostrarAlertaError('La cantidad seleccionada supera el stock disponible', quasar);
    }
};

const procederAlPago = () => {

};

</script>

<style scoped>
.tabla {
    height: 60vh;
    width: 90%;
    background-color: #F5F5F5;
    font-family: Arial, sans-serif;
    border: 3px solid black;
    margin-left: 5%;
    margin-right: 5%;
}

@media (max-width: 600px) {
    .tabla {
        width: 90%;
        margin: 0 auto;
        height: 60vh;
        font-size: 0.8em;
    }
}

.tabla th {
    font-weight: bold;
}

.tabla td {
    text-align: center;
}

.titulo {
    font-size: 2em;
    text-align: center;
    margin: 1em;
    color: #546e7a;
    font-weight: bold;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.boton-borrar {
    background-color: #FF4500;
    border: 1px solid;

}

.boton-borrar:hover {
    background-color: rgba(255, 69, 0, 0.8);

}

.boton-borrar {
    font-size: 0.8em;
    padding: 5px 10px;
    margin: 0 5px;
}

@media only screen and (max-width: 600px) {
    .boton-borrar {
        font-size: 14px;
        padding: 3px 6px;
    }
}
</style>