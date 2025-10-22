package shop.chaekmate.api.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.chaekmate.api.coupon.dto.response.BooksGetResponse;
import shop.chaekmate.api.coupon.dto.response.CategoriesGetResponse;

@FeignClient(
        name = "coreClient",
        url = "${core.service.url}"
)
public interface CoreClient {

    @GetMapping("/categories")
    List<CategoriesGetResponse> getFullCategoriesById(@RequestParam List<Long> categoryIds);

    @GetMapping("/books")
    List<BooksGetResponse> getFullBooksById(@RequestParam List<Long> bookIds);
}
