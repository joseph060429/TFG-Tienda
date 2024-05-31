<template>
    <div class="payment-cancelled-container">
        <div class="message-box">
            <h1>Pago Cancelado</h1>
            <p>Lamentamos informarte que tu pago ha sido cancelado. Entendemos que puede haber muchas razones para esta
                decisión y estamos aquí para ayudarte con cualquier inquietud que puedas tener.</p>
            <p>Si cambiaste de opinión o necesitas más información sobre nuestros productos y servicios, no dudes en
                ponerte
                en contacto con nuestro equipo de soporte al cliente. Estamos disponibles para brindarte la asistencia
                que
                necesites.</p>
            <p>Esperamos que reconsideres y vuelvas a nuestra tienda en el futuro. Nos esforzamos por ofrecerte la mejor
                experiencia de compra posible.</p>
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
.payment-cancelled-container {
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