package shop.chaekmate.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.chaekmate.api.common.dto.CommonResponse;
import shop.chaekmate.api.coupon.dto.response.BooksGetResponse;
import shop.chaekmate.api.coupon.dto.response.CategoriesGetResponse;

import java.util.List;

@FeignClient(name = "chaekmate-core")
public interface CoreApiClient {
    @GetMapping("/categories/bulk")
    CommonResponse<List<List<CategoriesGetResponse>>> getCategoriesWithParents(@RequestParam List<Long> categoryIds);

    @GetMapping("/books/bulk")
    CommonResponse<List<BooksGetResponse>> getBooksIds(@RequestParam List<Long> bookIds);

    @GetMapping("/api/internal/members/birth-month")
    CommonResponse<List<Long>> getMemberIdsByBirthMonth(
            @RequestParam int month);
}
