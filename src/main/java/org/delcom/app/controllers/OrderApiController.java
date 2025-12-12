package org.delcom.app.controllers;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.dto.OrderRequestDto;
import org.delcom.app.entities.OrderStatus;
import org.delcom.app.services.OrderService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.delcom.app.entities.Order;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDto request) {
        try {
            orderService.createOrder(request);
            // ApiResponse constructor(int status, String message, Object data)
            return ResponseEntity.ok(new ApiResponse<>(200, "Pesanan berhasil dibuat", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PostMapping("/update-status")
    public ResponseEntity<?> updateStatus(@RequestParam Long orderId, 
                                          @RequestParam OrderStatus status,
                                          HttpServletRequest request) { // Inject Request
        try {
            orderService.updateOrderStatus(orderId, status);
            
            // LOGIKA SMART REDIRECT
            // Ambil URL halaman sebelumnya (Referer)
            String referer = request.getHeader("Referer");
            
            // Jika tidak ada referer (langsung akses), kembalikan ke dashboard
            String redirectUrl = (referer != null) ? referer : "/dashboard";

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, redirectUrl)
                    .build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportToExcel() throws IOException {
        List<Order> orders = orderService.getAllOrders();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Laporan Transaksi");

            // STYLE HEADER (Bold, Background Biru, Text Putih)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // STYLE UANG (Format Rupiah)
            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("Rp #,##0"));

            // HEADER ROW
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID Order", "Nama Pelanggan", "Tanggal", "Total Harga", "Status"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // DATA ROWS
            int rowIdx = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowIdx++);
                
                row.createCell(0).setCellValue(order.getId());
                row.createCell(1).setCellValue(order.getCustomerName());
                row.createCell(2).setCellValue(order.getOrderDate().toString());
                
                Cell totalCell = row.createCell(3);
                totalCell.setCellValue(order.getTotalAmount().doubleValue());
                totalCell.setCellStyle(currencyStyle); // Apply format uang
                
                row.createCell(4).setCellValue(order.getStatus().name());
            }

            // AUTO SIZE COLUMNS
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=laporan_cantik.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        }
    }
}