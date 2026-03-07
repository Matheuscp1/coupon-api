    package org.coupon.api.domain;

    import jakarta.persistence.Entity;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;
    import jakarta.persistence.Table;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.coupon.api.exception.BusinessException;
    import org.hibernate.annotations.Where;

    import java.math.BigDecimal;
    import java.time.LocalDate;
    import java.util.UUID;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    @Table(name = "coupons")
    @Where(clause = "deleted = false")
    public class Coupon {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        private String code;

        private String description;

        private BigDecimal discountValue;

        private LocalDate expirationDate;

        private boolean published;

        private boolean deleted = false;

        public static Coupon create(
                String code,
                String description,
                BigDecimal discountValue,
                LocalDate expirationDate,
                boolean published
        ) {

            Coupon coupon = Coupon.builder()
                    .code(Coupon.sanitizeCode(code))
                    .description(description)
                    .discountValue(discountValue)
                    .expirationDate(expirationDate)
                    .published(published)
                    .build();
            coupon.validateDiscountValue();
            coupon.validateExpirationDate();

            return coupon;
        }

        public void delete() {

            if(this.deleted){
                throw new BusinessException("Coupon already deleted");
            }

            this.deleted = true;
        }

        private void validateDiscountValue() {
            if (this.discountValue.compareTo(BigDecimal.valueOf(0.5)) < 0) {
                throw new BusinessException("Discount must be at least 0.5");
            }
        }

        private void validateExpirationDate() {
            if (this.expirationDate.isBefore(LocalDate.now())) {
                throw new BusinessException("Expiration date cannot be in the past");
            }
        }

        private static String sanitizeCode(String code) {
            String sanitized = code.replaceAll("[^a-zA-Z0-9]", "");
            if (sanitized.length() != 6) {
                throw new BusinessException("Coupon code must have 6 characters after sanitization");
            }
            return sanitized;
        }

    }