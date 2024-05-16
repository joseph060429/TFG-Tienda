<template>
    <div class="container">
        <q-layout view="lHh lpr lFf" container style="height:10vh; width: 100%">
            <q-footer class="footer" style="background-color: #666666; height: 10vh; width: 100%">
                <q-toolbar dense class="q-pa-md" style="justify-content: space-between;">
                    <div style="display: flex; align-items: center;">
                        <span class="text-footer">Síguenos en:</span>
                        <div class="social-links" style="margin-left: 13px;">
                            <a href="#" class="social-link"><q-icon name="mdi-facebook" class="social-icon" /></a>
                            <a href="#" class="social-link"><q-icon name="mdi-instagram" class="social-icon" /></a>
                            <!-- funciona? -->
                        </div>
                    </div>
                    <img src="https://firebasestorage.googleapis.com/v0/b/proyecto-ionic-tienda.appspot.com/o/Logo-Imagenes%2FSoloLogo.png?alt=media&token=014b4175-3072-4a02-9d78-9ad45e7223a7"
                        alt="Logo" class="logo" />
                </q-toolbar>
            </q-footer>
        </q-layout>
    </div>
</template>

<!-- Scripts -->
<script setup>
</script>

<style lang="scss" scoped>


.container {
    width: 100%;
    margin: 0;
    padding: 0;
}

.footer {
    position: fixed;
    bottom: 0;
    display: flex;
    align-items: center;
    padding-bottom: 0;
    margin: 0;
    height: 10vh;
}

.footer-content {
    display: flex;
    justify-content: space-between;
    align-items: center;

}

.text-footer {
    color: white;
    font-size: 1.3em;
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