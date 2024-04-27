<template>
    <div class="q-pa-md">
        <q-footer class="footer" style="height: 10vh;">
            <div class="footer-content q-gutter-md">
                <q-toolbar dense class="q-pa-md" style="justify-content: space-between;">
                    <div style="display: flex; align-items: center;">
                        <span class="text-white">Síguenos en:</span>
                        <div class="social-links" style="margin-left: 13px;">
                            <a href="#" class="social-link"><q-icon name="mdi-facebook" class="social-icon" /></a>
                            <a href="#" class="social-link"><q-icon name="mdi-instagram" class="social-icon" /></a>
                        </div>
                    </div>
                    <img src="https://firebasestorage.googleapis.com/v0/b/proyecto-ionic-tienda.appspot.com/o/Logo-Imagenes%2FSoloLogo.png?alt=media&token=014b4175-3072-4a02-9d78-9ad45e7223a7"
                        alt="Logo" class="logo" />
                </q-toolbar>
            </div>
        </q-footer>
    </div>
</template>

<!-- Scripts -->
<script setup>
</script>

<style scoped>
.footer {
    position: fixed;
    bottom: 0;
    width: calc(100% - 20px);
    /* Reduce el ancho en 20px para dejar espacio a ambos lados */
    margin: 0 10px;
    /* Agrega un margen de 10px a ambos lados */
    background-color: #666666;

}


.footer-content {
    display: flex;
    justify-content: space-between;
    align-items: center;

}

.text-white {
    color: white;
}

.logo {
    height: 7vh;
}

.social-links {
    display: flex;
    align-items: center;
}

.social-icon {
    font-size: 24px;
    margin-right: 10px;
}

.facebook {
    color: #3b5998;
}

.instagram {
    background-color: #e4405f;
}

/* Estilos adicionales para pantallas más pequeñas */
@media (max-width: 600px) {
    .social-icon {
        font-size: 20px;
        /* Reduce el tamaño de los iconos en pantallas pequeñas */
    }
}
</style>