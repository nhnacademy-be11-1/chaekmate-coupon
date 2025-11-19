package shop.chaekmate.api.coupon.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import shop.chaekmate.api.coupon.validation.validator.CouponPolicyDatesValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 쿠폰 정책 날짜 검증 어노테이션
 * - CUSTOM_PERIOD일 때 날짜 필수
 * - 시작일 <= 종료일
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CouponPolicyDatesValidator.class)
public @interface ValidCouponPolicyDates {
    String message() default "쿠폰 날짜 설정이 올바르지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
