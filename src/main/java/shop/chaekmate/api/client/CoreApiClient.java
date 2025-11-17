package shop.chaekmate.api.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.chaekmate.api.coupon.dto.response.BooksGetResponse;
import shop.chaekmate.api.coupon.dto.response.CategoriesGetResponse;

// TODO: 코어 서버 이름 정해지면 변경 고려
@FeignClient(name = "core-api")
public interface CoreApiClient {
    @GetMapping("/categories/bulk")
    List<List<CategoriesGetResponse>> getCategoriesWithParents(@RequestParam List<Long> categoryIds);

    @GetMapping("/books/bulk")
    List<BooksGetResponse> getBooksIds(@RequestParam List<Long> bookIds);
}
