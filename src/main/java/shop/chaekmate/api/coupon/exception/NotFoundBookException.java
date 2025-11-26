package shop.chaekmate.api.coupon.exception;

import jakarta.ws.rs.NotFoundException;

public class NotFoundBookException extends NotFoundException {
    public NotFoundBookException(Long bookId) {
        super(String.format("존재하지 않는 도서입니다. bookId: %d", bookId));
    }
}
