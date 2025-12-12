package org.delcom.app.services;

import org.delcom.app.entities.OrderStatus;
import org.delcom.app.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------------------------------------------
    // TEST: getSummaryData()
    // ---------------------------------------------------------
    @Test
    @DisplayName("getSummaryData() harus mengembalikan data ringkasan dengan benar")
    void testGetSummaryData() {
        // Mock total revenue
        when(orderRepository.sumTotalAmountByStatus(OrderStatus.DONE))
                .thenReturn(new BigDecimal("1500000"));

        // Mock counts per status
        when(orderRepository.countByStatus(OrderStatus.NEW)).thenReturn(5L);
        when(orderRepository.countByStatus(OrderStatus.PROCESSING)).thenReturn(3L);
        when(orderRepository.countByStatus(OrderStatus.DONE)).thenReturn(10L);
        when(orderRepository.countByStatus(OrderStatus.SHIPPED)).thenReturn(7L);

        // Mock total count
        when(orderRepository.count()).thenReturn(25L);

        Map<String, Object> result = dashboardService.getSummaryData();

        assertEquals(new BigDecimal("1500000"), result.get("totalRevenue"));
        assertEquals(5L, result.get("countNew"));
        assertEquals(3L, result.get("countProcessing"));
        assertEquals(10L, result.get("countDone"));
        assertEquals(7L, result.get("countShipped"));
        assertEquals(25L, result.get("totalOrders"));

        verify(orderRepository).sumTotalAmountByStatus(OrderStatus.DONE);
        verify(orderRepository).countByStatus(OrderStatus.NEW);
        verify(orderRepository).countByStatus(OrderStatus.PROCESSING);
        verify(orderRepository).countByStatus(OrderStatus.DONE);
        verify(orderRepository).countByStatus(OrderStatus.SHIPPED);
        verify(orderRepository).count();
    }

    // ---------------------------------------------------------
    // TEST: Null safety di getSummaryData()
    // ---------------------------------------------------------
    @Test
    @DisplayName("getSummaryData() harus aman meski repository mengembalikan null")
    void testGetSummaryDataNullSafety() {
        when(orderRepository.sumTotalAmountByStatus(OrderStatus.DONE)).thenReturn(null);
        when(orderRepository.countByStatus(any())).thenReturn(null);
        when(orderRepository.count()).thenReturn(0L);

        Map<String, Object> result = dashboardService.getSummaryData();

        assertEquals(BigDecimal.ZERO, result.get("totalRevenue"));
        assertEquals(0L, result.get("countNew"));
        assertEquals(0L, result.get("countProcessing"));
        assertEquals(0L, result.get("countDone"));
        assertEquals(0L, result.get("countShipped"));
        assertEquals(0L, result.get("totalOrders"));
    }

    // ---------------------------------------------------------
    // TEST: getOrderStatusChartData()
    // ---------------------------------------------------------
    @Test
    @DisplayName("getOrderStatusChartData() harus mengembalikan labels dan data dengan benar")
    void testGetOrderStatusChartData() {
        when(orderRepository.countByStatus(OrderStatus.NEW)).thenReturn(4L);
        when(orderRepository.countByStatus(OrderStatus.PROCESSING)).thenReturn(8L);
        when(orderRepository.countByStatus(OrderStatus.SHIPPED)).thenReturn(2L);
        when(orderRepository.countByStatus(OrderStatus.DONE)).thenReturn(12L);

        Map<String, Object> result = dashboardService.getOrderStatusChartData();

        String[] labels = (String[]) result.get("labels");
        long[] data = (long[]) result.get("data");

        assertArrayEquals(new String[]{"New", "Processing", "Shipped", "Done"}, labels);
        assertArrayEquals(new long[]{4L, 8L, 2L, 12L}, data);

        verify(orderRepository).countByStatus(OrderStatus.NEW);
        verify(orderRepository).countByStatus(OrderStatus.PROCESSING);
        verify(orderRepository).countByStatus(OrderStatus.SHIPPED);
        verify(orderRepository).countByStatus(OrderStatus.DONE);
    }

    // ---------------------------------------------------------
    // TEST: Null safety untuk chart
    // ---------------------------------------------------------
    @Test
    @DisplayName("getOrderStatusChartData() harus aman meski countByStatus() mengembalikan null")
    void testGetOrderStatusChartNullSafety() {
        when(orderRepository.countByStatus(any())).thenReturn(null);

        Map<String, Object> result = dashboardService.getOrderStatusChartData();
        long[] data = (long[]) result.get("data");

        assertArrayEquals(new long[]{0, 0, 0, 0}, data);
    }
}
