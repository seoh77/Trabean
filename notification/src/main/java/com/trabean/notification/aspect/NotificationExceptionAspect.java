package com.trabean.notification.aspect;

import com.trabean.notification.exception.NotificationSaveException;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NotificationExceptionAspect {

    @AfterThrowing(
            pointcut = "execution(* com.trabean.notification.db.repository.NotificationRepository.save(..))")
    /*
    * (..)는 AspectJ에서 사용되는 포인트컷 표현식의 일부로, 메서드의 매개변수를 의미합니다. 이 표현식은 다음과 같은 의미를 가집니다:

        .. : 0개 이상의 인자를 받을 수 있음을 나타냅니다. 즉, 메서드가 어떤 개수의 인자를 받든지 상관없이 포인트컷에 매칭됩니다.
    * */
    public void handleDataAccessException() {
        throw NotificationSaveException.instance;
    }
}
