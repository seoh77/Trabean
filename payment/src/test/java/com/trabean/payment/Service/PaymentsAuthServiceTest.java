package com.trabean.payment.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import com.trabean.payment.dto.response.PaymentsAuthResponse;
import com.trabean.payment.dto.response.UserRoleResponse;
import com.trabean.payment.entity.Payments;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.repository.PaymentsRepository;
import com.trabean.payment.service.PaymentsAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

class PaymentsAuthServiceTest {

    @Mock
    private PaymentsRepository paymentsRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentsAuthService paymentsAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("유저가 Payer 일 때 성공")
    void processPayment_Success() {
        // given: Builder 패턴을 사용하여 UserRoleResponse 객체 생성
        UserRoleResponse mockUserRoleResponse = UserRoleResponse.builder()
                .status("200")
                .message("Success")
                .userId(1L)
                .userRole("payer")
                .build();

        Payments mockPayment = Payments.createInitialPayment(1L, 1L);

        // when: RestTemplate과 PaymentsRepository의 동작을 모킹
        when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), eq(UserRoleResponse.class)))
                .thenReturn(mockUserRoleResponse);
        when(paymentsRepository.save(any(Payments.class)))
                .thenReturn(mockPayment);

        // when: 서비스 호출
        PaymentsAuthResponse response = paymentsAuthService.processPayment("testUserKey", 1L);

        // then: 결제 응답 검증
        assertNotNull(response);
        assertEquals(mockPayment.getTransactionId(), response.getTransactionId());
        assertEquals(mockPayment.getPayId(), response.getPayId());
    }

    @Test
    @DisplayName("유저가 nonePayer일 때 예외 발생")
    void processPayment_UserRoleNonePayer_ThrowsException() {
        // given: Builder 패턴을 사용하여 UserRoleResponse 객체 생성
        UserRoleResponse mockUserRoleResponse = UserRoleResponse.builder()
                .status("200")
                .message("Success")
                .userId(1L)
                .userRole("nonePayer")
                .build();

        // when: RestTemplate 동작 모킹
        when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), eq(UserRoleResponse.class)))
                .thenReturn(mockUserRoleResponse);

        // then: 예외가 발생해야 함
        PaymentsException exception = assertThrows(PaymentsException.class, () ->
                paymentsAuthService.processPayment("testUserKey", 1L)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("권한이 없는 사용자입니다.", exception.getMessage());
    }
}
