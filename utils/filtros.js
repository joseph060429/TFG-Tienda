// export const copiaProductos = [...productos.value];

export const actualizarFiltros = (e) => {
    tipo_busqueda.value = e.value;
}


// FUNCION PARA BUSCAR POR MARCAS
export const actualizarMarcas = (e) => {
    const copiaProductos = [...productos.value];
    console.log("marca seleccionada", marcaProductoSeleccionado.value);
    console.log("copiaProductos", copiaProductos);
    const filtroPorMarca = copiaProductos.filter(producto => producto.marcaProducto === marcaProductoSeleccionado.value)
    console.log('response desde marca', filtroPorMarca);
    productos.value = filtroPorMarca
}

// FUNCION PARA BUSCAR POR CATEGORIAS
export const actualizarCategoria = (e) => {
    const copiaProductos = [...productos.value];
    console.log("categoria seleccionada", categoriaSeleccionada.value);
    console.log("copiaProductos", copiaProductos);
    const filtroPorCategoria = copiaProductos.filter(producto => producto.categoriaProducto === categoriaSeleccionada.value)
    console.log('response desde categoria', filtroPorCategoria);
    productos.value = filtroPorCategoria
}