package bg.softuni.matchday.article;

import bg.softuni.matchday.article.model.Article;
import bg.softuni.matchday.article.repository.ArticleRepository;
import bg.softuni.matchday.article.service.ArticleService;
import bg.softuni.matchday.user.model.User;
import bg.softuni.matchday.web.dto.AddArticleRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceUTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService articleService;

    @Test
    void addArticle_savesCorrectArticle() {
        AddArticleRequest request = new AddArticleRequest();
        request.setTitle("My Title");
        request.setContent("Article content");
        request.setImage("image.jpg");

        User user = new User();
        user.setFirstName("Nikola");
        user.setLastName("Penkov");

        articleService.addArticle(request, user);

        ArgumentCaptor<Article> articleCaptor = ArgumentCaptor.forClass(Article.class);

        verify(articleRepository).save(articleCaptor.capture());

        Article savedArticle = articleCaptor.getValue();

        assertEquals("My Title", savedArticle.getTitle());
        assertEquals("Nikola Penkov", savedArticle.getAuthor());
        assertEquals(LocalDate.now(), savedArticle.getPublishDate());
        assertEquals("Article content", savedArticle.getContent());
        assertEquals("image.jpg", savedArticle.getPicture());
    }

    @Test
    void getAllArticles_returnsArticlesSortedByDate() {
        Article article1 = new Article();
        article1.setTitle("Old");
        article1.setPublishDate(LocalDate.of(2024, 1, 1));

        Article article2 = new Article();
        article2.setTitle("Newest");
        article2.setPublishDate(LocalDate.of(2024, 3, 1));

        Article article3 = new Article();
        article3.setTitle("Middle");
        article3.setPublishDate(LocalDate.of(2024, 2, 1));

        when(articleRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(article1, article2, article3)));

        List<Article> result = articleService.getAllArticles();

        verify(articleRepository).findAll();

        assertEquals(article2, result.get(0));
        assertEquals(article3, result.get(1));
        assertEquals(article1, result.get(2));
    }

    @Test
    void getLimitedArticles_returnsTopFourArticlesSortedByDate() {
        Article oldest = new Article();
        oldest.setPublishDate(LocalDate.of(2020, 1, 1));

        Article older = new Article();
        older.setPublishDate(LocalDate.of(2021, 1, 1));

        Article old = new Article();
        old.setPublishDate(LocalDate.of(2022, 1, 1));

        Article middle = new Article();
        middle.setPublishDate(LocalDate.of(2023, 2, 1));

        Article newer = new Article();
        newer.setPublishDate(LocalDate.of(2024, 3, 1));

        Article newest = new Article();
        newest.setPublishDate(LocalDate.of(2025, 3, 1));

        Article latest = new Article();
        latest.setPublishDate(LocalDate.of(2026, 3, 1));

        when(articleRepository.findAll()).thenReturn(
                new ArrayList<>(List.of(
                        oldest, older, old, middle,
                        newer, newest, latest
                ))
        );

        List<Article> result = articleService.getLimitedArticles();

        verify(articleRepository).findAll();

        assertEquals(4, result.size());

        assertEquals(latest, result.get(0));
        assertEquals(newest, result.get(1));
        assertEquals(newer, result.get(2));
        assertEquals(middle, result.get(3));
    }

    @Test
    void getLimitedArticles_returnsAllArticles_whenThereAreFourOrLess() {
        Article a1 = new Article();
        a1.setPublishDate(LocalDate.of(2024, 1, 1));

        Article a2 = new Article();
        a2.setPublishDate(LocalDate.of(2023, 1, 1));

        Article a3 = new Article();
        a3.setPublishDate(LocalDate.of(2022, 1, 1));

        when(articleRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(a3, a1, a2)));

        List<Article> result = articleService.getLimitedArticles();

        assertEquals(3, result.size());
        assertEquals(a1, result.get(0));
        assertEquals(a2, result.get(1));
        assertEquals(a3, result.get(2));

        verify(articleRepository).findAll();
    }

    @Test
    void getById_returnsRightUser(){
        UUID uuid = UUID.randomUUID();

        Article article = new Article();
        article.setId(uuid);

        when(articleRepository.findById(uuid))
                .thenReturn(Optional.of(article));

        Article result = articleService.getById(uuid);

        assertEquals(article, result);

    }

    @Test
    void deleteById_deletesArticle() {
        UUID uuid = UUID.randomUUID();

        Article article = new Article();
        article.setId(uuid);

        when(articleRepository.findById(uuid))
                .thenReturn(Optional.of(article));

        articleService.deleteArticle(uuid);

        verify(articleRepository).delete(article);
    }
}
