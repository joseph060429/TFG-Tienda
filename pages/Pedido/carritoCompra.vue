<template>

    <div>
        <h2>Carrito de Compras</h2>
        <q-page-container v-if="productos.length === 0">
            <p>El carrito está vacío.</p>
        </q-page-container>
        <q-page-container v-else>
            <div v-for="(producto, index) in productos" :key="index" class="q-pa-md">
                <q-card>
                    <q-card-section>
                        <img :src="producto.imagen" alt="Producto">
                        <p>{{ producto.nombre }}</p>
                        <p>{{ producto.marca }}</p>
                        <p>{{ producto.precio }}</p>
                    </q-card-section>
                    <q-card-actions>
                        <q-btn color="negative" @click="eliminarProducto(index)">Eliminar</q-btn>
                    </q-card-actions>
                </q-card>
            </div>
            <q-btn @click="procederAlPago" color="primary" class="q-mt-md">Proceder al Pago</q-btn>
        </q-page-container>
    </div>


</template>

<script setup>
import { ref, onMounted } from 'vue';


definePageMeta({
  role: ['ROLE_USER']
});




const productos = ref([]);

const obtenerProductosDelCarrito = () => {
    const detalleProductoCarrito = localStorage.getItem('detalleProductoCarrito') || [];
    productos.value = detalleProductoCarrito;
};

const eliminarProducto = (index) => {
    productos.value.splice(index, 1);
    localStorage.set('detalleProductoCarrito', productos.value);
};

const procederAlPago = () => {

};

onMounted(() => {
    obtenerProductosDelCarrito();
});
</script>

<style scoped>
.q-pa-md {
    padding: 1rem;
}

.q-mt-md {
    margin-top: 1rem;
}
</style>