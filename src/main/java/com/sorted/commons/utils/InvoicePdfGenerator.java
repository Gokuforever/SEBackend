package com.sorted.commons.utils;

import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sorted.commons.beans.InvoiceItem;
import com.sorted.commons.entity.mongo.Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

public class InvoicePdfGenerator {

    private static final Logger logger = LoggerFactory.getLogger(InvoicePdfGenerator.class);
    private static final Color HEADER_COLOR = new Color(41, 128, 185);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static byte[] generateInvoicePdf(Invoice invoice) throws DocumentException {

        // Then generate the PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        document.open();

        // Set fonts
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, HEADER_COLOR);

        // Add header
        addHeader(document, titleFont);

        // Add invoice details
        addInvoiceDetails(document, invoice, boldFont, regularFont);

        // Add seller and buyer info
        addSellerBuyerInfo(document, invoice, boldFont, regularFont);

        // Add items table
        addItemsTable(document, invoice, boldFont, regularFont);

        // Add totals
        addTotals(document, invoice, boldFont, regularFont);

        // Add payment info
        addPaymentInfo(document, invoice, boldFont, regularFont);

        // Add footer
        addFooter(document, regularFont);

        document.close();
        return baos.toByteArray();
    }

    // Your existing PDF methods remain the same...
    private static void addHeader(Document document, Font titleFont) throws DocumentException {
        Paragraph header = new Paragraph("TAX INVOICE", titleFont);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(20);
        document.add(header);
    }

    private static void addInvoiceDetails(Document document, Invoice invoice, Font boldFont, Font regularFont)
            throws DocumentException {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);

        addTableRow(table, "Invoice No:", invoice.getInvoiceId(), boldFont, regularFont);
        addTableRow(table, "Invoice Date:", invoice.getInvoiceDate().format(DATE_FORMATTER), boldFont, regularFont);

        document.add(table);
    }

    private static void addSellerBuyerInfo(Document document, Invoice invoice, Font boldFont, Font regularFont)
            throws DocumentException {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);

        // Seller cell
        PdfPCell sellerCell = new PdfPCell();
        sellerCell.setPadding(10);
        sellerCell.setBorder(Rectangle.BOX);

        Paragraph sellerHeader = new Paragraph("SOLD BY:", boldFont);
        sellerCell.addElement(sellerHeader);
        sellerCell.addElement(new Paragraph(invoice.getSeller().getName(), regularFont));
        sellerCell.addElement(new Paragraph(invoice.getSeller().getAddress(), regularFont));
        sellerCell.addElement(new Paragraph("Phone: " + invoice.getSeller().getPhoneNo(), regularFont));
        sellerCell.addElement(new Paragraph("GST No: " + invoice.getSeller().getGstNo(), regularFont));
        sellerCell.addElement(new Paragraph("Seller ID: " + invoice.getSeller().getSellerId(), regularFont));

        // Buyer cell
        PdfPCell buyerCell = new PdfPCell();
        buyerCell.setPadding(10);
        buyerCell.setBorder(Rectangle.BOX);

        Paragraph buyerHeader = new Paragraph("BILLING ADDRESS:", boldFont);
        buyerCell.addElement(buyerHeader);
        buyerCell.addElement(new Paragraph(invoice.getBuyer().getName(), regularFont));
        buyerCell.addElement(new Paragraph(invoice.getBuyer().getAddress(), regularFont));
        buyerCell.addElement(new Paragraph("Email: " + invoice.getBuyer().getEmail(), regularFont));

        table.addCell(sellerCell);
        table.addCell(buyerCell);

        document.add(table);
    }

    private static void addItemsTable(Document document, Invoice invoice, Font boldFont, Font regularFont)
            throws DocumentException {

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);

        // Set column widths
        float[] columnWidths = {1f, 3f, 1.5f, 1f, 2f, 2f};
        table.setWidths(columnWidths);

        // Header row
        addHeaderCell(table, "S.No", boldFont);
        addHeaderCell(table, "Product Name", boldFont);
        addHeaderCell(table, "HSN Code", boldFont);
        addHeaderCell(table, "Qty", boldFont);
        addHeaderCell(table, "Unit Price", boldFont);
        addHeaderCell(table, "Total Price", boldFont);

        // Data rows
        int serialNo = 1;
        for (InvoiceItem item : invoice.getItems()) {
            addDataCell(table, String.valueOf(serialNo++), regularFont);
            addDataCell(table, item.getProductName(), regularFont);
            addDataCell(table, item.getHsnCode(), regularFont);
            addDataCell(table, String.valueOf(item.getQuantity()), regularFont);
            addDataCell(table, "₹" + item.getUnitPrice().toString(), regularFont);
            addDataCell(table, "₹" + item.getTotalPrice().toString(), regularFont);
        }

        document.add(table);
    }

    private static void addTotals(Document document, Invoice invoice, Font boldFont, Font regularFont)
            throws DocumentException {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setSpacingAfter(20);

        addTableRow(table, "Sub Total:", "₹" + invoice.getTotalAmount().toString(), boldFont, regularFont);
        addTableRow(table, "GST:", "₹" + invoice.getTotalGstAmount().toString(), boldFont, regularFont);

        // Total row with background
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL AMOUNT:", boldFont));
        totalLabelCell.setBackgroundColor(LIGHT_GRAY);
        totalLabelCell.setPadding(5);

        PdfPCell totalValueCell = new PdfPCell(new Phrase("₹" + invoice.getTotalNetAmount().toString(), boldFont));
        totalValueCell.setBackgroundColor(LIGHT_GRAY);
        totalValueCell.setPadding(5);

        table.addCell(totalLabelCell);
        table.addCell(totalValueCell);

        document.add(table);

        // Amount in words
        Paragraph amountInWords = new Paragraph("Amount in Words: " + invoice.getTotalAmountInWords(), boldFont);
        amountInWords.setSpacingBefore(10);
        amountInWords.setSpacingAfter(20);
        document.add(amountInWords);
    }

    private static void addPaymentInfo(Document document, Invoice invoice, Font boldFont, Font regularFont)
            throws DocumentException {

        Paragraph paymentHeader = new Paragraph("PAYMENT INFORMATION", boldFont);
        paymentHeader.setSpacingAfter(10);
        document.add(paymentHeader);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(60);
        table.setSpacingAfter(20);

        addTableRow(table, "Payment Method:", invoice.getPaymentInfo().getPaymentMethod(), boldFont, regularFont);
        addTableRow(table, "Transaction ID:", invoice.getPaymentInfo().getTransactionId(), boldFont, regularFont);
        addTableRow(table, "Payment Date:", invoice.getPaymentInfo().getPaymentDate().format(DATE_FORMATTER), boldFont, regularFont);

        document.add(table);
    }

    private static void addFooter(Document document, Font regularFont) throws DocumentException {
        Paragraph footer = new Paragraph("This is a computer generated invoice and does not require signature.", regularFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);
    }

    private static void addTableRow(PdfPTable table, String label, String value, Font boldFont, Font regularFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, boldFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, regularFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private static void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(HEADER_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        table.addCell(cell);
    }

    private static void addDataCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
}
