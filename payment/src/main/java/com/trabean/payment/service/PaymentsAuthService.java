package com.trabean.payment.service;

import com.trabean.payment.client.AccountClient;
import com.trabean.payment.dto.request.ValidatePasswordRequest;
import com.trabean.payment.dto.response.UserRoleResponse;
import com.trabean.payment.entity.Payments;
import com.trabean.payment.enums.UserRole;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.interceptor.UserHeaderInterceptor;
import com.trabean.payment.repository.PaymentsRepository;
import feign.FeignException.NotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentsAuthService {

    private final AccountClient accountClient;
    private final PaymentsRepository paymentsRepository;

    public void checkAuthPayment(Long userId, Long accountId) {
        // 유저 권한 확인 API 호출
        String requestBody = String.format("{\"userId\":\"%d\", \"accountId\":%d}", userId, accountId);

        // API 호출
        UserRoleResponse userRoleResponse = accountClient.getUserRole(requestBody);

        // 유저 권한 확인 후 처리
        if (userRoleResponse == null) {
            throw new PaymentsException("유저 결제 권한을 받아오지 못 했습니다.", HttpStatus.BAD_REQUEST);
        }
        if (userRoleResponse.getUserRole() == UserRole.NONE_PAYER) {
            throw new PaymentsException("권한이 없는 사용자입니다.", HttpStatus.FORBIDDEN);
        }

    }

    public String checkAccountPassword(ValidatePasswordRequest request) {
        log.info("유저 아이디 가져오기" + UserHeaderInterceptor.userId.get());
        String requestBody = String.format("{\"userId\":\"%d\", \"accountId\":%d, \"password\":\"%s\"}",
                UserHeaderInterceptor.userId.get(), request.getAccountId(), request.getPassword());
        log.info("@@@@@@@@@ payID 가져오기: " + request.getPayId());
        try {
            accountClient.validateAccountPassword(requestBody);
            Payments payment = paymentsRepository.findById(request.getPayId())
                    .orElseThrow(() -> new PaymentsException("결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
            // 트랜잭션 id 발급
            return payment.getTransactionId();
        } catch (PaymentsException e) {
            if (request.getPayId() == null) {
                throw new PaymentsException("payID 를 가져오는데 실패했습니다.", HttpStatus.BAD_REQUEST);
            }

            if (UserHeaderInterceptor.userId.get() == null) {
                throw new PaymentsException("유저 ID를 가져오는 데 실패했습니다.", HttpStatus.BAD_GATEWAY);
            }
            Payments payment = paymentsRepository.findById(request.getPayId())
                    .orElseThrow(() -> new PaymentsException("결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
            payment.updateErrorCount();
            paymentsRepository.save(payment);
            throw new PaymentsException("비밀번호를 잘못 입력하셨습니다. 현재 에러 횟수: " + payment.getPasswordErrorCount() + " / 5",
                    HttpStatus.UNAUTHORIZED);
        } catch (NotFound e) {
            throw new PaymentsException("해당 계좌를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }
}
