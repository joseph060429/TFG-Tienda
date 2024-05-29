<template>
    <div class="payment-success-container">
    <div class="message-box">
        <h1>Pago Completado Exitosamente</h1>
        <p>¡Gracias por tu compra! Nos complace informarte que tu pago ha sido procesado con éxito. Tu apoyo es muy
            importante para nosotros y nos esforzamos por brindarte el mejor servicio posible.</p>
        <p>Nos alegra que hayas elegido nuestra tienda para realizar tu compra. Estamos trabajando diligentemente para
            procesar y preparar tu pedido con la mayor rapidez y cuidado.</p>
        <p>Si tienes alguna pregunta o necesitas asistencia adicional, no dudes en ponerte en contacto con nuestro
            equipo de soporte al cliente. Estamos aquí para ayudarte y asegurarnos de que tengas la mejor experiencia de
            compra posible.</p>
        <q-btn color="primary" label="Volver al Inicio" @click="volverInicio" class="q-mt-md" />
    </div>
</div>
</template>

<script setup>
import { useRouter } from 'vue-router'


definePageMeta({
    role: ['ROLE_USER']
});

const router = useRouter();
const volverInicio = () => {
    router.push({ path: '/usuario/vistaInicioUsuario' })
};

</script>

<style lang="scss" scoped>
.payment-success-container {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 80vh;
    width: 100%;
    background-color: #f5f5f5;
    padding: 20px;
}

.message-box {
    background-color: #ffffff;
    border-radius: 10px;
    box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
    margin-top: 0.5%;
    text-align: center;
    width: 95%;
}

h1 {
    color: #4CAF50;
    margin-bottom: 1em;
    font-size: 4em;

}

p {
    color: #333333;
    font-size: 16px;
    margin-bottom: 10px;
}

.q-btn {
    width: 100%;
    max-width: 200px;
}

@media (max-width: 600px) {
    .message-box {
        width: 100%;
        padding: 10px;
    }

    h1 {
        font-size: 1.5em;
    }

    p {
        font-size: 14px;
    }
}



</style>