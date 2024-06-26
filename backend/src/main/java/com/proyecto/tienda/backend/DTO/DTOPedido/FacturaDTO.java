package com.proyecto.tienda.backend.DTO.DTOPedido;

import com.proyecto.tienda.backend.models.PedidosModelo;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.awt.Color;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDTO {

    private UsuarioModelo usuarioModelo;
    private PedidosModelo pedidosModelo;
    private List<ProductoPedidoDTO> productosPedidos;
    private double total;

    // Libreria Itext

    public byte[] generarFacturaPDF(FacturaDTO facturaDTO, String rutaLogo) {

        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();

        try {

            // Cro un flujo de bytes para almacenar el PDF en memoria
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, pdfStream);
            document.open();

            // Configuración de estilos
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.red);
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.black);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            // Agrego el logo de mi empresa a mi pdf
            Image imagenLogo = Image.getInstance(rutaLogo);
            imagenLogo.setAlignment(Element.ALIGN_CENTER);
            imagenLogo.scaleToFit(200, 200); // Aquí puedes ajustar el ancho y alto deseado
            document.add(imagenLogo);

            // Encabezado
            Paragraph titulo = new Paragraph("Factura de compra", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            // Numero del pedido
            PedidosModelo pedido = facturaDTO.getPedidosModelo();
            Paragraph id_pedido = new Paragraph("Número del pedido: " + pedido.getNumPedido(), boldFont);
            document.add(id_pedido);

            // Datos del cliente
            UsuarioModelo usuario = facturaDTO.getUsuarioModelo();
            Paragraph infoCliente = new Paragraph("Cliente: " + usuario.getNombre() + " " + usuario.getApellido(),
                    boldFont);
            document.add(infoCliente);

            // Agrego un párrafo vacío para dejar espacio
            document.add(new Paragraph(" "));

            // Detalles de los productos
            // El numero 3 indica el numero de columnas de mi tabla
            PdfPTable tabla = new PdfPTable(3);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new int[] { 3, 1, 1 });

            PdfPCell cell = new PdfPCell(new Phrase("Producto", boldFont));
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            tabla.addCell(cell);

            cell = new PdfPCell(new Phrase("Precio", boldFont));
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            tabla.addCell(cell);

            cell = new PdfPCell(new Phrase("Cantidad", boldFont));
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            tabla.addCell(cell);

            double total = 0.0;
            List<ProductoPedidoDTO> productos = facturaDTO.getProductosPedidos();
            for (ProductoPedidoDTO producto : productos) {
                tabla.addCell(new Phrase(producto.getNombre(), normalFont));
                tabla.addCell(new Phrase(String.valueOf(producto.getPrecioProducto()) + "€", normalFont));
                tabla.addCell(new Phrase(String.valueOf(producto.getCantidadPedida()), normalFont));

                // Calcular el total sumando el precio de cada producto
                total += producto.getPrecioProducto() * producto.getCantidadPedida();
            }

            // Total
            PdfPCell totalCell = new PdfPCell(new Phrase("Total: " + total + "€", fuenteSubtitulo));
            totalCell.setColspan(3);
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tabla.addCell(totalCell);

            document.add(tabla);

            // Tabla para la dirección de envío
            PdfPTable tablaEnvio = new PdfPTable(1);
            tablaEnvio.setWidthPercentage(100);

            PdfPCell celdaEnvio = new PdfPCell(new Phrase("Dirección de envío:", boldFont));
            celdaEnvio.setBackgroundColor(Color.LIGHT_GRAY);
            celdaEnvio.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaEnvio.addCell(celdaEnvio);

            celdaEnvio = new PdfPCell(new Phrase(pedido.getDireccionCompletaEnvio(), normalFont));
            celdaEnvio.setBorderWidth(0);
            celdaEnvio.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaEnvio.addCell(celdaEnvio);

            // Tabla para la dirección de facturación
            PdfPTable tablaFacturacion = new PdfPTable(1);
            tablaFacturacion.setWidthPercentage(100);

            PdfPCell celdaFacturacion = new PdfPCell(new Phrase("Dirección de facturación:", boldFont));
            celdaFacturacion.setBackgroundColor(Color.LIGHT_GRAY);
            celdaFacturacion.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaFacturacion.addCell(celdaFacturacion);

            celdaFacturacion = new PdfPCell(new Phrase(pedido.getDireccionCompletaFacturacion(), normalFont));
            celdaFacturacion.setBorderWidth(0);
            celdaFacturacion.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaFacturacion.addCell(celdaFacturacion);

            // Agrego las tablas al documento
            document.add(tablaEnvio);
            document.add(tablaFacturacion);

            // Cierro el documento
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al generar la factura: " + e.getMessage());
        }

        return pdfStream.toByteArray();
    }

}
