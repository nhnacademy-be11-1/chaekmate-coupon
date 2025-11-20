package shop.chaekmate.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.chaekmate.api.coupon.dto.response.BooksGetResponse;
import shop.chaekmate.api.coupon.dto.response.CategoriesGetResponse;

import java.util.List;

@FeignClient(name = "chaekmate-core")
public interface CoreApiClient {
    @GetMapping("/categories/bulk")
    List<List<CategoriesGetResponse>> getCategoriesWithParents(@RequestParam List<Long> categoryIds);

    @GetMapping("/books/bulk")
    List<BooksGetResponse> getBooksIds(@RequestParam List<Long> bookIds);
}
