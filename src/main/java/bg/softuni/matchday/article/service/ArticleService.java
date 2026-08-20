package bg.softuni.matchday.article.service;

import bg.softuni.matchday.article.client.ArticleClient;
import bg.softuni.matchday.article.model.Article;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.web.dto.AddArticleRequest;
import bg.softuni.matchday.web.dto.ArticleResponse;
import bg.softuni.matchday.web.dto.CreateArticleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ArticleService {

    private final ArticleClient articleClient;

    @Autowired
    public ArticleService(ArticleClient articleClient) {
        this.articleClient = articleClient;
    }

    @CacheEvict(value = "articles", allEntries = true)
    public void addArticle(AddArticleRequest addArticleRequest, User user) {

        CreateArticleRequest request = new CreateArticleRequest(
                addArticleRequest.getTitle(),
                addArticleRequest.getImage(),
                addArticleRequest.getContent(),
                user.getFirstName() + " " + user.getLastName()
        );

        articleClient.addArticle(request);
    }

    @Cacheable(value = "articles")
    public List<Article> getAllArticles() {

        return articleClient.getAllArticles()
                .stream()
                .map(this::mapToArticle)
                .sorted(Comparator.comparing(Article::getPublishDate).reversed())
                .toList();
    }

    public List<Article> getLimitedArticles() {

        List<Article> articles = getAllArticles();

        if (articles.size() > 4) {
            return articles.subList(0, 4);
        }

        return articles;
    }

    @Cacheable(value = "articlesById", key = "#id")
    public Article getById(UUID id) {

        ArticleResponse response = articleClient.getArticleById(id);

        return mapToArticle(response);
    }

    @Caching(evict = {@CacheEvict(value = "articles", allEntries = true), @CacheEvict(value = "articlesById", key = "#id")})
    public void deleteArticle(UUID id) {

        articleClient.deleteArticle(id);
    }

    private Article mapToArticle(ArticleResponse response) {

        Article article = new Article();

        article.setId(response.getId());
        article.setTitle(response.getTitle());
        article.setPicture(response.getPicture());
        article.setContent(response.getContent());
        article.setAuthor(response.getAuthor());
        article.setPublishDate(response.getPublishDate());

        return article;
    }
}
