package bg.softuni.matchday.article.service;

import bg.softuni.matchday.article.model.Article;
import bg.softuni.matchday.article.repository.ArticleRepository;
import bg.softuni.matchday.game.model.Game;
import bg.softuni.matchday.game.repository.GameRepository;
import bg.softuni.matchday.team.model.Team;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.web.dto.AddArticleRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public void addArticle(AddArticleRequest addArticleRequest, User user) {
        Article article = new Article();
        article.setTitle(addArticleRequest.getTitle());
        article.setAuthor(user.getFirstName() + ' ' + user.getLastName());
        article.setPublishDate(LocalDate.now());
        article.setContent(addArticleRequest.getContent());
        article.setPicture(addArticleRequest.getImage());

        articleRepository.save(article);
    }

    public List<Article> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        articles.sort(Comparator.comparing(Article::getPublishDate).reversed());
        return articles;
    }

    public List<Article> getLimitedArticles() {
        if(getAllArticles().size() > 4){
            return getAllArticles().subList(0, 4);
        }

        return getAllArticles();
    }

    public Article getById(UUID id) {
        return articleRepository.findById(id).orElseThrow(() -> new RuntimeException("Article not found with id: " + id));
    }

    public void deleteArticle(UUID id) {
        articleRepository.delete(getById(id));
    }
}
