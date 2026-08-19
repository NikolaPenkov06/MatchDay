package bg.softuni.matchday.article.client;

import bg.softuni.matchday.web.dto.AddArticleRequest;
import bg.softuni.matchday.web.dto.ArticleResponse;
import bg.softuni.matchday.web.dto.CreateArticleRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "matchday-service-article", url = "http://localhost:8081/api/articles")
public interface ArticleClient {

    @GetMapping
    List<ArticleResponse> getAllArticles();

    @GetMapping("/{id}")
    ArticleResponse getArticleById(@PathVariable UUID id);

    @PostMapping
    void addArticle(@RequestBody CreateArticleRequest request);

    @DeleteMapping("/{id}")
    void deleteArticle(@PathVariable UUID id);
}