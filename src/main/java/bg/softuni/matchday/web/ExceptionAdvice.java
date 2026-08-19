package bg.softuni.matchday.web;

import bg.softuni.matchday.exception.UserDoesNotExistException;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(UserDoesNotExistException.class)
    public String handleUserDoesNotExistException(RedirectAttributes redirectAttributes){

        redirectAttributes.addFlashAttribute("userNotFoundMessage", "User doesn't exist");
        return "redirect:/login";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            AccessDeniedException.class,
            NoResourceFoundException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ModelAndView handleNotFoundExceptions(){

        return new ModelAndView("not-found");
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException(Exception exception) {

        exception.printStackTrace();

        return new ModelAndView("internal-server-error");
    }

    @GetMapping("/not-found")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound() {
        return "not-found";
    }
}
