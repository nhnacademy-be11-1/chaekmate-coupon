package shop.chaekmate.api.coupon.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import shop.chaekmate.api.coupon.entity.type.CouponAppliedPeriodType;
import shop.chaekmate.api.coupon.validation.CouponPolicyDateValidatable;
import shop.chaekmate.api.coupon.validation.annotation.ValidCouponPolicyDates;

public class CouponPolicyDatesValidator implements ConstraintValidator<ValidCouponPolicyDates, CouponPolicyDateValidatable> {

    @Override
    public boolean isValid(CouponPolicyDateValidatable request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        // CUSTOM_PERIOD일 때만 날짜 검증
        if (request.appliedPeriodType() == CouponAppliedPeriodType.CUSTOM_PERIOD) {
            return validateCustomPeriod(request, context);
        }

        // THIRTY_DAYS, MONTH_LAST_DAY는 날짜가 선택적이므로 항상 true
        return true;
    }

    private boolean validateCustomPeriod(CouponPolicyDateValidatable request, ConstraintValidatorContext context) {
        if (request.appliedStartedAt() == null || request.appliedExpiredAt() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "CUSTOM_PERIOD 타입은 시작일과 종료일이 필수입니다."
            ).addConstraintViolation();
            return false;
        }

        if (request.appliedStartedAt().isAfter(request.appliedExpiredAt())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "시작일은 종료일보다 이전이어야 합니다."
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
