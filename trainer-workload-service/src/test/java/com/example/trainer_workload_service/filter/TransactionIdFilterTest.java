package com.example.trainer_workload_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionIdFilterTest {

    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    private static final String TRANSACTION_ID_MDC_KEY = "transactionId";
    private final TransactionIdFilter transactionIdFilter =
            new TransactionIdFilter();
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldUseExistingTransactionIdFromRequest() throws Exception {
        String transactionId = "test-transaction-id";

        when(request.getHeader(TRANSACTION_ID_HEADER))
                .thenReturn(transactionId);

        transactionIdFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(response).setHeader(
                TRANSACTION_ID_HEADER,
                transactionId
        );

        verify(filterChain).doFilter(request, response);

        assertNull(MDC.get(TRANSACTION_ID_MDC_KEY));
    }

    @Test
    void shouldGenerateTransactionIdWhenHeaderIsMissing() throws Exception {
        when(request.getHeader(TRANSACTION_ID_HEADER))
                .thenReturn(null);

        transactionIdFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        var captor =
                org.mockito.ArgumentCaptor.forClass(String.class);

        verify(response).setHeader(
                eq(TRANSACTION_ID_HEADER),
                captor.capture()
        );

        String generatedTransactionId = captor.getValue();

        assertNotNull(generatedTransactionId);
        assertFalse(generatedTransactionId.isBlank());

        assertDoesNotThrow(
                () -> java.util.UUID.fromString(generatedTransactionId)
        );

        verify(filterChain).doFilter(request, response);

        assertNull(MDC.get(TRANSACTION_ID_MDC_KEY));
    }

    @Test
    void shouldGenerateTransactionIdWhenHeaderIsBlank() throws Exception {
        when(request.getHeader(TRANSACTION_ID_HEADER))
                .thenReturn("   ");

        transactionIdFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        var captor =
                org.mockito.ArgumentCaptor.forClass(String.class);

        verify(response).setHeader(
                eq(TRANSACTION_ID_HEADER),
                captor.capture()
        );

        String generatedTransactionId = captor.getValue();

        assertNotNull(generatedTransactionId);
        assertFalse(generatedTransactionId.isBlank());

        assertDoesNotThrow(
                () -> java.util.UUID.fromString(generatedTransactionId)
        );

        verify(filterChain).doFilter(request, response);

        assertNull(MDC.get(TRANSACTION_ID_MDC_KEY));
    }

    @Test
    void shouldPutTransactionIdInMdcDuringRequestProcessing() throws Exception {
        String transactionId = "test-transaction-id";

        when(request.getHeader(TRANSACTION_ID_HEADER))
                .thenReturn(transactionId);

        doAnswer(invocation -> {
            assertEquals(
                    transactionId,
                    MDC.get(TRANSACTION_ID_MDC_KEY)
            );

            return null;
        }).when(filterChain).doFilter(request, response);

        transactionIdFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(MDC.get(TRANSACTION_ID_MDC_KEY));
    }

    @Test
    void shouldRemoveTransactionIdFromMdcWhenFilterChainThrowsException()
            throws Exception {

        String transactionId = "test-transaction-id";

        when(request.getHeader(TRANSACTION_ID_HEADER))
                .thenReturn(transactionId);

        doThrow(new RuntimeException("Test exception"))
                .when(filterChain)
                .doFilter(request, response);

        assertThrows(
                RuntimeException.class,
                () -> transactionIdFilter.doFilterInternal(
                        request,
                        response,
                        filterChain
                )
        );

        assertNull(MDC.get(TRANSACTION_ID_MDC_KEY));
    }
}
