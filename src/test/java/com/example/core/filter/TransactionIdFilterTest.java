package com.example.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionIdFilterTest {

    private static final String TRANSACTION_ID = "transactionId";
    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private TransactionIdFilter transactionIdFilter;

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldUseExistingTransactionId() throws Exception {
        String transactionId = "test-transaction-id";

        when(request.getHeader(TRANSACTION_ID_HEADER))
                .thenReturn(transactionId);

        doAnswer(invocation -> {
            assertEquals(
                    transactionId,
                    MDC.get(TRANSACTION_ID),
                    "Transaction id should be stored in MDC during request processing"
            );

            return null;
        }).when(filterChain).doFilter(request, response);

        transactionIdFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(response).setHeader(
                TRANSACTION_ID_HEADER,
                transactionId
        );

        verify(filterChain)
                .doFilter(request, response);

        assertNull(
                MDC.get(TRANSACTION_ID),
                "Transaction id should be removed from MDC after request processing"
        );
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

        var transactionIdCaptor =
                org.mockito.ArgumentCaptor.forClass(String.class);

        verify(response).setHeader(
                eq(TRANSACTION_ID_HEADER),
                transactionIdCaptor.capture()
        );

        String generatedTransactionId =
                transactionIdCaptor.getValue();

        assertNotNull(
                generatedTransactionId,
                "Generated transaction id should not be null"
        );

        assertFalse(
                generatedTransactionId.isBlank(),
                "Generated transaction id should not be blank"
        );

        assertDoesNotThrow(
                () -> UUID.fromString(generatedTransactionId),
                "Generated transaction id should be a valid UUID"
        );

        verify(filterChain)
                .doFilter(request, response);

        assertNull(
                MDC.get(TRANSACTION_ID),
                "Transaction id should be removed from MDC after request processing"
        );
    }

    @Test
    void shouldGenerateTransactionIdWhenHeaderIsBlank() throws Exception {
        when(request.getHeader(TRANSACTION_ID_HEADER))
                .thenReturn(" ");

        transactionIdFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        var transactionIdCaptor =
                org.mockito.ArgumentCaptor.forClass(String.class);

        verify(response).setHeader(
                eq(TRANSACTION_ID_HEADER),
                transactionIdCaptor.capture()
        );

        String generatedTransactionId =
                transactionIdCaptor.getValue();

        assertNotNull(
                generatedTransactionId,
                "Generated transaction id should not be null"
        );

        assertFalse(
                generatedTransactionId.isBlank(),
                "Generated transaction id should not be blank"
        );

        assertDoesNotThrow(
                () -> UUID.fromString(generatedTransactionId),
                "Generated transaction id should be a valid UUID"
        );

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void shouldRemoveTransactionIdWhenFilterChainThrowsException() throws Exception {
        String transactionId = "test-transaction-id";

        when(request.getHeader(TRANSACTION_ID_HEADER))
                .thenReturn(transactionId);

        doThrow(new RuntimeException("Test exception"))
                .when(filterChain)
                .doFilter(request, response);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> transactionIdFilter.doFilterInternal(
                        request,
                        response,
                        filterChain
                ),
                "RuntimeException should be propagated from filter chain"
        );

        assertEquals(
                "Test exception",
                exception.getMessage(),
                "Exception message should match"
        );

        assertNull(
                MDC.get(TRANSACTION_ID),
                "Transaction id should be removed from MDC when filter chain throws exception"
        );

        verify(response).setHeader(
                TRANSACTION_ID_HEADER,
                transactionId
        );

        verify(filterChain)
                .doFilter(request, response);
    }
}
