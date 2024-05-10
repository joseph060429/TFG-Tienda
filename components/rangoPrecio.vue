<template>
    <div class="q-pa-md">
        <q-item>
            <q-item-section avatar>
                <q-icon name="mdi-currency-eur" />
            </q-item-section>
            <q-item-section>
                <q-range v-model="model1" :min="0" :max="100000" label />
            </q-item-section>
        </q-item>

    </div>
</template>

<script setup>
import { ref } from 'vue'

const model1 = ref({ min: 0, max: 100000 })

</script>